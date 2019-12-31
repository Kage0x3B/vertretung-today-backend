package de.syscy.vertretungtoday.controller;

import de.syscy.vertretungtoday.model.MoodleSubstitutionPlan;
import de.syscy.vertretungtoday.model.SubstitutionDate;
import de.syscy.vertretungtoday.response.ApiResponse;
import de.syscy.vertretungtoday.response.DashboardSummaryResponse;
import de.syscy.vertretungtoday.service.MoodleSubstitutionPlanService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Arrays;
import java.util.Set;
import java.util.stream.Collectors;

// REST Controller für alles mit Vertretungsplänen
// Alle Anfragen unterstützen extra Sortieren der Ergebnisse nach Stufe/Klasse ("gradeAddition" -> bei 5a wäre "a" der gradeAddition Teil)
// und mit einer Liste von Kursen für die Oberstufe. Wird auf dem Backend Server anstatt in der App gemacht,
// damit die Funktionalität schon da ist was wichtig für Benachrichtigungen wird, weil bei denen auf dem Backend Server
// alles sortiert werden *muss*
@RestController
@RequestMapping("/substitutionPlan")
public class SubstitutionPlanController {
	private MoodleSubstitutionPlanService substitutionPlanService;

	public SubstitutionPlanController(MoodleSubstitutionPlanService substitutionPlanService) {
		this.substitutionPlanService = substitutionPlanService;
	}

	// Gibt ein "DashboardSummaryResponse" zurück, was extra fürs Dashboard ist mit Anzahl der Vertretungseinträge und
	// Nachrichten des Tages
	@GetMapping("/summary")
	public ResponseEntity<ApiResponse> getSummary(@RequestParam(value = "grade", defaultValue = "-1") int grade,
												  @RequestParam(value = "gradeAddition", defaultValue = "") String gradeAddition,
												  @RequestParam(value = "courses", defaultValue = "") String[] courses) {
		Set<String> coursesSet = null;

		if(courses != null && courses.length > 0) {
			coursesSet = Arrays.stream(courses).collect(Collectors.toSet());
		}

		MoodleSubstitutionPlan today = substitutionPlanService.getSubstitutionPlan(SubstitutionDate.TODAY, grade, gradeAddition, coursesSet);
		MoodleSubstitutionPlan next = substitutionPlanService.getSubstitutionPlan(SubstitutionDate.NEXT, grade, gradeAddition, coursesSet);

		DashboardSummaryResponse summaryResponse = new DashboardSummaryResponse();
		summaryResponse.setAmountToday(today.getSubstitutionEntries().size());
		summaryResponse.setAmountNext(next.getSubstitutionEntries().size());
		summaryResponse.setNextDate(next.getMotd() == null ? null : next.getMotd().getDate());
		summaryResponse.setMotd(new DashboardSummaryResponse.MotdSection(today.getMotd(), next.getMotd()));

		return ApiResponse.ok(summaryResponse).create();
	}

	// Gibt die Einträge des Vertretungsplan für "today" oder "next" zurück
	// Dabei ist "today" genau der heutige Tag und "next" ist der erste Vertretungsplan von irgendeinem Tag nach heute
	// Bedeutet z.B. in den Ferien der für den ersten Schultag oder am Freitag der für Montag
	@GetMapping("/get/{day}")
	public ResponseEntity<ApiResponse> getSubstitutions(@PathVariable("day") String day,
														@RequestParam(value = "grade", defaultValue = "-1") int grade,
														@RequestParam(value = "gradeAddition", defaultValue = "") String gradeAddition,
														@RequestParam(value = "courses", defaultValue = "") String[] courses) {
		SubstitutionDate date = SubstitutionDate.fromString(day);

		if(date == null) {
			throw new IllegalArgumentException("Invalid parameter \"day\"");
		}

		Set<String> coursesSet = null;

		if(courses != null && courses.length > 0) {
			coursesSet = Arrays.stream(courses).collect(Collectors.toSet());
		}

		return ApiResponse.ok(substitutionPlanService.getSubstitutionPlan(date, grade, gradeAddition, coursesSet)).create();
	}
}