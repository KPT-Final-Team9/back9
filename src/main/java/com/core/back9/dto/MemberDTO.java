package com.core.back9.dto;

import com.core.back9.entity.constant.Role;
import com.core.back9.entity.constant.Status;
import com.fasterxml.jackson.annotation.JsonInclude;
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
        private String email;
        private String password;
        private String phoneNumber;
        private Long tenantId;
//		private SignType signType;
//		private String firebaseUid;
//		private String firebaseFcmToken;
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
        //		private SignType signType;
//		private String firebaseUid;
//		private String firebaseFcmToken;
        private LocalDateTime createdAt;
    }

    @AllArgsConstructor
    @NoArgsConstructor
    @Builder
    @Getter
    public static class LoginRequest {
        private String email;
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
        //		private SignType signType;
//		private String firebaseUid;
//		private String firebaseFcmToken;
        private LocalDateTime createdAt;
        private LocalDateTime updatedAt;
    }

}
