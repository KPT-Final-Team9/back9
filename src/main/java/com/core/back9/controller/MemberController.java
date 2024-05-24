package com.core.back9.controller;

import com.core.back9.dto.MemberDTO;
import com.core.back9.service.MemberService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/members")
public class MemberController {

    private final MemberService memberService;

    @PreAuthorize("hasAnyAuthority('USER', 'OWNER','ADMIN')")
    @GetMapping("/info")
    public ResponseEntity<MemberDTO.Info> getCurrentMemberInfo(HttpServletRequest request) {
        return ResponseEntity.ok(memberService.getCurrenMemberInfo());
    }

    @PreAuthorize("hasAnyAuthority('ADMIN')")
    @GetMapping("/info/{email}")
    public ResponseEntity<MemberDTO.Info> getMemberInfo(@PathVariable String email) {
        return ResponseEntity.ok(memberService.getMemberInfo(email));
    }

}
