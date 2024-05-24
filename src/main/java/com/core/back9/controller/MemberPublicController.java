package com.core.back9.controller;

import com.core.back9.dto.MemberDTO;
import com.core.back9.service.MemberService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@RestController
@RequestMapping("/public-api")
public class MemberPublicController {

    private final MemberService memberService;

    @PostMapping("/sign-up/user")
    public ResponseEntity<MemberDTO.RegisterResponse> userSignup(
            @Valid
            @RequestBody MemberDTO.RegisterRequest request
    ) {
        return ResponseEntity.ok(memberService.userSignup(request));
    }

    @PostMapping("/sign-up/owner")
    public ResponseEntity<MemberDTO.RegisterResponse> ownerSignup(
            @Valid
            @RequestBody MemberDTO.RegisterRequest request
    ) {
        return ResponseEntity.ok(memberService.ownerSignup(request));
    }

    @PostMapping("/sign-up/admin")
    public ResponseEntity<MemberDTO.RegisterResponse> adminSignup(
            @Valid
            @RequestBody MemberDTO.RegisterRequest request
    ) {
        return ResponseEntity.ok(memberService.adminSignup(request));
    }

    @PostMapping("/sign-in")
    public ResponseEntity<MemberDTO.LoginResponse> login(
            @Valid
            @RequestBody MemberDTO.LoginRequest request
    ) {
        return ResponseEntity.ok(memberService.login(request));
    }

}
