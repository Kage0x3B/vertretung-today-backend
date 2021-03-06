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
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class MoodleSubstitutionPlanService {
	private SubstitutionInfoRepository infoRepository;
	private MotdRepository motdRepository;

	public MoodleSubstitutionPlanService(SubstitutionInfoRepository infoRepository, MotdRepository motdRepository) {
		this.infoRepository = infoRepository;
		this.motdRepository = motdRepository;
	}

	public MoodleSubstitutionPlan getSubstitutionPlan(SubstitutionDate date, int grade, String gradeAddition, Set<String> courses) {
		//TODO: Cache this result, at least for a few minutes or with invalidation by an update event?
		MoodleSubstitutionPlan substitutionPlan = new MoodleSubstitutionPlan();

		Optional<MoodleMessageOfTheDay> motd = Optional.empty();
		List<SubstitutionEntry> substitutionEntries = null;

		LocalDate today = LocalDate.now();

		if(date == SubstitutionDate.TODAY) {
			motd = motdRepository.findById(today);

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

		if(courses != null) {
			Set<String> finalCourses = courses.stream().map(String::toLowerCase).collect(Collectors.toSet());
			substitutionEntries = substitutionEntries.stream().filter(s -> finalCourses.contains(s.getCourseId().toLowerCase()))
													 .collect(Collectors.toList());
		}

		if(gradeAddition != null && !gradeAddition.isEmpty()) {
			/*
			TODO: Using lowercase here would create a problem with grade additions for the mixed english classes with additions
			 containing an uppercase "E" if there is a class like "5e" in the future.. Not currently a problem at the FWG so ignored
			 */
			String finalGradeAddition = gradeAddition.toLowerCase();
			substitutionEntries = substitutionEntries.stream().filter(s -> s.getGradeAddition().toLowerCase().contains(finalGradeAddition))
													 .collect(Collectors.toList());
		}

		if(courses != null && courses.size() > 1) {
			Set<String> finalCourses = courses.stream().map(String::toLowerCase).collect(Collectors.toSet());
			substitutionEntries = substitutionEntries.stream().filter(s -> finalCourses.contains(s.getCourseId().toLowerCase()))
													 .collect(Collectors.toList());
		}

		substitutionPlan.setMotd(motd.orElse(null));
		substitutionPlan.setSubstitutionEntries(substitutionEntries);

		Optional<LocalDate> optDate = substitutionEntries.stream().map(SubstitutionEntry::getDay).findAny();
		Optional<LocalDateTime> optModifiedTime = substitutionEntries.stream().map(SubstitutionEntry::getModifiedDate).findAny();

		substitutionPlan.setDate(optDate.orElse(date.getDate()));
		substitutionPlan.setModifiedTime(optModifiedTime.orElse(substitutionPlan.getDate().atStartOfDay()));

		return substitutionPlan;
	}
}