package com.core.back9.service;

import com.core.back9.dto.ContractDTO;
import com.core.back9.dto.MemberDTO;
import com.core.back9.entity.Contract;
import com.core.back9.entity.Room;
import com.core.back9.entity.Tenant;
import com.core.back9.entity.constant.ContractStatus;
import com.core.back9.entity.constant.ContractType;
import com.core.back9.entity.constant.Role;
import com.core.back9.entity.constant.Status;
import com.core.back9.exception.ApiErrorCode;
import com.core.back9.exception.ApiException;
import com.core.back9.mapper.ContractMapper;
import com.core.back9.repository.ContractRepository;
import com.core.back9.repository.RoomRepository;
import com.core.back9.repository.TenantRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Transactional
@Service
public class ContractService {

    private final TenantRepository tenantRepository;
    private final RoomRepository roomRepository;
    private final ContractRepository contractRepository;
    private final ContractMapper contractMapper;

    public ContractDTO.RegisterResponse registerContract(
            MemberDTO.Info member,
            Long buildingId,
            Long roomId,
            Long tenantId,
            ContractDTO.RegisterRequest request
    ) {

        Room room = validateOwnerAndRoomExistence(member, buildingId, roomId);

        // 상위 데이터 유효성 검증
        Tenant tenant = tenantRepository.getValidOneTenantOrThrow(tenantId);

        // 공실 유무 체크(내가 계약 이행을 원하는 일자에 이미 맺어진 계약이 있는지 체크)
        List<ContractStatus> statusList = List.of(ContractStatus.PENDING, ContractStatus.COMPLETED, ContractStatus.IN_PROGRESS);

        contractRepository.findByContractDuplicate(roomId, statusList, request.getStartDate(), request.getEndDate())
                .ifPresent(contracts -> {
                    if (!contracts.isEmpty()) {
                        throw new ApiException(ApiErrorCode.ROOM_ALREADY_ASSIGNED);
                    }
                });

        Contract validContract = contractMapper.toEntity(request, tenant, room, ContractType.INITIAL); // 연결 관계 매핑
        Contract savedContract = contractRepository.save(validContract);

        Contract completedContract = savedContract.contractComplete(); // 계약 무조건 완료처리 (계약 등록과 동시에 실행되는 완료처리라 기간 검증 필요X)

        room.addContract(completedContract);

        return contractMapper.toRegisterResponse(completedContract);

    }

    public ContractDTO.Info renewContract(
            MemberDTO.Info member,
            Long buildingId,
            Long roomId,
            Long contractId,
            Long tenantId,
            ContractDTO.RenewRequest request
    ) {

        Room room = validateOwnerAndRoomExistence(member, buildingId, roomId);
        Tenant tenant = tenantRepository.getValidOneTenantOrThrow(tenantId);
        Contract contract = contractRepository.findByContractId(contractId)
                .orElseThrow(() -> new ApiException(ApiErrorCode.NOT_FOUND_VALID_CONTRACT));// 기존 계약이 존재하는지 검증

        LocalDate renewalDate = contract.getEndDate().plusDays(1); // 재계약의 시작일은 기존 계약 만료일의 다음날
        ContractDTO.RenewDto renewDto = contractMapper.toDto(request, renewalDate); // dto 추가 생성해 재시작일을 반영

        Contract validContract = contractMapper.toEntity(renewDto, tenant, room, ContractType.RENEWAL); // dto -> 엔티티 매핑
        Contract savedContract = contractRepository.save(validContract);

        Contract completedContract = savedContract.contractComplete();

        room.addContract(completedContract);

        return contractMapper.toInfo(completedContract);

    }

    @Transactional(readOnly = true)
    public ContractDTO.InfoList getAllContract(
            MemberDTO.Info member,
            Long buildingId,
            Long roomId,
            Pageable pageable
    ) {

        Room room = validateOwnerAndRoomExistence(member, buildingId, roomId);

        Page<Contract> contracts = contractRepository.selectAllRegisteredContract(room.getId(), Status.REGISTER, pageable);

        long count = contracts.getTotalElements();

        List<ContractDTO.Info> contractInfoList = contracts.stream()
                .map(contractMapper::toInfo)
                .collect(Collectors.toList());

        return contractMapper.toInfoList(count, contractInfoList);

    }

