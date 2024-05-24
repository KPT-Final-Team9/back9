package com.core.back9.service;

import com.core.back9.dto.ContractDTO;
import com.core.back9.entity.Contract;
import com.core.back9.entity.Room;
import com.core.back9.entity.Tenant;
import com.core.back9.entity.constant.ContractType;
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

import java.util.List;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Transactional(readOnly = true)
@Service
public class ContractService {

    private final TenantRepository tenantRepository;
    private final RoomRepository roomRepository;
    private final ContractRepository contractRepository;
    private final ContractMapper contractMapper;

    @Transactional
    public ContractDTO.RegisterResponse registerContract(
            Long buildingId,
            Long roomId,
            Long tenantId,
            ContractDTO.RegisterRequest request
    ) {

        // 상위 데이터 유효성 검증
        Tenant tenant = tenantRepository.getValidOneTenantOrThrow(tenantId);
        Room room = roomRepository.getValidRoomWithIdOrThrow(buildingId, roomId, Status.REGISTER); // 해당 building의 room 조회

        contractRepository.findByContractRoomIdAndTenantId(roomId, tenantId, ContractType.INITIAL)
                .ifPresent(contract -> {
                    throw new ApiException(ApiErrorCode.ROOM_ALREADY_ASSIGNED);
                }); // 중복 데이터 검증(최초 계약이라는 정합성을 지켜야함!)

        Contract validcontract = contractMapper.toEntity(request, tenant, room, ContractType.INITIAL); // 연결 관계 매핑

        return contractMapper.toRegisterResponse(contractRepository.save(validcontract));

    }

    public ContractDTO.Info renewContract(
            Long buildingId,
            Long roomId,
            Long contractId,
            Long tenantId, ContractDTO.RegisterRequest request
    ) {

        Tenant tenant = tenantRepository.getValidOneTenantOrThrow(tenantId);
        Room room = roomRepository.getValidRoomWithIdOrThrow(buildingId, roomId, Status.REGISTER);

        contractRepository.findByContractInitial(contractId, ContractType.INITIAL)
                .orElseThrow(() -> new ApiException(ApiErrorCode.NOT_FOUND_VALID_CONTRACT)); // 초기 계약인지 검증

        // TODO : 최초 계약의 기간에 따라 재계약 일자를 검증 & 설정하는 비즈니스 로직 필요, 현재는 재계약의 동작 여부를 확인하기 위한 정도만 구현함


        Contract validContract = contractMapper.toEntity(request, tenant, room, ContractType.RENEWAL); // 엔티티 매핑

        return contractMapper.toInfo(validContract);

    }

    public ContractDTO.InfoList getAllContract(
            Long buildingId,
            Long roomId,
            Pageable pageable
    ) {

        Room room = roomRepository.getValidRoomWithIdOrThrow(buildingId, roomId, Status.REGISTER);
        Page<Contract> contracts = contractRepository.selectAllRegisteredContract(room.getId(), Status.REGISTER, pageable);

        long count = contracts.getTotalElements();

        List<ContractDTO.Info> contractInfoList = contracts.stream()
                .map(contractMapper::toInfo)
                .collect(Collectors.toList());

        return contractMapper.toInfoList(count, contractInfoList);

    }

    public ContractDTO.Info getOneContract(
            Long buildingId,
            Long roomId,
            Long contractId
    ) {

        Room room = roomRepository.getValidRoomWithIdOrThrow(buildingId, roomId, Status.REGISTER);
        Contract contract = contractRepository.getValidOneContractOrThrow(room.getId(), contractId);

        return contractMapper.toInfo(contract);

    }

    @Transactional
    public ContractDTO.Info modifyContract(
            Long buildingId,
            Long roomId,
            Long contractId,
            ContractDTO.UpdateRequest request
    ) {

        Room room = roomRepository.getValidRoomWithIdOrThrow(buildingId, roomId, Status.REGISTER);
        Contract contract = contractRepository.getValidOneContractOrThrow(room.getId(), contractId);
        Contract updatedContract = contract.infoUpdate(request);

        return contractMapper.toInfo(updatedContract);

    }

    @Transactional
    public Integer deleteContract(
            Long buildingId,
            Long roomId,
            Long contractId
    ) {

        Room room = roomRepository.getValidRoomWithIdOrThrow(buildingId, roomId, Status.REGISTER);
        return contractRepository.deleteRegisteredContract(Status.UNREGISTER, room.getId(), contractId)
                .filter(result -> result != 0)
                .orElseThrow(() -> new ApiException(ApiErrorCode.DELETE_FAIL));

    }

}
