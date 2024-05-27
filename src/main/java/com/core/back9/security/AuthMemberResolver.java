package com.core.back9.security;

import com.core.back9.entity.Member;
import com.core.back9.entity.constant.Status;
import com.core.back9.exception.ApiErrorCode;
import com.core.back9.exception.ApiException;
import com.core.back9.mapper.MemberMapper;
import com.core.back9.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.MethodParameter;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.support.WebDataBinderFactory;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.method.support.ModelAndViewContainer;

@RequiredArgsConstructor
@Component
@Slf4j
public class AuthMemberResolver implements HandlerMethodArgumentResolver {

    private final MemberRepository memberRepository;
    private final MemberMapper memberMapper;

    @Override
    public boolean supportsParameter(MethodParameter parameter) {
        return parameter.hasParameterAnnotation(AuthMember.class);
    }

    @Override
    public Object resolveArgument(MethodParameter parameter, ModelAndViewContainer mavContainer, NativeWebRequest webRequest, WebDataBinderFactory binderFactory) {
        final Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication == null || !(authentication.getPrincipal() instanceof UserDetails userDetails)) {
            throw new ApiException(ApiErrorCode.NOT_AUTHENTICATED_USER);
        }

        CustomUserDetails principal = memberRepository.findByEmailAndStatus(userDetails.getUsername(), Status.REGISTER)
                .map(CustomUserDetails::new)
                .orElseThrow(() -> new ApiException(ApiErrorCode.NOT_FOUND_VALID_MEMBER));

        Member member = principal.getMember()
                .orElseThrow(() -> new ApiException(ApiErrorCode.NOT_FOUND_VALID_PRINCIPAL));

        return memberMapper.toInfo(member);
    }

}
