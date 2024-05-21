package com.core.back9.mapper;

import com.core.back9.dto.ContractDTO;
import com.core.back9.entity.Contract;
import org.mapstruct.Mapper;
import org.mapstruct.MappingConstants;
import org.mapstruct.ReportingPolicy;

@Mapper(
  componentModel = MappingConstants.ComponentModel.SPRING,
  unmappedSourcePolicy = ReportingPolicy.IGNORE,
  unmappedTargetPolicy = ReportingPolicy.IGNORE
)
public interface ContractMapper {

	Contract toEntity(ContractDTO.RegisterRequest registerRequest);

	ContractDTO.RegisterResponse toRegisterResponse(Contract contract);

	ContractDTO.Info toInfo(Contract contract);

}
