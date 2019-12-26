package de.syscy.vertretungtoday.security.controller;

import de.syscy.vertretungtoday.exception.EntityNotFoundException;
import de.syscy.vertretungtoday.security.model.Account;
import de.syscy.vertretungtoday.security.repository.AccountRepository;
import de.syscy.vertretungtoday.security.request.LoginRequest;
import de.syscy.vertretungtoday.security.request.RegisterRequest;
import de.syscy.vertretungtoday.security.response.JwtTokenResponse;
import de.syscy.vertretungtoday.security.service.AuthenticationService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/auth")
public class AuthenticationController {
	private static final Logger LOGGER = LoggerFactory.getLogger(AuthenticationController.class);

	private AuthenticationService authenticationService;
	private AccountRepository accountRepository;
	private PasswordEncoder passwordEncoder;

	public AuthenticationController(AuthenticationService authenticationService, AccountRepository accountRepository, PasswordEncoder passwordEncoder) {
		this.authenticationService = authenticationService;
		this.accountRepository = accountRepository;
		this.passwordEncoder = passwordEncoder;
	}

	@PostMapping("/login")
	public ResponseEntity<JwtTokenResponse> login(@RequestBody LoginRequest request) {
		return new ResponseEntity<>(authenticationService.generateJwtToken(request.getUsername(), request.getPassword()), HttpStatus.OK);
	}

	@PostMapping("/register")
	public ResponseEntity<String> register(@RequestBody RegisterRequest request) {
		Account account = new Account();
		account.setEmail(request.getEmail());
		account.setUsername(request.getUsername());
		account.setPassword(passwordEncoder.encode(request.getPassword()));
		accountRepository.saveAndFlush(account);

		return ResponseEntity.ok("Success, validation required");
	}

	@GetMapping("/debug-list")
	public ResponseEntity<List<Account>> debugList() {
		return new ResponseEntity<>(accountRepository.findAll(), HttpStatus.OK);
	}

	@ExceptionHandler(EntityNotFoundException.class)
	public ResponseEntity<String> handleEntityNotFoundException(EntityNotFoundException ex) {
		return new ResponseEntity<>(ex.getMessage(), HttpStatus.NOT_FOUND);
	}
}