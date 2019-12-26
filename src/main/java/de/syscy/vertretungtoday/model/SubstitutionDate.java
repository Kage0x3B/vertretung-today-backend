package de.syscy.vertretungtoday.model;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.TimeZone;

public enum SubstitutionDate {
	TODAY,
	NEXT;

	private static final int CUSTOM_SATURDAY = 5;
	private static final int CUSTOM_SUNDAY = 6;

	public static SubstitutionDate fromString(String s) {
		try {
			return valueOf(s.toUpperCase());
		} catch(IllegalArgumentException ex) {
			return null;
		}
	}

	private static int convertWeekdayFormat(int weekday) {
		// Wrap Sunday around to 6, every other day back one for Sunday and again so it starts at 0
		return weekday == 1 ? 6 : weekday - 2;
	}

	public LocalDate getDate() {
		if(this == TODAY) {
			return LocalDate.now();
		} else {
			Calendar today = new GregorianCalendar();

			// Starts at Sunday = 1 !! -.-' Probably some weird american thing but ok
			int dayOfWeek = convertWeekdayFormat(today.get(Calendar.DAY_OF_WEEK) - 2);

			dayOfWeek += 1; //Skip 1 day

			int daySkipAmount = 1;

			if(dayOfWeek == CUSTOM_SATURDAY) { // If the next day is Saturday, skip two more
				daySkipAmount = 3;
			} else if(dayOfWeek == CUSTOM_SUNDAY) { // If the next day is Saturday, skip one more
				daySkipAmount = 2;
			}

			Calendar nextDay = (Calendar) today.clone();
			nextDay.add(Calendar.DAY_OF_MONTH, daySkipAmount);

			TimeZone timeZone = nextDay.getTimeZone();
			ZoneId zoneId = timeZone == null ? ZoneId.systemDefault() : timeZone.toZoneId();

			return LocalDateTime.ofInstant(nextDay.toInstant(), zoneId).toLocalDate();
		}
	}
}