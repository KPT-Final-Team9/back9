package com.core.back9.dto;

import com.core.back9.entity.constant.RatingType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

public class ScoreDTO {

	@AllArgsConstructor
	@NoArgsConstructor
	@Builder
	@Getter
	public static class RegisterRequest {
		private int score;
		private String comment;
		private RatingType ratingType;
	}

	@AllArgsConstructor
	@NoArgsConstructor
	@Builder
	@Getter
	public static class RegisterResponse {
		private Long id;
		private int score;
		private String comment;
		private RatingType ratingType;
		private LocalDateTime createdAt;
	}

	@AllArgsConstructor
	@NoArgsConstructor
	@Builder
	@Getter
	public static class Info {
		private int score;
		private String comment;
		private boolean bookmark;
		private RatingType ratingType;
		private LocalDateTime createdAt;
		private LocalDateTime updatedAt;
	}

}
