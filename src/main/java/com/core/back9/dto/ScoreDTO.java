package com.core.back9.dto;

import com.core.back9.entity.constant.RatingType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.time.YearMonth;

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

	@AllArgsConstructor
	@NoArgsConstructor
	@Builder
	@Getter
	public static class DetailByMonth {    // 선택한 월을 기준으로 이전 1년치 데이터 배열로 주기
		private YearMonth selectedMonth;    // 선택한 월(년/월)
		private float totalAvg;                // 내 호실 점수
		private float evaluationProgress;    // 평가 진행률
		private float facilityAvg;        // 평가 항목별 점수 (시설)
		private float managementAvg;    // 평가 항목별 점수 (관리)
		private float complaintAvg;        // 평가 항목별 점수 (민원)
	}

}
