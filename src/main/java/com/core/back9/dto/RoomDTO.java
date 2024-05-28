package com.core.back9.dto;

import com.core.back9.entity.constant.Status;
import com.core.back9.entity.constant.Usage;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

public class RoomDTO {

	@AllArgsConstructor
	@NoArgsConstructor
	@Builder
	@Getter
	public static class Request {
		@NotBlank(message = "호실명은 필수 입력입니다")
		private String name;

		@NotBlank(message = "층은 필수 입력입니다")
		private String floor;

		@NotNull(message = "면적은 필수 입력입니다")
		private float area;

		@NotNull(message = "용도는 필수 입력입니다")
		private Usage usage;
	}

	@AllArgsConstructor
	@NoArgsConstructor
	@Builder
	@Getter
	public static class Response {
		private Long id;
		private String name;
		private String floor;
		private float area;
		private Usage usage;
		private Status status;
		private LocalDateTime createdAt;
	}

	@AllArgsConstructor
	@NoArgsConstructor
	@Builder
	@Getter
	public static class Info {
		private Long id;
		private String name;
		private String floor;
		private float area;
		private Usage usage;
		private Status status;
		private float rating;
		private ContractDTO.InfoList contracts;
		private SettingDTO.Info setting;
		private LocalDateTime createdAt;
		private LocalDateTime updatedAt;
	}

	@AllArgsConstructor
	@NoArgsConstructor
	@Builder
	@Getter
	public static class InfoWithOwner {
		private Long id;
		private String name;
		private String floor;
		private float area;
		private Usage usage;
		private Status status;
		private MemberDTO.OwnerInfo ownerInfo;
	}

}
