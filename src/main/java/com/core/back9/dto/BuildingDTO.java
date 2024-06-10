package com.core.back9.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.*;
import org.springframework.data.domain.Page;

import java.time.LocalDateTime;

public class BuildingDTO {

	@AllArgsConstructor
	@NoArgsConstructor
	@Builder
	@Getter
	public static class Request {    // TODO 유효성 테스트 코드 해야함
		@NotBlank(message = "건물명은 필수 입력입니다")
		private String name;

		@NotBlank(message = "주소는 필수 입력입니다")
		private String address;

		@NotBlank(message = "우편번호는 필수 입력입니다")
		private String zipCode;
	}

	@AllArgsConstructor
	@NoArgsConstructor
	@Builder
	@Getter
	public static class Response {
		private Long id;
		private String name;
		private String address;
		private String zipCode;
		private LocalDateTime createdAt;
	}

	@AllArgsConstructor
	@NoArgsConstructor
	@Builder
	@Getter
	@Setter
	public static class Info {
		private Long id;
		private String name;
		private String address;
		private String zipCode;
		private LocalDateTime createdAt;
		private LocalDateTime updatedAt;
		private Page<RoomDTO.Info> roomPage;
	}

	@AllArgsConstructor
	@NoArgsConstructor
	@Builder
	@Getter
	public static class SimpleInfo {
		private Long id;
		private String name;
	}

}
