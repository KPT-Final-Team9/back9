package com.core.back9.dto;

import com.core.back9.entity.constant.RoomStatus;
import com.core.back9.entity.constant.Usage;
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
	public static class RegisterRequest {
		private String name;
		private String floor;
		private float area;
		private RoomStatus roomStatus;
		private Usage usage;
	}

	@AllArgsConstructor
	@NoArgsConstructor
	@Builder
	@Getter
	public static class RegisterResponse {
		private Long id;
		private String name;
		private String floor;
		private float area;
		private RoomStatus roomStatus;
		private Usage usage;
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
		private RoomStatus roomStatus;
		private Usage usage;
		private float rating;
		private LocalDateTime createdAt;
		private LocalDateTime updatedAt;
	}

}