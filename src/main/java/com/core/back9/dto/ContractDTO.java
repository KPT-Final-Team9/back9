package com.core.back9.dto;

import com.core.back9.entity.constant.ContractStatus;
import com.core.back9.entity.constant.Status;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

public class ContractDTO {

	@AllArgsConstructor
	@NoArgsConstructor
	@Builder
	@Getter
	public static class RegisterRequest {
		private LocalDate startDate;
		private LocalDate endDate;
		private Long deposit;
		private Long rentalPrice;
	}

	@AllArgsConstructor
	@NoArgsConstructor
	@Builder
	@Getter
	public static class UpdateRequest {
		private LocalDate startDate;
		private LocalDate endDate;
		private LocalDate checkOut;
		private Long deposit;
		private Long rentalPrice;
	}

	@AllArgsConstructor
	@NoArgsConstructor
	@Builder
	@Getter
	public static class RegisterResponse {
		private Long id;
		private LocalDate startDate;
		private LocalDate endDate;
		private LocalDate checkOut;
		private Long deposit;
		private Long rentalPrice;
		private ContractStatus contractStatus;
		private LocalDateTime createdAt;
	}

	@AllArgsConstructor
	@NoArgsConstructor
	@Builder
	@Getter
	public static class Info {
		private Long id;
		private LocalDate startDate;
		private LocalDate endDate;
		private LocalDate checkOut;
		private Long deposit;
		private Long rentalPrice;
		private ContractStatus contractStatus;
		private LocalDateTime createdAt;
		private LocalDateTime updatedAt;
	}

	@AllArgsConstructor
	@NoArgsConstructor
	@Builder
	@Getter
	public static class InfoList {
		private Long count;
		private List<Info> infoList;
	}
}
