package com.core.back9.jwt;

import com.core.back9.dto.TokenDTO;
import com.core.back9.entity.Member;
import io.jsonwebtoken.*;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.stereotype.Component;

import java.security.Key;
import java.util.*;

@Component
@Slf4j
public class JwtProvider implements InitializingBean {

    private static final String AUTHORITIES_KEY = "auth";
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
        claims.put(AUTHORITIES_KEY, member.getRole());

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

        Collection<? extends GrantedAuthority> authorities = Arrays.stream(claims.get(AUTHORITIES_KEY).toString().split(","))
                .map(SimpleGrantedAuthority::new)
                .toList();

        User principal = new User(claims.get("email", String.class), "", authorities);

        return new UsernamePasswordAuthenticationToken(principal, jwt, authorities);
    }

    public boolean validateToken(String jwt) {
        try {
            Jwts.parserBuilder().setSigningKey(key).build().parseClaimsJws(jwt);

            return true;
        } catch (io.jsonwebtoken.security.SecurityException | MalformedJwtException e) {
            log.info("잘못된 JWT 서명입니다.");
        } catch (ExpiredJwtException e) {
            log.info("만료된 JWT입니다.");
        } catch (UnsupportedJwtException e) {
            log.info("지원되지 않는 JWT입니다.");
        } catch (IllegalArgumentException e) {
            log.info("잘못된 JWT입니다.");
        }

        return false;
    }

}
