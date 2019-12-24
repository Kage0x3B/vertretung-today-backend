package de.syscy.vertretungtoday.event;

import de.syscy.vertretungtoday.moodle.MoodleResourceInfo;
import lombok.Setter;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.ApplicationEventPublisherAware;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class MoodleFileUpdateEventPublisher implements ApplicationEventPublisherAware {
	private @Setter ApplicationEventPublisher applicationEventPublisher;

	public void publishEvent(Object source, List<MoodleResourceInfo> resourceInfoList) {
		applicationEventPublisher.publishEvent(new MoodleFileUpdateEvent(source, resourceInfoList));
	}
}