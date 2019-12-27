package de.syscy.vertretungtoday.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(value= HttpStatus.FAILED_DEPENDENCY, reason="Not validated")
public class NotValidatedException extends RuntimeException {
	public NotValidatedException(String message) {
		super(message);
	}
}