    @Transactional(readOnly = true)
    public ContractDTO.Info getOneContract(
            MemberDTO.Info member,
            Long buildingId,
            Long roomId,
            Long contractId
    ) {

        Room room = validateOwnerAndRoomExistence(member, buildingId, roomId);

        Contract contract = contractRepository.getValidOneContractOrThrow(room.getId(), contractId);

        return contractMapper.toInfo(contract);

    }

    public ContractDTO.Info modifyContract(
            MemberDTO.Info member,
            Long buildingId,
            Long roomId,
            Long contractId,
            ContractDTO.UpdateRequest request
    ) {

        Room room = validateOwnerAndRoomExistence(member, buildingId, roomId);

        Contract contract = contractRepository.getValidOneContractOrThrow(room.getId(), contractId);
        Contract updatedContract = contract.infoUpdate(request);

        return contractMapper.toInfo(updatedContract);

    }

    public Integer deleteContract(
            MemberDTO.Info member,
            Long buildingId,
            Long roomId,
            Long contractId
    ) {

        Room room = validateOwnerAndRoomExistence(member, buildingId, roomId);

        return contractRepository.deleteRegisteredContract(Status.UNREGISTER, room.getId(), contractId)
                .filter(result -> result != 0)
                .orElseThrow(() -> new ApiException(ApiErrorCode.DELETE_FAIL));

    }

    public ContractDTO.StatusInfo completeContract(
            MemberDTO.Info member,
            Long buildingId,
            Long roomId,
            Long contractId,
            LocalDate startDate
    ) {

        Contract contract = getValidRoomAndContract(member, buildingId, roomId, contractId);

        if (startDate.isAfter(contract.getStartDate())) {
            throw new ApiException(ApiErrorCode.INVALID_CHANGE, "계약 완료처리가 가능한 일자가 이미 경과했습니다.");
        }

        Contract contractCompleted = contract.contractComplete();

        return contractMapper.toStatusInfo(contractCompleted);

    }

    public ContractDTO.StatusInfo cancelContract(
            MemberDTO.Info member,
            Long buildingId,
            Long roomId,
            Long contractId,
            LocalDate startDate
    ) {

        Contract contract = getValidRoomAndContract(member, buildingId, roomId, contractId);

        if (startDate.isAfter(contract.getStartDate())) {
            throw new ApiException(ApiErrorCode.INVALID_CHANGE, "취소 가능한 일자가 경과했습니다.");
        }

        Contract contractCanceled = contract.contractCancelMissedStartDate();

        return contractMapper.toStatusInfo(contractCanceled);

    }

    public ContractDTO.StatusInfo progressContract(
            MemberDTO.Info member,
            Long buildingId,
            Long roomId,
            Long contractId,
            LocalDate startDate
    ) {

        Contract contract = getValidRoomAndContract(member, buildingId, roomId, contractId);

        if (!startDate.isEqual(contract.getStartDate())) {
            throw new ApiException(ApiErrorCode.INVALID_CHANGE, "계약 이행 가능 일자가 아닙니다.");
        }

        Contract contractProgressed = contract.contractInProgress();

        return contractMapper.toStatusInfo(contractProgressed);
    }

    public ContractDTO.StatusInfo expireContract(
            MemberDTO.Info member,
            Long buildingId,
            Long roomId,
            Long contractId,
            LocalDate endDate
    ) {

        Contract contract = getValidRoomAndContract(member, buildingId, roomId, contractId);

        if (endDate.isBefore(contract.getEndDate())) { // 원하는 일자가 실제 만료일의 이전 일자인 경우(이후 일자인지의 여부는 상관 X -> 실제 만료일 이후라도 만료 처리 가능 고려함)
            throw new ApiException(ApiErrorCode.INVALID_CHANGE,
                    """
                            만료 상태로 변경을 원하는 일자가
                            정해진 만료 일자보다 이전 일자인 경우
                            계약 만료 상태로 변경할 수 없습니다.
                            """);
        }

        Contract contractExpired = contract.contractExpire();

        return contractMapper.toStatusInfo(contractExpired);

    }

