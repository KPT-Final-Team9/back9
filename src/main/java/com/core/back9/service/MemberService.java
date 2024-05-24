package com.core.back9.service;

import com.core.back9.common.util.SecurityUtil;
import com.core.back9.dto.MemberDTO;
import com.core.back9.dto.TokenDTO;
import com.core.back9.entity.Member;
import com.core.back9.entity.constant.Role;
import com.core.back9.entity.constant.Status;
import com.core.back9.exception.ApiErrorCode;
import com.core.back9.exception.ApiException;
import com.core.back9.jwt.JwtProvider;
import com.core.back9.mapper.MemberMapper;
import com.core.back9.repository.MemberRepository;
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
    private final MemberMapper memberMapper;
    private final PasswordEncoder passwordEncoder;
    private final JwtProvider jwtProvider;

    @Transactional
    public MemberDTO.RegisterResponse userSignup(MemberDTO.RegisterRequest request) {
        memberRepository.findByEmail(request.getEmail())
                .ifPresent(member -> {
                    throw new ApiException(ApiErrorCode.INTERNAL_SERVER_ERROR);
                });

        Member member = memberMapper.toEntity(request, Role.USER, Status.REGISTER);
        Member savedMember = memberRepository.save(member);

        return memberMapper.toUserRegisterResponse(savedMember);
    }

    @Transactional
    public MemberDTO.RegisterResponse ownerSignup(MemberDTO.RegisterRequest request) {
        memberRepository.findByEmail(request.getEmail())
                .ifPresent(member -> {
                    throw new ApiException(ApiErrorCode.INTERNAL_SERVER_ERROR);
                });

        Member member = memberMapper.toEntity(request, Role.OWNER, Status.REGISTER);
        Member savedMember = memberRepository.save(member);

        return memberMapper.toOwnerRegisterResponse(savedMember);
    }

    @Transactional
    public MemberDTO.RegisterResponse adminSignup(MemberDTO.RegisterRequest request) {
        memberRepository.findByEmail(request.getEmail())
                .ifPresent(member -> {
                    throw new ApiException(ApiErrorCode.INTERNAL_SERVER_ERROR);
                });

        Member member = memberMapper.toEntity(request, Role.ADMIN, Status.REGISTER);
        Member savedMember = memberRepository.save(member);

        return memberMapper.toAdminRegisterResponse(savedMember);
    }

    @Transactional
    public MemberDTO.LoginResponse login(MemberDTO.LoginRequest request) {
        Member member = memberRepository.findByEmailAndStatus(request.getEmail(), Status.REGISTER)
                .filter(entity -> passwordEncoder.matches(request.getPassword(), entity.getPassword()))
                .orElseThrow(() -> new ApiException(ApiErrorCode.INTERNAL_SERVER_ERROR)); // TODO 나중에 예외 추가해서 수정하기

        TokenDTO token = jwtProvider.createToken(member);

        return memberMapper.toLoginResponse(member, token);
    }

    public MemberDTO.Info getCurrenMemberInfo() {
        String currentUsername = SecurityUtil.getCurrentUsername()
                .orElseThrow(() -> new ApiException(ApiErrorCode.INTERNAL_SERVER_ERROR));

        return memberMapper.toInfo(
                memberRepository.findByEmailAndStatus(currentUsername, Status.REGISTER)
                        .orElseThrow(() -> new ApiException(ApiErrorCode.INTERNAL_SERVER_ERROR))
        );
    }

    public MemberDTO.Info getMemberInfo(String email) {
        return memberMapper.toInfo(memberRepository.findByEmailAndStatus(email, Status.REGISTER)
                .orElse(null));
    }

}
