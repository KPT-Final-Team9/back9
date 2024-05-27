package com.core.back9.mapper;

import com.core.back9.dto.ContractDTO;
import com.core.back9.entity.Contract;
import com.core.back9.entity.Room;
import com.core.back9.entity.Tenant;
import com.core.back9.entity.constant.ContractType;
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

    Contract toEntity(ContractDTO.RegisterRequest request, Tenant tenant, Room room, ContractType contractType);

    Contract toEntity(ContractDTO.UpdateRequest request);



    ContractDTO.RegisterResponse toRegisterResponse(Contract contract);

    ContractDTO.Info toInfo(Contract contract);

    ContractDTO.InfoList toInfoList(Long count, List<ContractDTO.Info> infoList);

    ContractDTO.statusInfo toStatusInfo(Contract contract);

}
