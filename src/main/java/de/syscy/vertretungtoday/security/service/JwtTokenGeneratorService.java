package de.syscy.vertretungtoday.security.service;

import io.jsonwebtoken.JwtBuilder;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.HashMap;

@Component
public class JwtTokenGeneratorService {
	private String secret;
	private Long expiration;

	public JwtTokenGeneratorService(@Value("${jwt.secret}") String secret, @Value("${jwt.expiration}") Long expiration) {
		this.secret = secret;
		this.expiration = expiration;
	}

	public String generateToken(String username) {
		final Date createdDate = new Date();
		final Date expirationDate = calculateExpirationDate(createdDate);

		JwtBuilder jwtBuilder = Jwts.builder();
		jwtBuilder.setClaims(new HashMap<>());
		jwtBuilder.setSubject(username);
		jwtBuilder.setIssuedAt(createdDate);
		jwtBuilder.setExpiration(expirationDate);
		jwtBuilder.signWith(SignatureAlgorithm.HS512, secret);

		return jwtBuilder.compact();
	}

	private Date calculateExpirationDate(Date createdDate) {
		return new Date(createdDate.getTime() + expiration * 10000);
	}
}