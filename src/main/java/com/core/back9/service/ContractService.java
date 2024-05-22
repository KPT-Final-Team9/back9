package com.core.back9.service;

import com.core.back9.dto.ContractDTO;
import com.core.back9.entity.Contract;
import com.core.back9.entity.constant.Status;
import com.core.back9.exception.ApiErrorCode;
import com.core.back9.exception.ApiException;
import com.core.back9.mapper.ContractMapper;
import com.core.back9.repository.ContractRepository;
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

    private final ContractRepository contractRepository;
    private final ContractMapper contractMapper;

    @Transactional
    public ContractDTO.RegisterResponse registerContract(ContractDTO.RegisterRequest request) {

        Contract contract = contractMapper.toEntity(request);

        return contractMapper.toRegisterResponse(contractRepository.save(contract));
    }

    public ContractDTO.InfoList getAllContract(Pageable pageable) {

        Page<Contract> contracts = contractRepository.selectAllRegisteredContract(Status.REGISTER, pageable);

        long count = contracts.getTotalElements();

        List<ContractDTO.Info> contractInfoList = contracts.stream()
                .map(contractMapper::toInfo)
                .collect(Collectors.toList());

        return contractMapper.toInfoList(count, contractInfoList);

    }

    @Transactional
    public ContractDTO.Info modifyContract(
            Long contractId,
            ContractDTO.UpdateRequest request
    ) {

        Contract contract = contractRepository.getValidOneContractOrThrow(Status.REGISTER, contractId);
        Contract updatedContract = contract.infoUpdate(request);

        return contractMapper.toInfo(updatedContract);

    }

    @Transactional
    public Integer deleteContract(Long contractId) {

        return contractRepository.deleteRegisteredContract(Status.UNREGISTER, contractId)
                .filter(result -> result != 0)
                .orElseThrow(() -> new ApiException(ApiErrorCode.DELETE_FAIL));

    }

    public ContractDTO.Info getOneContract(Long contractId) {

        Contract contract = contractRepository.getValidOneContractOrThrow(Status.REGISTER, contractId);

        return contractMapper.toInfo(contract);
    }
}
