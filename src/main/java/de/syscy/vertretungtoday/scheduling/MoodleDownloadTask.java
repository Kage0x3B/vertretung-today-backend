package de.syscy.vertretungtoday.scheduling;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
public class MoodleDownloadTask {
	@Scheduled(fixedDelay = 10 * 60 * 1000)
	public void updateSubstitutionPlans() {

	}
}