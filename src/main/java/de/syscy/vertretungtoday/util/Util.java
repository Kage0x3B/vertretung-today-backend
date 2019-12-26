package de.syscy.vertretungtoday.util;

import lombok.experimental.UtilityClass;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;

@UtilityClass
public class Util {
	private static final DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("dd.MM.yyyy");
	private static final DateTimeFormatter timeFormatter = DateTimeFormatter.ofPattern("HH:mm");

	public static String cleanString(String s) {
		return s.toLowerCase().replaceAll("[^a-z0-9_-]", "");
	}

	public static LocalDate parseDate(String dateString) {
		return LocalDate.parse(dateString, dateFormatter);
	}

	public static LocalDateTime parseTime(LocalDate baseDate, String timeString) {
		LocalTime time = LocalTime.parse(timeString, timeFormatter);
		return time.atDate(baseDate);
	}
}