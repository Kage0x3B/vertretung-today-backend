package de.syscy.vertretungtoday.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(value = HttpStatus.UNAUTHORIZED, reason = "Moodle account validation failed")
public class InvalidMoodleAccountException extends RuntimeException {
	public InvalidMoodleAccountException() {
		super();
	}

	public InvalidMoodleAccountException(String message) {
		super(message);
	}
}