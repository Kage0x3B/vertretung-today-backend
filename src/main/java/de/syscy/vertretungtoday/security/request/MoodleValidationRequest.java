package de.syscy.vertretungtoday.security.request;

import lombok.Data;

@Data
public class MoodleValidationRequest {
	private String accountUsername;
	private String moodleUsername;
	private String moodlePassword;
}