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
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
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

        contractRepository.findByContract(roomId, statusList, request.getStartDate())
                .ifPresent(contracts -> {
                    if (!contracts.isEmpty()) {
                        throw new ApiException(ApiErrorCode.ROOM_ALREADY_ASSIGNED);
                    }
                });

        Contract validContract = contractMapper.toEntity(request, tenant, room, ContractType.INITIAL); // 연결 관계 매핑
        Contract savedContract = contractRepository.save(validContract);

        Contract completedContract = savedContract.contractComplete(); // 계약 무조건 완료처리 (계약 등록과 동시에 실행되는 완료처리라 기간 검증 필요X)

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

    public ContractDTO.statusInfo completeContract(
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

    public ContractDTO.statusInfo cancelContract(
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

    public ContractDTO.statusInfo progressContract(
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

    public ContractDTO.statusInfo expireContract(
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

    public ContractDTO.statusInfo terminateContract(
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
        if(member.getRole() != Role.OWNER) {
            throw new ApiException(ApiErrorCode.DO_NOT_HAVE_PERMISSION, "소유자만 접근할 수 있습니다.");
        }

        Room room = roomRepository.getRoomBySpecificIds(buildingId, roomId, member.getId(), Status.REGISTER)
                .orElseThrow(() -> new ApiException(ApiErrorCode.NOT_FOUND_VALID_ROOM));

        return contractRepository.getValidOneContractOrThrow(room.getId(), contractId);

    }

    private Room validateOwnerAndRoomExistence(MemberDTO.Info member, Long buildingId, Long roomId) {
        if(member.getRole() != Role.OWNER) {
            throw new ApiException(ApiErrorCode.DO_NOT_HAVE_PERMISSION, "소유자만 접근할 수 있습니다.");
        }

        return roomRepository.getRoomBySpecificIds(buildingId, roomId, member.getId(), Status.REGISTER)
                .orElseThrow(() -> new ApiException(ApiErrorCode.NOT_FOUND_VALID_ROOM));

    }

}
