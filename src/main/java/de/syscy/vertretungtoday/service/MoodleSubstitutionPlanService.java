package de.syscy.vertretungtoday.service;

import de.syscy.vertretungtoday.repository.SubstitutionInfoRepository;
import org.springframework.stereotype.Service;

@Service
public class MoodleSubstitutionPlanService {
	private SubstitutionInfoRepository infoRepository;

	public MoodleSubstitutionPlanService(SubstitutionInfoRepository infoRepository) {
		this.infoRepository = infoRepository;
	}
}