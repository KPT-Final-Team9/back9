package com.core.back9.controller;

import com.core.back9.dto.MemberDTO;
import com.core.back9.service.MemberService;
import io.swagger.v3.oas.annotations.Operation;
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

    @Operation(summary = "입주자 회원가입")
    @PostMapping("/sign-up/user")
    public ResponseEntity<MemberDTO.RegisterResponse> userSignup(
            @Valid
            @RequestBody MemberDTO.RegisterRequest request
    ) {
        return ResponseEntity.ok(memberService.userSignup(request));
    }

    @Operation(summary = "소유자 회원가입")
    @PostMapping("/sign-up/owner")
    public ResponseEntity<MemberDTO.RegisterResponse> ownerSignup(
            @Valid
            @RequestBody MemberDTO.RegisterRequest request
    ) {
        return ResponseEntity.ok(memberService.ownerSignup(request));
    }

    @Operation(summary = "관리자 회원가입")
    @PostMapping("/sign-up/admin")
    public ResponseEntity<MemberDTO.RegisterResponse> adminSignup(
            @Valid
            @RequestBody MemberDTO.RegisterRequest request
    ) {
        return ResponseEntity.ok(memberService.adminSignup(request));
    }

    @Operation(summary = "입주자 로그인")
    @PostMapping("/sign-in/user")
    public ResponseEntity<MemberDTO.LoginResponse> userLogin(
            @Valid
            @RequestBody MemberDTO.LoginRequest request
    ) {
        return ResponseEntity.ok(memberService.userLogin(request));
    }

    @Operation(summary = "소유자 로그인")
    @PostMapping("/sign-in/owner")
    public ResponseEntity<MemberDTO.LoginResponse> ownerLogin(
            @Valid
            @RequestBody MemberDTO.LoginRequest request
    ) {
        return ResponseEntity.ok(memberService.ownerLogin(request));
    }

    @Operation(summary = "관리자 로그인")
    @PostMapping("/sign-in/admin")
    public ResponseEntity<MemberDTO.LoginResponse> adminLogin(
            @Valid
            @RequestBody MemberDTO.LoginRequest request
    ) {
        return ResponseEntity.ok(memberService.adminLogin(request));
    }

}
