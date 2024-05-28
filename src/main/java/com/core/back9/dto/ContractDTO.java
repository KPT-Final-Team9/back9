package com.core.back9.dto;

import com.core.back9.entity.constant.ContractStatus;
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
	public static class RenewRequest {
		private LocalDate endDate; // 새 만기 일자
		private Long deposit;
		private Long rentalPrice;
	}

	@AllArgsConstructor
	@NoArgsConstructor
	@Builder
	@Getter
	public static class RenewDto {
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
		private TenantDTO.SimpleInfo tenant;
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
	public static class StatusInfo {
		private Long id;
		private LocalDate startDate;
		private LocalDate endDate;
		private LocalDate checkOut;
		private ContractStatus contractStatus;
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
