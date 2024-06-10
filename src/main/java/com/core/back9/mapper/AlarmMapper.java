package com.core.back9.mapper;

import com.core.back9.dto.AlarmDTO;
import com.core.back9.entity.Alarm;
import org.mapstruct.Mapper;
import org.mapstruct.MappingConstants;
import org.mapstruct.ReportingPolicy;

@Mapper(
  componentModel = MappingConstants.ComponentModel.SPRING,
  unmappedSourcePolicy = ReportingPolicy.IGNORE,
  unmappedTargetPolicy = ReportingPolicy.IGNORE
)
public interface AlarmMapper {

	Alarm toEntity(AlarmDTO.Request request);

	AlarmDTO.Info toInfo(Alarm alarm);

}
