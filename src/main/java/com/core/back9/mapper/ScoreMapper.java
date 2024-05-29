package com.core.back9.mapper;

import com.core.back9.dto.ScoreDTO;
import com.core.back9.entity.Score;
import org.mapstruct.Mapper;
import org.mapstruct.MappingConstants;
import org.mapstruct.ReportingPolicy;

@Mapper(
  componentModel = MappingConstants.ComponentModel.SPRING,
  unmappedSourcePolicy = ReportingPolicy.IGNORE,
  unmappedTargetPolicy = ReportingPolicy.IGNORE
)
public interface ScoreMapper {

	Score toEntity(ScoreDTO.UpdateRequest updateRequest);

	ScoreDTO.UpdateResponse toUpdateResponse(Score score);

	ScoreDTO.Info toInfo(Score score);

}
