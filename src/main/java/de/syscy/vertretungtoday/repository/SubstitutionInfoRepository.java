package de.syscy.vertretungtoday.repository;

import de.syscy.vertretungtoday.model.SubstitutionEntry;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface SubstitutionInfoRepository extends JpaRepository<SubstitutionEntry, String> {
	List<SubstitutionEntry> findAllByDay(LocalDate day);

	List<SubstitutionEntry> findAllByDayAfter(LocalDate day);

	List<SubstitutionEntry> findAllByDayAndGrade(LocalDate day, int grade);

	List<SubstitutionEntry> findAllByDayAfterAndGrade(LocalDate day, int grade);
}