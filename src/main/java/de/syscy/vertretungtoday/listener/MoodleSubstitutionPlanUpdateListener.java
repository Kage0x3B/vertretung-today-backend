package de.syscy.vertretungtoday.listener;

import de.syscy.vertretungtoday.event.SubstitutionPlanUpdateEvent;
import de.syscy.vertretungtoday.repository.MotdRepository;
import de.syscy.vertretungtoday.repository.SubstitutionInfoRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

@Component
public class MoodleSubstitutionPlanUpdateListener {
	private static final Logger LOGGER = LoggerFactory.getLogger(MoodleSubstitutionPlanUpdateListener.class);

	private SubstitutionInfoRepository substitutionInfoRepository;
	private MotdRepository motdRepository;

	public MoodleSubstitutionPlanUpdateListener(SubstitutionInfoRepository substitutionInfoRepository, MotdRepository motdRepository) {
		this.substitutionInfoRepository = substitutionInfoRepository;
		this.motdRepository = motdRepository;
	}

	@Async
	@EventListener
	public void onSubstitutionPlanUpdate(SubstitutionPlanUpdateEvent event) {
		LOGGER.info("Updating substitution plan for " + event.getSubstitutionPlan().getDate());

		substitutionInfoRepository.saveAll(event.getSubstitutionPlan().getSubstitutionEntries());
		substitutionInfoRepository.flush();
		motdRepository.saveAndFlush(event.getSubstitutionPlan().getMessageOfTheDay());
	}
}