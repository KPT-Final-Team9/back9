package com.core.back9.dto;

import com.core.back9.entity.constant.Status;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotEmpty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

public class TenantDTO {

	@AllArgsConstructor
	@NoArgsConstructor
	@Builder
	@Getter
	public static class Request {
		@NotEmpty(message = "입주사명은 필수 입력입니다.")
		private String name;

		@NotEmpty(message = "사업자번호는 필수 입력입니다.")
		private String companyNumber;
	}

	@AllArgsConstructor
	@NoArgsConstructor
	@Builder
	@Getter
	public static class Response {
		private Long id;
		private String name;
		private String companyNumber;
		private LocalDateTime createdAt;
	}

	@AllArgsConstructor
	@NoArgsConstructor
	@Builder
	@Getter
	public static class Info {
		private Long id;
		private String name;
		private String companyNumber;
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

	@AllArgsConstructor
	@NoArgsConstructor
	@Builder
	@Getter
	public static class SimpleInfo {
		private Long tenantId;
		private String tenantName;
		private String companyNumber;
	}

}
