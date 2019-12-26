package de.syscy.vertretungtoday.event;

import de.syscy.vertretungtoday.model.MoodleSubstitutionPlan;
import lombok.Setter;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.ApplicationEventPublisherAware;
import org.springframework.stereotype.Component;

@Component
public class SubstitutionPlanUpdateEventPublisher implements ApplicationEventPublisherAware {
	private @Setter ApplicationEventPublisher applicationEventPublisher;

	public void publishEvent(Object source, MoodleSubstitutionPlan substitutionPlan) {
		applicationEventPublisher.publishEvent(new SubstitutionPlanUpdateEvent(source, substitutionPlan));
	}
}