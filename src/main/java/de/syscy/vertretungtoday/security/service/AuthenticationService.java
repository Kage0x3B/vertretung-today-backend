package de.syscy.vertretungtoday.security.service;

import de.syscy.vertretungtoday.security.exception.EntityNotFoundException;
import de.syscy.vertretungtoday.security.repository.AccountRepository;
import de.syscy.vertretungtoday.security.response.JwtTokenResponse;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class AuthenticationService {
	private AccountRepository accountRepository;
	private JwtTokenGeneratorService jwtTokenGeneratorService;
	private PasswordEncoder passwordEncoder;

	public AuthenticationService(AccountRepository accountRepository, JwtTokenGeneratorService jwtTokenGeneratorService, PasswordEncoder passwordEncoder) {
		this.accountRepository = accountRepository;
		this.jwtTokenGeneratorService = jwtTokenGeneratorService;
		this.passwordEncoder = passwordEncoder;
	}

	public JwtTokenResponse generateJwtToken(String username, String password) {
		return accountRepository.findByUsername(username).stream().findAny()
								.filter(account -> passwordEncoder.matches(password, account.getPassword()) && account.isValidated())
								.map(account -> new JwtTokenResponse(jwtTokenGeneratorService.generateToken(username)))
								.orElseThrow(() -> new EntityNotFoundException("Account not found"));
	}
}