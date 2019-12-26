package de.syscy.vertretungtoday.model;

import lombok.Data;

import javax.persistence.Entity;
import javax.persistence.Id;
import java.time.LocalDate;

@Data
@Entity
public class MoodleMessageOfTheDay {
	@Id private LocalDate date;
	private String message;
}