package de.syscy.vertretungtoday.listener;

import de.syscy.vertretungtoday.event.MoodleFileUpdateEvent;
import de.syscy.vertretungtoday.moodle.MoodleResourceInfo;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

@Component
public class MoodleFileUpdateListener {
	@Async
	@EventListener
	public void onMoodleFileUpdate(MoodleFileUpdateEvent event) {
		for(MoodleResourceInfo resourceInfo : event.getResourceInfoList()) {

		}
	}
}