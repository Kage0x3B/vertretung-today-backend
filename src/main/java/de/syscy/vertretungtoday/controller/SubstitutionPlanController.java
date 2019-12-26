package de.syscy.vertretungtoday.controller;

import de.syscy.vertretungtoday.model.MoodleSubstitutionPlan;
import de.syscy.vertretungtoday.model.SubstitutionDate;
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

	@GetMapping("/get/{day}")
	public ResponseEntity<MoodleSubstitutionPlan> getSubstitutions(@PathVariable("day") String day, @RequestParam(value = "grade", defaultValue = "-1") int grade) {
		SubstitutionDate date = SubstitutionDate.fromString(day);

		if(date == null) {
			throw new IllegalArgumentException("Invalid parameter \"day\"");
		}

		return new ResponseEntity<>(substitutionPlanService.getSubstitutionPlan(date, grade), HttpStatus.OK);
	}
}