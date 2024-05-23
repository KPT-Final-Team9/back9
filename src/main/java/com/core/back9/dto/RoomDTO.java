package com.core.back9.dto;

import com.core.back9.entity.constant.Status;
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
	public static class Request {
		private String name;
		private String floor;
		private float area;
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
		private SettingDTO.Info setting;
		private LocalDateTime createdAt;
		private LocalDateTime updatedAt;
	}

}
