package com.core.back9.common.util;

import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Optional;

@Slf4j
public class SecurityUtil {

    private SecurityUtil() {
    }

    public static Optional<String> getCurrentUsername() {
        final Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication == null) {
            log.info("Security Context에 인증 정보가 없습니다.");

            return Optional.empty();
        }

        String username = ((UserDetails) authentication.getPrincipal()).getUsername();

        return Optional.of(username);
    }

}
