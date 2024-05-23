package com.core.back9.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

public class SettingDTO {

	@AllArgsConstructor
	@NoArgsConstructor
	@Builder
	@Getter
	public static class Info {
		private Long id;
		private boolean ratingToggle;
		private String encourageMessage;
		private LocalDateTime updatedAt;
	}

}
