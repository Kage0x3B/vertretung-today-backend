package de.syscy.vertretungtoday.security;

import de.syscy.vertretungtoday.security.exception.JwtAuthenticationException;
import de.syscy.vertretungtoday.security.service.JwtTokenService;
import io.jsonwebtoken.JwtException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.stereotype.Component;

@Component
public class JwtAuthenticationProvider implements AuthenticationProvider {
	private static final Logger LOGGER = LoggerFactory.getLogger(JwtAuthenticationProvider.class);

	private final JwtTokenService jwtTokenService;

	@Autowired
	public JwtAuthenticationProvider(JwtTokenService jwtTokenGeneratorService) {
		this.jwtTokenService = jwtTokenGeneratorService;
	}

	@Override
	public Authentication authenticate(Authentication authentication) throws AuthenticationException {
		try {
			String token = (String) authentication.getCredentials();
			String username = jwtTokenService.getUsernameFromToken(token);

			return jwtTokenService.validateToken(token).map(b -> new JwtAuthenticatedProfile(username))
								  .orElseThrow(() -> new JwtAuthenticationException("JWT Token validation failed"));
		} catch(JwtException ex) {
			LOGGER.error(String.format("Invalid JWT Token: %s", ex.getMessage()));
			throw new JwtAuthenticationException("Failed to verify token");
		}
	}

	@Override
	public boolean supports(Class<?> authentication) {
		return JwtAuthentication.class.equals(authentication);
	}
}