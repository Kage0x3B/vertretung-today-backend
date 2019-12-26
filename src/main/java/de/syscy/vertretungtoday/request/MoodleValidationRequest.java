package de.syscy.vertretungtoday.request;

import lombok.Data;

@Data
public class MoodleValidationRequest {
	private String moodleUsername;
	private String moodlePassword;
}