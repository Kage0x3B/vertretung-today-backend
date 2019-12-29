package de.syscy.vertretungtoday.controller;

import de.syscy.vertretungtoday.model.MoodleSubstitutionPlan;
import de.syscy.vertretungtoday.model.SubstitutionDate;
import de.syscy.vertretungtoday.response.ApiResponse;
import de.syscy.vertretungtoday.response.DashboardSummaryResponse;
import de.syscy.vertretungtoday.service.MoodleSubstitutionPlanService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.Arrays;
import java.util.Set;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/substitutionPlan")
public class SubstitutionPlanController {
	private MoodleSubstitutionPlanService substitutionPlanService;

	public SubstitutionPlanController(MoodleSubstitutionPlanService substitutionPlanService) {
		this.substitutionPlanService = substitutionPlanService;
	}

	@GetMapping("/summary")
	public ResponseEntity<ApiResponse> getSummary(@RequestParam(value = "grade", defaultValue = "-1") int grade, @RequestParam(value = "courses", defaultValue = "") String[] courses) {
		Set<String> coursesSet = null;

		if(courses != null && courses.length > 0) {
			coursesSet = Arrays.stream(courses).collect(Collectors.toSet());
		}

		MoodleSubstitutionPlan today = substitutionPlanService.getSubstitutionPlan(SubstitutionDate.TODAY, grade, coursesSet);
		MoodleSubstitutionPlan next = substitutionPlanService.getSubstitutionPlan(SubstitutionDate.NEXT, grade, coursesSet);

		DashboardSummaryResponse summaryResponse = new DashboardSummaryResponse();
		summaryResponse.setAmountToday(today.getSubstitutionEntries().size());
		summaryResponse.setAmountNext(next.getSubstitutionEntries().size());
		summaryResponse.setNextDate(next.getMotd() == null ? null : next.getMotd().getDate());
		summaryResponse.setMotd(new DashboardSummaryResponse.MotdSection(today.getMotd(), next.getMotd()));

		return ApiResponse.ok(summaryResponse).create();
	}

	@GetMapping("/get/{day}")
	public ResponseEntity<ApiResponse> getSubstitutions(@PathVariable("day") String day, @RequestParam(value = "grade", defaultValue = "-1") int grade, @RequestParam(value = "courses", defaultValue = "") String[] courses) {
		SubstitutionDate date = SubstitutionDate.fromString(day);

		if(date == null) {
			throw new IllegalArgumentException("Invalid parameter \"day\"");
		}

		Set<String> coursesSet = null;

		if(courses != null && courses.length > 0) {
			coursesSet = Arrays.stream(courses).collect(Collectors.toSet());
		}

		return ApiResponse.ok(substitutionPlanService.getSubstitutionPlan(date, grade, coursesSet)).create();
	}
}