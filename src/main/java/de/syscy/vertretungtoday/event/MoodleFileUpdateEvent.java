package de.syscy.vertretungtoday.event;

import de.syscy.vertretungtoday.moodle.MoodleResourceInfo;
import lombok.*;
import org.springframework.context.ApplicationEvent;

import java.util.List;

@ToString
@EqualsAndHashCode(callSuper = true)
public class MoodleFileUpdateEvent extends ApplicationEvent {
	private final @Getter List<MoodleResourceInfo> resourceInfoList;

	public MoodleFileUpdateEvent(Object source, List<MoodleResourceInfo> resourceInfoList) {
		super(source);

		this.resourceInfoList = resourceInfoList;
	}
}