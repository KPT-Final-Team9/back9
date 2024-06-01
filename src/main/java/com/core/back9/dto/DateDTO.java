package com.core.back9.dto;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.DayOfWeek;
import java.time.Month;
import java.time.Year;

@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
public class DateDTO {

	private Year year;
	private int yearValue;
	private Month month;
	private int monthValue;
	private int quarter;
	private Month startMonth;
	private int startMonthValue;
	private Month endMonth;
	private int endMonthValue;
	private DayOfWeek day;
	private int dayValue;

	@Builder
	public DateDTO(
	  Year year, int yearValue, Month month, int monthValue, int quarter,
	  Month startMonth, int startMonthValue, Month endMonth, int endMonthValue, DayOfWeek day, int dayValue) {
		this.year = year;
		this.yearValue = yearValue;
		this.month = month;
		this.monthValue = monthValue;
		this.quarter = quarter;
		this.startMonth = startMonth;
		this.startMonthValue = startMonthValue;
		this.endMonth = endMonth;
		this.endMonthValue = endMonthValue;
		this.day = day;
		this.dayValue = dayValue;
	}

}
