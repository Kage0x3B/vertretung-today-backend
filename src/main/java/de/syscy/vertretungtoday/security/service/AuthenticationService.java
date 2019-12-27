package de.syscy.vertretungtoday.security.service;

import de.syscy.vertretungtoday.exception.EntityNotFoundException;
import de.syscy.vertretungtoday.exception.NotValidatedException;
import de.syscy.vertretungtoday.exception.UnauthorizedException;
import de.syscy.vertretungtoday.response.ApiResponse;
import de.syscy.vertretungtoday.security.model.Account;
import de.syscy.vertretungtoday.security.repository.AccountRepository;
import de.syscy.vertretungtoday.security.response.JwtTokenResponse;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class AuthenticationService {
	private AccountRepository accountRepository;
	private JwtTokenGeneratorService jwtTokenGeneratorService;
	private PasswordEncoder passwordEncoder;

	public AuthenticationService(AccountRepository accountRepository, JwtTokenGeneratorService jwtTokenGeneratorService,
								 PasswordEncoder passwordEncoder) {
		this.accountRepository = accountRepository;
		this.jwtTokenGeneratorService = jwtTokenGeneratorService;
		this.passwordEncoder = passwordEncoder;
	}

	public JwtTokenResponse generateJwtToken(String username, String password) {
		Account account = accountRepository.findByUsername(username).orElseThrow(() -> new EntityNotFoundException("Account not found"));

		if(!passwordEncoder.matches(password, account.getPassword())) {
			throw new UnauthorizedException("Invalid password");
		}

		if(!account.isValidated()) {
			throw new NotValidatedException("Not validated");
		}

		return new JwtTokenResponse(jwtTokenGeneratorService.generateToken(account.getUsername()));
	}
}