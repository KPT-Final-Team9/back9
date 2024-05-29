package com.core.back9.dto;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Builder
@Getter
public class DateDTO {

	private int year;
	private int month;
	private int quarter;
	private int startMonth;
	private int endMonth;

	@Builder
	public DateDTO(int year, int month, int quarter, int startMonth, int endMonth) {
		this.year = year;
		this.month = month;
		this.quarter = quarter;
		this.startMonth = startMonth;
		this.endMonth = endMonth;
	}

}
