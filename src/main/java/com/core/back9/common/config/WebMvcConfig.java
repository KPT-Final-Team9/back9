package com.core.back9.common.config;

import com.core.back9.mapper.MemberMapper;
import com.core.back9.repository.MemberRepository;
import com.core.back9.security.AuthMemberResolver;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.util.List;

@Configuration
@RequiredArgsConstructor
public class WebMvcConfig implements WebMvcConfigurer {

    private final MemberRepository memberRepository;
    private final MemberMapper memberMapper;

    @Override
    public void addArgumentResolvers(List<HandlerMethodArgumentResolver> resolvers) {
        resolvers.add(new AuthMemberResolver(memberRepository, memberMapper));
    }

}
