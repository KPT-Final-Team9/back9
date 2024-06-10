package com.core.back9.dto;

import com.core.back9.entity.constant.ComplaintStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

public class ComplaintDTO {

	@AllArgsConstructor
	@NoArgsConstructor
	@Builder
	@Getter
	public static class RegisterRequest {
		private Long roomId;
		private String complaintMessage;
	}

	@AllArgsConstructor
	@NoArgsConstructor
	@Builder
	@Getter
	public static class Info {
		private Long id;
		private String complaintMessage;
		private ComplaintStatus complaintStatus;
		private String completedMessage;
		private LocalDateTime createdAt;
		private LocalDateTime updatedAt;
	}

}
