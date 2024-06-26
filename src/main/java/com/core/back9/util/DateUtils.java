package com.core.back9.util;

import com.core.back9.dto.DateDTO;
import lombok.NoArgsConstructor;

import java.time.*;
import java.util.Arrays;
import java.util.List;

@NoArgsConstructor
public class DateUtils {

	protected LocalDate localDate = LocalDate.now();

	public DateUtils(LocalDate localDate) {
		this.localDate = localDate;
	}

	public int getYear() {
		return localDate.getYear();
	}

	public int getYear(LocalDate localDate) {
		return localDate.getYear();
	}

	public int getMonthValue() {
		return localDate.getMonth().getValue();
	}

	public Month getMonth() {
		return localDate.getMonth();
	}

	public int getMonthValue(LocalDate localDate) {
		return localDate.getMonth().getValue();
	}

	public int getQuarter(LocalDate... localDate) {
		LocalDate date = Arrays.stream(localDate).findFirst().orElseGet(() -> this.localDate);
		Month month = date.getMonth();
		if (month.getValue() <= 3) {
			return 1;
		} else if (month.getValue() <= 6) {
			return 2;
		} else if (month.getValue() <= 9) {
			return 3;
		} else {
			return 4;
		}
	}

	public int getStartMonth(int quarter) {
		return switch (quarter) {
			case 1 -> 1;
			case 2 -> 4;
			case 3 -> 7;
			default -> 10;
		};
	}

	public int getEndMonth(int quarter) {
		return switch (quarter) {
			case 1 -> 3;
			case 2 -> 6;
			case 3 -> 9;
			default -> 12;
		};
	}

	public DateDTO getYearAndQuarter(LocalDate... localDate) {
		LocalDate date = Arrays.stream(localDate).findFirst().orElseGet(() -> this.localDate);
		return DateDTO.builder()
		  .yearValue(date.getYear())
		  .quarter(getQuarter(date))
		  .build();
	}

	public DateDTO getStartAndEndMonths(LocalDate... localDate) {
		LocalDate date = Arrays.stream(localDate).findFirst().orElseGet(() -> this.localDate);
		return DateDTO.builder()
		  .startMonthValue(getQuarter(date))
		  .endMonthValue(getQuarter(date))
		  .build();
	}

	public DateDTO allParameters(LocalDate... localDate) {
		LocalDate date = Arrays.stream(localDate).findFirst().orElseGet(() -> this.localDate);
		return DateDTO.builder()
		  .yearValue(date.getYear())
		  .monthValue(getMonthValue(date))
		  .quarter(getQuarter(date))
		  .startMonthValue(getQuarter(date))
		  .endMonthValue(getQuarter(date))
		  .build();
	}

	public boolean hasDataWithinTwoYears(List<LocalDateTime> updatedAts) {
		LocalDateTime now = LocalDateTime.now();
		LocalDateTime twoYearsAgo = now.minusYears(2);
		return updatedAts.stream().anyMatch(updatedAt -> !updatedAt.isBefore(twoYearsAgo));
	}

	public LocalDateTime[] getStartDayAndEndDayByYearAndQuarter(int year, int quarter) {
		int startMonth = getStartMonth(quarter);
		int endMonth = getEndMonth(quarter);

		LocalDateTime startDate = LocalDate.of(year, startMonth, 1).atTime(LocalTime.MIN);
		LocalDateTime endDate = YearMonth.of(year, endMonth).atEndOfMonth().atTime(LocalTime.MAX);
		return new LocalDateTime[]{startDate, endDate};
	}

}
