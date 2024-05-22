package com.core.back9.dto;

import lombok.*;
import org.springframework.data.domain.Page;

import java.time.LocalDateTime;

public class BuildingDTO {

	@AllArgsConstructor
	@NoArgsConstructor
	@Builder
	@Getter
	public static class Request {
		private String name;
		private String address;
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

}
