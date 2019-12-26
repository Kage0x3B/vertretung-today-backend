package de.syscy.vertretungtoday.model;

import lombok.Data;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Data
public class MoodleSubstitutionPlan {
	private LocalDate date;
	private LocalDateTime modifiedTime;

	private MoodleMessageOfTheDay messageOfTheDay;
	private List<SubstitutionEntry> substitutionEntries;
}