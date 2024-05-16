package com.core.back9.mapper;

import com.core.back9.dto.MemberDTO;
import com.core.back9.entity.Member;
import org.mapstruct.Mapper;
import org.mapstruct.MappingConstants;
import org.mapstruct.ReportingPolicy;

@Mapper(
  componentModel = MappingConstants.ComponentModel.SPRING,
  unmappedSourcePolicy = ReportingPolicy.IGNORE
)
public interface MemberMapper {

	Member toEntity(MemberDTO.RegisterRequest registerRequest);

	MemberDTO.RegisterResponse toRegisterResponse(Member member);

	MemberDTO.Info toInfo(Member member);

}
