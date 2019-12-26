package de.syscy.vertretungtoday.model;

import lombok.Data;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import java.time.LocalDate;

@Data
@Entity
public class MoodleMessageOfTheDay {
	@Id @GeneratedValue(strategy = GenerationType.AUTO) private Integer id;

	private String message;
	private LocalDate date;
}