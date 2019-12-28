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

@RestController
@RequestMapping("/substitutionPlan")
public class SubstitutionPlanController {
	private MoodleSubstitutionPlanService substitutionPlanService;

	public SubstitutionPlanController(MoodleSubstitutionPlanService substitutionPlanService) {
		this.substitutionPlanService = substitutionPlanService;
	}

	@GetMapping("/summary")
	public ResponseEntity<ApiResponse> getSummary(@RequestParam(value = "grade", defaultValue = "-1") int grade) {
		MoodleSubstitutionPlan today = substitutionPlanService.getSubstitutionPlan(SubstitutionDate.TODAY, grade);
		MoodleSubstitutionPlan next = substitutionPlanService.getSubstitutionPlan(SubstitutionDate.NEXT, grade);

		DashboardSummaryResponse summaryResponse = new DashboardSummaryResponse();
		summaryResponse.setAmountToday(today.getSubstitutionEntries().size());
		summaryResponse.setAmountNext(next.getSubstitutionEntries().size());
		summaryResponse.setMotd(new DashboardSummaryResponse.MotdSection(today.getMessageOfTheDay(), next.getMessageOfTheDay()));

		return ApiResponse.ok(summaryResponse).create();
	}

	@GetMapping("/get/{day}")
	public ResponseEntity<ApiResponse> getSubstitutions(@PathVariable("day") String day, @RequestParam(value = "grade", defaultValue = "-1") int grade) {
		SubstitutionDate date = SubstitutionDate.fromString(day);

		if(date == null) {
			throw new IllegalArgumentException("Invalid parameter \"day\"");
		}

		return ApiResponse.ok(substitutionPlanService.getSubstitutionPlan(date, grade)).create();
	}
}