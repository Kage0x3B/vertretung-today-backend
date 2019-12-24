package de.syscy.vertretungtoday.moodle;

import lombok.Data;

@Data
public class MoodleResourceInfo {
	private final int id;
	private final ResourceType type;
	private final String url;

	public static enum ResourceType {
		FILE,
		EMBEDDED_PAGE
	}
}