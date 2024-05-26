package com.core.back9.service;

import com.core.back9.common.util.SecurityUtil;
import com.core.back9.dto.MemberDTO;
import com.core.back9.dto.TokenDTO;
import com.core.back9.entity.Member;
import com.core.back9.entity.Tenant;
import com.core.back9.entity.constant.Role;
import com.core.back9.entity.constant.Status;
import com.core.back9.exception.ApiErrorCode;
import com.core.back9.exception.ApiException;
import com.core.back9.jwt.JwtProvider;
import com.core.back9.mapper.MemberMapper;
import com.core.back9.repository.MemberRepository;
import com.core.back9.repository.TenantRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
@Transactional(readOnly = true)
@Service
@Slf4j
public class MemberService {

    private final MemberRepository memberRepository;
    private final TenantRepository tenantRepository;
    private final MemberMapper memberMapper;
    private final PasswordEncoder passwordEncoder;
    private final JwtProvider jwtProvider;

    @Transactional
    public MemberDTO.RegisterResponse userSignup(MemberDTO.RegisterRequest request) {
        memberRepository.findByEmail(request.getEmail())
                .ifPresent(member -> {
                    throw new ApiException(ApiErrorCode.DUPLICATE_EMAIL);
                });

        memberRepository.findByPhoneNumber(request.getPhoneNumber())
                .ifPresent(member -> {
                    throw new ApiException(ApiErrorCode.DUPLICATE_PHONE_NUMBER);
                });

        Tenant tenant = tenantRepository.getValidOneTenantOrThrow(request.getTenantId());

        Member member = memberMapper.toEntity(request, tenant, Role.USER, Status.REGISTER);
        Member savedMember = memberRepository.save(member);

        return memberMapper.toUserRegisterResponse(savedMember);
    }

    @Transactional
    public MemberDTO.RegisterResponse ownerSignup(MemberDTO.RegisterRequest request) {
        memberRepository.findByEmail(request.getEmail())
                .ifPresent(member -> {
                    throw new ApiException(ApiErrorCode.DUPLICATE_EMAIL);
                });

        memberRepository.findByPhoneNumber(request.getPhoneNumber())
                .ifPresent(member -> {
                    throw new ApiException(ApiErrorCode.DUPLICATE_PHONE_NUMBER);
                });

        Tenant tenant = tenantRepository.getValidOneTenantOrThrow(request.getTenantId());

        Member member = memberMapper.toEntity(request, tenant, Role.OWNER, Status.REGISTER);
        Member savedMember = memberRepository.save(member);

        return memberMapper.toOwnerRegisterResponse(savedMember);
    }

    @Transactional
    public MemberDTO.RegisterResponse adminSignup(MemberDTO.RegisterRequest request) {
        memberRepository.findByEmail(request.getEmail())
                .ifPresent(member -> {
                    throw new ApiException(ApiErrorCode.DUPLICATE_EMAIL);
                });

        memberRepository.findByPhoneNumber(request.getPhoneNumber())
                .ifPresent(member -> {
                    throw new ApiException(ApiErrorCode.DUPLICATE_PHONE_NUMBER);
                });

        Tenant tenant = tenantRepository.getValidOneTenantOrThrow(request.getTenantId());

        Member member = memberMapper.toEntity(request, tenant, Role.ADMIN, Status.REGISTER);
        Member savedMember = memberRepository.save(member);

        return memberMapper.toAdminRegisterResponse(savedMember);
    }

    @Transactional
    public MemberDTO.LoginResponse login(MemberDTO.LoginRequest request) {
        Member member = memberRepository.findByEmailAndStatus(request.getEmail(), Status.REGISTER)
                .orElseThrow(() -> new ApiException(ApiErrorCode.NOT_FOUND_VALID_MEMBER));

        String requestPassword = request.getPassword();
        validatePassword(requestPassword, member);

        TokenDTO token = jwtProvider.createToken(member);

        return memberMapper.toLoginResponse(member, token);
    }

    private void validatePassword(String requestPassword, Member member) {
        if (!passwordEncoder.matches(requestPassword, member.getPassword())) {
            throw new ApiException(ApiErrorCode.INVALID_PASSWORD);
        }
    }

    public MemberDTO.Info getCurrenMemberInfo() {
        String currentUsername = SecurityUtil.getCurrentUsername()
                .orElseThrow(() -> new ApiException(ApiErrorCode.NOT_AUTHENTICATED_USER));

        return memberMapper.toInfo(
                memberRepository.findByEmailAndStatus(currentUsername, Status.REGISTER)
                        .orElseThrow(() -> new ApiException(ApiErrorCode.NOT_FOUND_VALID_MEMBER))
        );
    }

    public MemberDTO.Info getMemberInfo(String email) {
        return memberMapper.toInfo(memberRepository.findByEmailAndStatus(email, Status.REGISTER)
                .orElseThrow(() -> new ApiException(ApiErrorCode.NOT_FOUND_VALID_MEMBER)));
    }

}