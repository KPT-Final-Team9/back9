package com.core.back9.dto;

import com.core.back9.entity.constant.Role;
import com.core.back9.entity.constant.Status;
import com.fasterxml.jackson.annotation.JsonInclude;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

public class MemberDTO {

    @AllArgsConstructor
    @NoArgsConstructor
    @Builder
    @Getter
    public static class RegisterRequest {
        @NotBlank(message = "이메일은 필수 입력값입니다.")
        @Email(message = "잘못된 이메일 형식입니다.")
        private String email;

        @NotBlank(message = "비밀번호는 필수 입력값입니다.")
        @Size(min = 8, max = 16, message = "비밀번호는 8자 이상, 16자 이하여야 합니다.")
        private String password;

        @NotBlank(message = "휴대폰 번호는 필수 입력값입니다.")
        @Pattern(regexp = "^01[016789]-\\d{3,4}-\\d{4}$", message = "잘못된 휴대폰 번호 형식입니다.")
        private String phoneNumber;

        private Long tenantId;
    }

    @AllArgsConstructor
    @NoArgsConstructor
    @JsonInclude(JsonInclude.Include.NON_NULL)
    @Builder
    @Getter
    public static class RegisterResponse {
        private Long id;
        private String email;
        private Role role;
        private String phoneNumber;
        private Status status;
        private TenantDTO.Response tenant;
        private LocalDateTime createdAt;
    }

    @AllArgsConstructor
    @NoArgsConstructor
    @Builder
    @Getter
    public static class LoginRequest {
        @NotBlank(message = "이메일은 필수 입력값입니다.")
        @Email(message = "잘못된 이메일 형식입니다.")
        private String email;

        @NotBlank(message = "비밀번호는 필수 입력값입니다.")
        @Size(min = 8, max = 16, message = "비밀번호는 8자 이상, 16자 이하여야 합니다.")
        private String password;
    }

    @AllArgsConstructor
    @NoArgsConstructor
    @Builder
    @Getter
    public static class LoginResponse {
        private String email;
        private Role role;
        private String token;
    }

    @AllArgsConstructor
    @NoArgsConstructor
    @JsonInclude(JsonInclude.Include.NON_NULL)
    @Builder
    @Getter
    public static class Info {
        private Long id;
        private String email;
        private Role role;
        private String phoneNumber;
        private Status status;
        private TenantDTO.Info tenant;
        private LocalDateTime createdAt;
        private LocalDateTime updatedAt;
    }

    @AllArgsConstructor
    @NoArgsConstructor
    @Builder
    @Getter
    public static class OwnerInfo {
        private Long ownerId;
        private String ownerEmail;
        private Role role;
        private Status ownerStatus;
    }

}
