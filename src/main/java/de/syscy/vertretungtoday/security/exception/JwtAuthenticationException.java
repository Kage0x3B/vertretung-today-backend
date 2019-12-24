package de.syscy.vertretungtoday.security.exception;

import org.springframework.security.core.AuthenticationException;

public class JwtAuthenticationException extends AuthenticationException {
	public JwtAuthenticationException(String message) {
		super(message);
	}
}