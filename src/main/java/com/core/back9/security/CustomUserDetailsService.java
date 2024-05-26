package com.core.back9.security;

import com.core.back9.entity.Member;
import com.core.back9.entity.constant.Status;
import com.core.back9.repository.MemberRepository;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Component
public class CustomUserDetailsService implements UserDetailsService {

    private final MemberRepository memberRepository;

    public CustomUserDetailsService(MemberRepository memberRepository) {
        this.memberRepository = memberRepository;
    }

    @Transactional
    @Override
    public UserDetails loadUserByUsername(final String email) throws UsernameNotFoundException {
        return createUser(memberRepository.findByEmailAndStatus(email, Status.REGISTER)
                .orElseThrow(() -> new UsernameNotFoundException("회원 정보가 없습니다.")));
    }

    private User createUser(Member member) {
        List<GrantedAuthority> grantedAuthorities = List.of(new SimpleGrantedAuthority(member.getRole().toString()));

        return new User(member.getEmail(), member.getPassword(), grantedAuthorities);
    }

}
