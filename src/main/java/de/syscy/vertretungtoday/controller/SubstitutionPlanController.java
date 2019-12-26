package de.syscy.vertretungtoday.controller;

import de.syscy.vertretungtoday.model.MoodleSubstitutionPlan;
import de.syscy.vertretungtoday.model.SubstitutionDate;
import de.syscy.vertretungtoday.service.MoodleSubstitutionPlanService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;

@RestController
@RequestMapping("/substitutionPlan")
public class SubstitutionPlanController {
	private MoodleSubstitutionPlanService substitutionPlanService;

	public SubstitutionPlanController(MoodleSubstitutionPlanService substitutionPlanService) {
		this.substitutionPlanService = substitutionPlanService;
	}

	@PostMapping("/get/{day}")
	public ResponseEntity<MoodleSubstitutionPlan> getSubstitutions(@PathVariable("day") String day) {
		SubstitutionDate date = SubstitutionDate.fromString(day);

		if(date == null) {
			throw new IllegalArgumentException("Invalid parameter \"day\"");
		}

		return new ResponseEntity<>(substitutionPlanService.getSubstitutionPlan(date), HttpStatus.OK);
	}
}