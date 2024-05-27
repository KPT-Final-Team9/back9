package com.core.back9.jwt;

import com.core.back9.dto.TokenDTO;
import com.core.back9.entity.Member;
import com.core.back9.entity.constant.Role;
import com.core.back9.exception.ApiErrorCode;
import com.core.back9.exception.ApiException;
import com.core.back9.security.CustomUserDetails;
import io.jsonwebtoken.*;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import io.jsonwebtoken.security.SecurityException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;

import java.security.Key;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@Component
@Slf4j
public class JwtProvider implements InitializingBean {

    private final String secret;
    private final long tokenValidity;
    private Key key;

    public JwtProvider(
            @Value("${jwt.secret-key}") String secret,
            @Value("${jwt.token-validity}") long tokenValidity
    ) {
        this.secret = secret;
        this.tokenValidity = tokenValidity;
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        byte[] keyBytes = Decoders.BASE64.decode(secret);
        this.key = Keys.hmacShaKeyFor(keyBytes);
    }

    public TokenDTO createToken(Member member) {
        Map<String, Object> claims = new HashMap<>();
        claims.put("email", member.getEmail());
        claims.put("role", member.getRole());

        long now = (new Date()).getTime();
        Date expiration = new Date(now + tokenValidity);

        String token = Jwts.builder()
                .signWith(key, SignatureAlgorithm.HS512)
                .setClaims(claims)
                .setExpiration(expiration)
                .compact();

        return TokenDTO.builder()
                .token(token)
                .build();
    }

    public Authentication getAuthentication(String jwt) {
        Claims claims = Jwts
                .parserBuilder()
                .setSigningKey(key)
                .build()
                .parseClaimsJws(jwt)
                .getBody();

        CustomUserDetails principal = new CustomUserDetails(Member.builder()
                .email(claims.get("email", String.class))
                .password("")
                .role(Role.valueOf(claims.get("role", String.class)))
                .build());

        return new UsernamePasswordAuthenticationToken(principal, jwt, principal.getAuthorities());
    }

    public boolean validateToken(String jwt) {
        try {
            Jwts.parserBuilder().setSigningKey(key).build().parseClaimsJws(jwt);

            return true;
        } catch (SecurityException | MalformedJwtException | IllegalArgumentException e) {
            throw new ApiException(ApiErrorCode.INVALID_TOKEN);
        } catch (ExpiredJwtException e) {
            throw new ApiException(ApiErrorCode.EXPIRED_TOKEN);
        }
    }

}
