package com.core.back9.service;

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

import java.util.List;

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
        validateEmail(request.getEmail(), Role.USER);
        validatePhoneNumber(request.getPhoneNumber(), Role.USER);

        Tenant tenant = tenantRepository.getValidOneTenantOrThrow(request.getTenantId());

        Member member = memberMapper.toEntity(request, tenant, Role.USER, Status.REGISTER);
        Member savedMember = memberRepository.save(member);

        return memberMapper.toUserRegisterResponse(savedMember);
    }

    @Transactional
    public MemberDTO.RegisterResponse ownerSignup(MemberDTO.RegisterRequest request) {
        validateEmail(request.getEmail(), Role.OWNER);
        validatePhoneNumber(request.getPhoneNumber(), Role.OWNER);

        Member member = memberMapper.toEntity(request, Role.OWNER, Status.REGISTER);
        Member savedMember = memberRepository.save(member);

        return memberMapper.toOwnerRegisterResponse(savedMember);
    }

    @Transactional
    public MemberDTO.RegisterResponse adminSignup(MemberDTO.RegisterRequest request) {
        validateEmail(request.getEmail(), Role.ADMIN);
        validatePhoneNumber(request.getPhoneNumber(), Role.ADMIN);

        Member member = memberMapper.toEntity(request, Role.ADMIN, Status.REGISTER);
        Member savedMember = memberRepository.save(member);

        return memberMapper.toAdminRegisterResponse(savedMember);
    }

    private void validateEmail(String email, Role role) {
        memberRepository.findByEmailAndRoleAndStatus(email, role, Status.REGISTER)
                .ifPresent(member -> {
                    throw new ApiException(ApiErrorCode.DUPLICATE_EMAIL);
                });
    }

    private void validatePhoneNumber(String phoneNumber, Role role) {
        memberRepository.findByPhoneNumberAndRoleAndStatus(phoneNumber, role, Status.REGISTER)
                .ifPresent(member -> {
                    throw new ApiException(ApiErrorCode.DUPLICATE_PHONE_NUMBER);
                });
    }

    @Transactional
    public MemberDTO.LoginResponse userLogin(MemberDTO.LoginRequest request) {
        Member member = memberRepository.findUserByEmailAndStatus(request.getEmail(), Role.USER, Status.REGISTER)
                .orElseThrow(() -> new ApiException(ApiErrorCode.NOT_FOUND_VALID_MEMBER));

        String requestPassword = request.getPassword();
        validatePassword(requestPassword, member);

        TokenDTO token = jwtProvider.createToken(member);

        return memberMapper.toLoginResponse(member, token);
    }

    @Transactional
    public MemberDTO.LoginResponse ownerLogin(MemberDTO.LoginRequest request) {
        Member member = memberRepository.findByEmailAndRoleAndStatus(request.getEmail(), Role.OWNER, Status.REGISTER)
                .orElseThrow(() -> new ApiException(ApiErrorCode.NOT_FOUND_VALID_MEMBER));

        String requestPassword = request.getPassword();
        validatePassword(requestPassword, member);

        TokenDTO token = jwtProvider.createToken(member);

        return memberMapper.toLoginResponse(member, token);
    }

    @Transactional
    public MemberDTO.LoginResponse adminLogin(MemberDTO.LoginRequest request) {
        Member member = memberRepository.findByEmailAndRoleAndStatus(request.getEmail(), Role.ADMIN, Status.REGISTER)
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

    public MemberDTO.Info selectOne(MemberDTO.Info member) {
        Member validMember = memberRepository.getValidMemberWithEmailAndStatus(member.getEmail(), Status.REGISTER);
        return memberMapper.toInfo(validMember);
    }

    public List<MemberDTO.Info> selectAllMembersByTenantId(MemberDTO.Info member) {
        long tenantId = member.getTenant().getId();
        return memberRepository.findAllByTenantIdAndStatus(tenantId, Status.REGISTER)
          .stream().map(memberMapper::toInfo).toList();
    }

}
