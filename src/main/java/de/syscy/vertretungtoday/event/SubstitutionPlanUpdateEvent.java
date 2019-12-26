package de.syscy.vertretungtoday.event;

import de.syscy.vertretungtoday.model.MoodleResource;
import de.syscy.vertretungtoday.model.MoodleSubstitutionPlan;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;
import org.springframework.context.ApplicationEvent;

import java.util.List;

@ToString
@EqualsAndHashCode(callSuper = true)
public class SubstitutionPlanUpdateEvent extends ApplicationEvent {
	private final @Getter MoodleSubstitutionPlan substitutionPlan;

	public SubstitutionPlanUpdateEvent(Object source, MoodleSubstitutionPlan substitutionPlan) {
		super(source);

		this.substitutionPlan = substitutionPlan;
	}
}