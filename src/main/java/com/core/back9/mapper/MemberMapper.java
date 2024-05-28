package com.core.back9.mapper;

import com.core.back9.dto.MemberDTO;
import com.core.back9.dto.TokenDTO;
import com.core.back9.entity.Member;
import com.core.back9.entity.Tenant;
import com.core.back9.entity.constant.Role;
import com.core.back9.entity.constant.Status;
import org.mapstruct.*;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

@Mapper(
        componentModel = MappingConstants.ComponentModel.SPRING,
        unmappedSourcePolicy = ReportingPolicy.IGNORE,
        unmappedTargetPolicy = ReportingPolicy.IGNORE
)
public interface MemberMapper {

    @Mapping(source = "registerRequest.password", target = "password", qualifiedByName = "encryptedPassword")
    @Mapping(target = "tenant", expression = "java(tenant)")
    Member toEntity(MemberDTO.RegisterRequest registerRequest, @Context Tenant tenant, Role role, Status status);

    @Mapping(source = "registerRequest.password", target = "password", qualifiedByName = "encryptedPassword")
    Member toEntity(MemberDTO.RegisterRequest registerRequest, Role role, Status status);

    Member toEntity(MemberDTO.LoginRequest loginRequest);

    @Named("encryptedPassword")
    default String encryptPassword(String password) {
        return new BCryptPasswordEncoder().encode(password);
    }

    MemberDTO.RegisterResponse toUserRegisterResponse(Member member);

    MemberDTO.RegisterResponse toOwnerRegisterResponse(Member member);

    MemberDTO.RegisterResponse toAdminRegisterResponse(Member member);

    MemberDTO.LoginResponse toLoginResponse(Member member, TokenDTO token);

    MemberDTO.Info toInfo(Member member);

    MemberDTO.OwnerInfo toOwnerInfo(Member member);

}
