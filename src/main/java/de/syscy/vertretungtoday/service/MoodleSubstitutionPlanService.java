package de.syscy.vertretungtoday.service;

import de.syscy.vertretungtoday.model.MoodleMessageOfTheDay;
import de.syscy.vertretungtoday.model.MoodleSubstitutionPlan;
import de.syscy.vertretungtoday.model.SubstitutionDate;
import de.syscy.vertretungtoday.model.SubstitutionEntry;
import de.syscy.vertretungtoday.repository.MotdRepository;
import de.syscy.vertretungtoday.repository.SubstitutionInfoRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class MoodleSubstitutionPlanService {
	private SubstitutionInfoRepository infoRepository;
	private MotdRepository motdRepository;

	public MoodleSubstitutionPlanService(SubstitutionInfoRepository infoRepository, MotdRepository motdRepository) {
		this.infoRepository = infoRepository;
		this.motdRepository = motdRepository;
	}

	public MoodleSubstitutionPlan getSubstitutionPlan(SubstitutionDate date) {
		return getSubstitutionPlan(date, -1);
	}

	public MoodleSubstitutionPlan getSubstitutionPlan(SubstitutionDate date, int grade) {
		//TODO: Cache this result, at least for a few minutes or with invalidation by an update event?
		MoodleSubstitutionPlan substitutionPlan = new MoodleSubstitutionPlan();

		MoodleMessageOfTheDay motd = null;
		List<SubstitutionEntry> substitutionEntries = null;

		LocalDate today = LocalDate.now();

		if(date == SubstitutionDate.TODAY) {
			motd = motdRepository.findFirstByDate(today);

			if(grade == -1) {
				substitutionEntries = infoRepository.findAllByDay(today);
			} else {
				substitutionEntries = infoRepository.findAllByDayAndGrade(today, grade);
			}
		} else if(date == SubstitutionDate.NEXT) {
			motd = motdRepository.findFirstByDateAfter(today);

			if(grade == -1) {
				substitutionEntries = infoRepository.findAllByDayAfter(today);
			} else {
				substitutionEntries = infoRepository.findAllByDayAfterAndGrade(today, grade);
			}
		}

		if(substitutionEntries == null) {
			throw new IllegalStateException("substitutionEntries is null");
		}
		
		substitutionPlan.setMessageOfTheDay(motd);
		substitutionPlan.setSubstitutionEntries(substitutionEntries);

		Optional<LocalDate> optDate = substitutionEntries.stream().map(SubstitutionEntry::getDay).findAny();
		Optional<LocalDateTime> optModifiedTime = substitutionEntries.stream().map(SubstitutionEntry::getModifiedDate).findAny();

		substitutionPlan.setDate(optDate.orElse(date.getDate()));
		substitutionPlan.setModifiedDateTime(optModifiedTime.orElse(substitutionPlan.getDate().atStartOfDay()));

		return substitutionPlan;
	}
}