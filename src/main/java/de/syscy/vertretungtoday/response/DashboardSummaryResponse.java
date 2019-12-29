package de.syscy.vertretungtoday.response;

import de.syscy.vertretungtoday.model.MoodleMessageOfTheDay;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.time.LocalDate;

@Data
public class DashboardSummaryResponse {
	private int amountToday;
	private int amountNext;
	private LocalDate nextDate;
	private MotdSection motd;

	@Data
	@AllArgsConstructor
	public static class MotdSection {
		private MoodleMessageOfTheDay today;
		private MoodleMessageOfTheDay next;
	}
}