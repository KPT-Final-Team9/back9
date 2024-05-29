package com.core.back9.util;

import com.core.back9.dto.DateDTO;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.Month;
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

	public int getMonth() {
		return localDate.getMonth().getValue();
	}

	public int getMonth(LocalDate localDate) {
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
		  .year(date.getYear())
		  .quarter(getQuarter(date))
		  .build();
	}

	public DateDTO getStartAndEndMonths(LocalDate... localDate) {
		LocalDate date = Arrays.stream(localDate).findFirst().orElseGet(() -> this.localDate);
		return DateDTO.builder()
		  .startMonth(getQuarter(date))
		  .endMonth(getQuarter(date))
		  .build();
	}

	public DateDTO allParameters(LocalDate... localDate) {
		LocalDate date = Arrays.stream(localDate).findFirst().orElseGet(() -> this.localDate);
		return DateDTO.builder()
		  .year(date.getYear())
		  .month(getMonth(date))
		  .quarter(getQuarter(date))
		  .startMonth(getQuarter(date))
		  .endMonth(getQuarter(date))
		  .build();
	}

	public boolean hasDataWithinTwoYears(List<LocalDateTime> updatedAts) {
		LocalDateTime now = LocalDateTime.now();
		LocalDateTime twoYearsAgo = now.minusYears(2);
		return updatedAts.stream().anyMatch(updatedAt -> !updatedAt.isBefore(twoYearsAgo));
	}

}
