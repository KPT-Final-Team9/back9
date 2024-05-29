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
	public static class CreateResponse {
		private Long id;
		private Long roomId;
		private RatingType ratingType;
		private LocalDateTime createdAt;
	}

	@AllArgsConstructor
	@NoArgsConstructor
	@Builder
	@Getter
	public static class UpdateRequest {
		private int score;
		private String comment;
	}

	@AllArgsConstructor
	@NoArgsConstructor
	@Builder
	@Getter
	public static class UpdateResponse {
		private Long id;
		private int score;
		private String comment;
		private RatingType ratingType;
		private LocalDateTime updatedAt;
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
