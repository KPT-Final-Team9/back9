package com.core.back9.mapper;

import com.core.back9.dto.TenantDTO;
import com.core.back9.entity.Tenant;
import org.mapstruct.Mapper;
import org.mapstruct.MappingConstants;
import org.mapstruct.ReportingPolicy;

@Mapper(
  componentModel = MappingConstants.ComponentModel.SPRING,
  unmappedSourcePolicy = ReportingPolicy.IGNORE
)
public interface TenantMapper {

	Tenant toEntity(TenantDTO.RegisterRequest registerRequest);

	TenantDTO.RegisterResponse toRegisterResponse(Tenant tenant);

	TenantDTO.Info toInfo(Tenant tenant);

}
