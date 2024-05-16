package com.core.back9.dto;

import com.core.back9.entity.constant.Role;
import com.core.back9.entity.constant.SignType;
import com.core.back9.entity.constant.Status;
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
		private Role role;
		private String phoneNumber;
		private Status status;
		private SignType signType;
		private String firebaseUid;
		private String firebaseFcmToken;
	}

	@AllArgsConstructor
	@NoArgsConstructor
	@Builder
	@Getter
	public static class RegisterResponse {
		private Long id;
		private String email;
		private Role role;
		private String phoneNumber;
		private Status status;
		private SignType signType;
		private String firebaseUid;
		private String firebaseFcmToken;
		private LocalDateTime createdAt;
	}

	@AllArgsConstructor
	@NoArgsConstructor
	@Builder
	@Getter
	public static class Info {
		private Long id;
		private String email;
		private Role role;
		private String phoneNumber;
		private Status status;
		private SignType signType;
		private String firebaseUid;
		private String firebaseFcmToken;
		private LocalDateTime createdAt;
		private LocalDateTime updatedAt;
	}

}
