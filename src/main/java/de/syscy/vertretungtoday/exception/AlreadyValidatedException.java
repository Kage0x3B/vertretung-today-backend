package de.syscy.vertretungtoday.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(value= HttpStatus.NOT_FOUND, reason="Account already validated")
public class AlreadyValidatedException extends RuntimeException {
	public AlreadyValidatedException(String message) {
		super(message);
	}
}