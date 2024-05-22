package com.core.back9.mapper;

import com.core.back9.dto.TenantDTO;
import com.core.back9.entity.Tenant;
import org.mapstruct.*;

import java.util.List;

@Mapper(
  componentModel = MappingConstants.ComponentModel.SPRING,
  unmappedSourcePolicy = ReportingPolicy.IGNORE,
  unmappedTargetPolicy = ReportingPolicy.IGNORE
)
public interface TenantMapper {

	Tenant toEntity(TenantDTO.Request request);

	TenantDTO.Response toRegisterResponse(Tenant tenant);

	TenantDTO.Info toInfo(Tenant tenant);

	TenantDTO.InfoList toInfoList(Long count, List<TenantDTO.Info> infoList);

}
