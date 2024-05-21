package com.core.back9.mapper;

import com.core.back9.dto.SettingDTO;
import com.core.back9.entity.Setting;
import org.mapstruct.Mapper;
import org.mapstruct.MappingConstants;
import org.mapstruct.ReportingPolicy;

@Mapper(
  componentModel = MappingConstants.ComponentModel.SPRING,
  unmappedSourcePolicy = ReportingPolicy.IGNORE,
  unmappedTargetPolicy = ReportingPolicy.IGNORE
)
public interface SettingMapper {

	Setting toEntity(SettingDTO.RegisterRequest registerRequest);

	SettingDTO.RegisterResponse toRegisterResponse(Setting setting);

	SettingDTO.Info toInfo(Setting setting);

}
