package de.syscy.vertretungtoday.repository;

import de.syscy.vertretungtoday.model.MoodleMessageOfTheDay;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.Optional;

// Eine Tabelle für alle "Nachrichten des Tages", verknüpft mit dem passenden Datum
@Repository
public interface MotdRepository extends JpaRepository<MoodleMessageOfTheDay, LocalDate> {
	Optional<MoodleMessageOfTheDay> findFirstByDateAfter(LocalDate date);
}