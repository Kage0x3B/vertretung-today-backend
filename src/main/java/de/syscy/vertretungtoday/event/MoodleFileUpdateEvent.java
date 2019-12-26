package de.syscy.vertretungtoday.event;

import de.syscy.vertretungtoday.model.MoodleResource;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;
import org.springframework.context.ApplicationEvent;

@ToString
@EqualsAndHashCode(callSuper = true)
public class MoodleFileUpdateEvent extends ApplicationEvent {
	private final @Getter MoodleResource resource;

	public MoodleFileUpdateEvent(Object source, MoodleResource resource) {
		super(source);

		this.resource = resource;
	}
}