    public ContractDTO.StatusInfo terminateContract(
            MemberDTO.Info member,
            Long buildingId,
            Long roomId,
            Long contractId,
            LocalDate checkOut
    ) {

        Contract contract = getValidRoomAndContract(member, buildingId, roomId, contractId);

        if (checkOut.isAfter(contract.getEndDate()) ||
            checkOut.isEqual(contract.getCheckOut())) {
            throw new ApiException(ApiErrorCode.INVALID_CHANGE,
                    """
                            원하는 퇴실 일자가 기존 퇴실 일자와 같거나
                            계약 종료 일자 보다 이후의 일자인 경우
                            계약 파기 상태로 변경할 수 없습니다.
                            """);
        }

        Contract contractTerminated = contract.contractTerminate(checkOut);

        return contractMapper.toStatusInfo(contractTerminated);

    }

    private Contract getValidRoomAndContract(
            MemberDTO.Info member, Long buildingId,
            Long roomId,
            Long contractId
    ) {
        if (member.getRole() != Role.OWNER) {
            throw new ApiException(ApiErrorCode.DO_NOT_HAVE_PERMISSION, "소유자만 접근할 수 있습니다.");
        }

        Room room = roomRepository.getRoomBySpecificIds(buildingId, roomId, member.getId(), Status.REGISTER)
                .orElseThrow(() -> new ApiException(ApiErrorCode.NOT_FOUND_VALID_ROOM));

        return contractRepository.getValidOneContractOrThrow(room.getId(), contractId);

    }

    private Room validateOwnerAndRoomExistence(MemberDTO.Info member, Long buildingId, Long roomId) {
        if (member.getRole() != Role.OWNER) {
            throw new ApiException(ApiErrorCode.DO_NOT_HAVE_PERMISSION, "소유자만 접근할 수 있습니다.");
        }

        return roomRepository.getRoomBySpecificIds(buildingId, roomId, member.getId(), Status.REGISTER)
                .orElseThrow(() -> new ApiException(ApiErrorCode.NOT_FOUND_VALID_ROOM));

    }

    /* 내 호실의 임대료 및 타호실 동일 항목 평균값 조회 */
    public ContractDTO.CostInfo getContractCostInfo(MemberDTO.Info member, Long buildingId, Long roomId) {

        Room room = validateOwnerAndRoomExistence(member, buildingId, roomId);

        // 내 호실의 임대료 조회
        ContractDTO.CostDto costDto = calculateAveragesPerRoom(room.getId());

        // 해당 빌딩의 모든 호실의 계약 조회
        ContractDTO.CostAverageDto costAverageDto = calculateCostAverages(buildingId, costDto);

        return contractMapper.toCostInfo(costDto, costAverageDto); // dto로 정리해서 반환

    }

    private ContractDTO.CostDto calculateAveragesPerRoom(Long roomId) {

        Contract contract = contractRepository.findByLatestContract(roomId); // 없으면 null 반환

        if (contract == null) {
            return new ContractDTO.CostDto();
        }

        return contractMapper.toCostDto(contract.getId(), contract.getDeposit(), contract.getRentalPrice());
    }

    private ContractDTO.CostAverageDto calculateCostAverages(Long buildingId, ContractDTO.CostDto costDto) {

        List<Contract> contracts;

        if (costDto.getId() == null) {
            contracts = contractRepository.findByContractInProgressAllRoomsPerBuilding(buildingId, null);
        } else {
            contracts = contractRepository.findByContractInProgressAllRoomsPerBuilding(buildingId, costDto.getId());
        }

        Double averageDeposit = contracts.stream() // 평균 보증금 계산
                .mapToDouble(Contract::getDeposit)
                .average()
                .orElse(0.0);

        Double averageRentalPrice = contracts.stream() // 평균 임대료 계산
                .mapToDouble(Contract::getRentalPrice)
                .average()
                .orElse(0.0);

        return contractMapper.toCostAverageDto(averageDeposit, averageRentalPrice);

    }

    /* 내 호실의 재계약률 & 비교 호실의 재계약률 평균 (현재일 기준, 누적치) */
    public ContractDTO.RenewalContractRateInfo getRenewalContractRateInfo(MemberDTO.Info member, Long buildingId, Long roomId) {
        Room room = validateOwnerAndRoomExistence(member, buildingId, roomId);

        List<ContractStatus> statusList = List.of(ContractStatus.PENDING, ContractStatus.CANCELED);

        /* 내 호실 재계약률 산출 */
        Double renewalContractRate = getRenewalContractData(room.getId(), statusList);

        /* 내 호실의 계약을 제외한 선택 빌딩의 모든 계약률 평균 산출 */
        double averageRenewalContractRate = getRenewalRelativeContractData(buildingId, room, statusList);

        return contractMapper.toRenewalContractRateInfo(renewalContractRate, averageRenewalContractRate);

    }

