package de.syscy.vertretungtoday.repository;

import de.syscy.vertretungtoday.model.MoodleMessageOfTheDay;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;

@Repository
public interface MotdRepository extends JpaRepository<MoodleMessageOfTheDay, Integer> {
	MoodleMessageOfTheDay findFirstByDate(LocalDate date);

	MoodleMessageOfTheDay findFirstByDateAfter(LocalDate date);
}