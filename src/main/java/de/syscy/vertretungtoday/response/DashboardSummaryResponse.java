package de.syscy.vertretungtoday.response;

import de.syscy.vertretungtoday.model.MoodleMessageOfTheDay;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
public class DashboardSummaryResponse {
	private int amountToday;
	private int amountNext;
	private MotdSection motd;

	@Data
	@AllArgsConstructor
	public static class MotdSection {
		private MoodleMessageOfTheDay today;
		private MoodleMessageOfTheDay next;
	}
}