    private double getRenewalRelativeContractData(Long buildingId, Room room, List<ContractStatus> statusList) {
        List<Contract> contracts = contractRepository.findByAllContractAllRoomsPerBuilding(buildingId, room.getId(), statusList);
        Map<Room, Map<ContractType, Long>> contractCountsPerRoomAndType = getContractCountsPerRoomAndType(contracts);

        return contractCountsPerRoomAndType.values().stream()
                .mapToDouble(contractsTypeMap -> calculateRenewalContract(contracts, contractsTypeMap))
                .average()
                .orElse(0.0);
    }

    private double calculateRenewalContract(List<Contract> contracts, Map<ContractType, Long> contractsTypeMap) {
        long initialContractsCount = contractsTypeMap.getOrDefault(ContractType.INITIAL, 0L);
        long renewalContractsCount = contractsTypeMap.getOrDefault(ContractType.RENEWAL, 0L) + initialContractsCount - 1L; // 재계약 시도를 나타내는 계약 수

        System.out.println(renewalContractsCount);

        long failedRenewalContractsCount = getRenewalContractFailedCount(contracts);

        long pureRenewalContractsCount = renewalContractsCount - failedRenewalContractsCount;
        long totalContractsCount = initialContractsCount + pureRenewalContractsCount;

        System.out.println(totalContractsCount);

        double result = ((double) pureRenewalContractsCount / (totalContractsCount - 1)) * 100;

        return Math.round(result * 10.0) / 10.0;
    }

    private Map<Room, Map<ContractType, Long>> getContractCountsPerRoomAndType(List<Contract> contractList) {
        return contractList.stream()
                .collect(Collectors.groupingBy(
                        Contract::getRoom,
                        Collectors.groupingBy(
                                Contract::getContractType,
                                Collectors.counting()) // 2차 세분류
                ));
    }

    private Double getRenewalContractData(Long roomId, List<ContractStatus> statusList) {
        /* 내 호실 재계약 데이터 조회 & 카운팅(일단 누적치로 가자) - 계약대기, 취소상태 제외 모든 계약 데이터 조회 */
        List<Contract> contracts = contractRepository.findByAllContractPerRoom(roomId, statusList);
        Map<ContractType, Long> contractsTypeMap = getContractCountPerType(contracts);

        return calculateRenewalContract(contracts, contractsTypeMap);
    }

    private Map<ContractType, Long> getContractCountPerType(List<Contract> contracts) {
        return contracts.stream()
                .collect(Collectors.groupingBy(Contract::getContractType, Collectors.counting()));
    }

    private long getRenewalContractFailedCount(List<Contract> contracts) {
        return contracts.stream()
                .filter(contract -> contract.getContractType() == ContractType.INITIAL) // ContractType.INITIAL만 필터링
                .filter(this::isFailedRenewal) // 재계약 실패( isFailedRenewal=true )만 필터링
                .count();
    }

    private boolean isFailedRenewal(Contract currentContract) {
        /* 현재 계약의 이전 계약 정보를 가져옴 */
        PageRequest pageRequest = PageRequest.of(0, 1);

        Contract previousContract = contractRepository.findPreviousContract(currentContract.getId(), pageRequest);

        /* 이전 계약이 없으면 재계약이 아니므로 실패로 처리하지 않음 (false) */
        if (previousContract == null) {
            return false;
        }

        /* 이전 계약이 RENEWAL이고 현재 계약이 INITIAL인 경우 재계약 실패 처리 (true) */
        boolean result = previousContract.getContractType() == ContractType.RENEWAL
                         && currentContract.getContractType() == ContractType.INITIAL;
        return result;
    }

    /* 내 호실의 연간 공실률 & 비교 호실 연평균 공실률 조회 (현재일 기준) */
    public ContractDTO.VacancyRateInfo getContractVacancyRate(MemberDTO.Info member, Long buildingId, Long roomId) {
        Room room = validateOwnerAndRoomExistence(member, buildingId, roomId);

        return null;
    }

}
