package com.core.back9.mapper;

import com.core.back9.dto.ComplaintDTO;
import com.core.back9.entity.Complaint;
import org.mapstruct.Mapper;
import org.mapstruct.MappingConstants;
import org.mapstruct.ReportingPolicy;

@Mapper(
  componentModel = MappingConstants.ComponentModel.SPRING,
  unmappedSourcePolicy = ReportingPolicy.IGNORE,
  unmappedTargetPolicy = ReportingPolicy.IGNORE
)
public interface ComplaintMapper {

	ComplaintDTO.Info toInfo(Complaint complaint);

}
