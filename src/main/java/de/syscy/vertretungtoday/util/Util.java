package de.syscy.vertretungtoday.util;

import lombok.experimental.UtilityClass;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeFormatterBuilder;
import java.time.format.SignStyle;

import static java.time.temporal.ChronoField.*;

@UtilityClass
public class Util {
	private static final DateTimeFormatter DATE_FORMATTER = new DateTimeFormatterBuilder().appendValue(DAY_OF_MONTH, 1, 2, SignStyle.NEVER)
																						  .appendLiteral('.')
																						  .appendValue(MONTH_OF_YEAR, 1, 2, SignStyle.NEVER)
																						  .appendLiteral('.')
																						  .appendValue(YEAR, 4, 10, SignStyle.EXCEEDS_PAD)
																						  .toFormatter();
	;
	private static final DateTimeFormatter TIME_FORMATTER = DateTimeFormatter.ISO_LOCAL_TIME;

	public static String cleanString(String s) {
		return s.toLowerCase().replaceAll("[^a-z0-9_-]", "");
	}

	public static LocalDate parseDate(String dateString) {
		return LocalDate.parse(dateString, DATE_FORMATTER);
	}

	public static LocalDateTime parseTime(LocalDate baseDate, String timeString) {
		LocalTime time = LocalTime.parse(timeString, TIME_FORMATTER);
		return time.atDate(baseDate);
	}
}