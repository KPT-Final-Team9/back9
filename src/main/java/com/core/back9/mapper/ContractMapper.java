package com.core.back9.mapper;

import com.core.back9.dto.ContractDTO;
import com.core.back9.entity.Contract;
import org.mapstruct.Mapper;
import org.mapstruct.MappingConstants;
import org.mapstruct.ReportingPolicy;

import java.util.List;

@Mapper(
        componentModel = MappingConstants.ComponentModel.SPRING,
        unmappedSourcePolicy = ReportingPolicy.IGNORE,
        unmappedTargetPolicy = ReportingPolicy.IGNORE
)
public interface ContractMapper {

    Contract toEntity(ContractDTO.RegisterRequest registerRequest);

    Contract toEntity(ContractDTO.UpdateRequest request);

    ContractDTO.RegisterResponse toRegisterResponse(Contract contract);

    ContractDTO.Info toInfo(Contract contract);

    ContractDTO.InfoList toInfoList(Long count, List<ContractDTO.Info> infoList);

}
