package de.syscy.vertretungtoday.security.controller;

import de.syscy.vertretungtoday.exception.AlreadyValidatedException;
import de.syscy.vertretungtoday.exception.EntityNotFoundException;
import de.syscy.vertretungtoday.exception.InvalidMoodleAccountException;
import de.syscy.vertretungtoday.moodle.MoodleApi;
import de.syscy.vertretungtoday.response.ApiResponse;
import de.syscy.vertretungtoday.response.UserMessageResponse;
import de.syscy.vertretungtoday.security.model.Account;
import de.syscy.vertretungtoday.security.repository.AccountRepository;
import de.syscy.vertretungtoday.security.request.LoginRequest;
import de.syscy.vertretungtoday.security.request.MoodleValidationRequest;
import de.syscy.vertretungtoday.security.request.RegisterRequest;
import de.syscy.vertretungtoday.security.response.JwtTokenResponse;
import de.syscy.vertretungtoday.security.service.AuthenticationService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;

@RestController
@RequestMapping("/auth")
public class AuthenticationController {
	private static final Logger LOGGER = LoggerFactory.getLogger(AuthenticationController.class);

	private AuthenticationService authenticationService;
	private AccountRepository accountRepository;
	private PasswordEncoder passwordEncoder;
	private MoodleApi moodleApi;

	{
	}

	public AuthenticationController(AuthenticationService authenticationService, AccountRepository accountRepository,
									PasswordEncoder passwordEncoder, MoodleApi moodleApi) {
		this.authenticationService = authenticationService;
		this.accountRepository = accountRepository;
		this.passwordEncoder = passwordEncoder;
		this.moodleApi = moodleApi;
	}

	@PostMapping("/login")
	public ResponseEntity<ApiResponse> login(@RequestBody LoginRequest request) {
		JwtTokenResponse jwtTokenResponse = authenticationService.generateJwtToken(request.getUsername(), request.getPassword());

		return ApiResponse.ok("Valid authentication", jwtTokenResponse).create();
	}

	@PostMapping("/register")
	public ResponseEntity<ApiResponse> register(@RequestBody RegisterRequest request) {
		if(request.getUsername().length() < 3) {
			return ApiResponse
					.build(HttpStatus.BAD_REQUEST, "Invalid username length", new UserMessageResponse("username_field", "invalid_length"))
					.create();
		}

		if(request.getPassword().length() < 6) {
			return ApiResponse
					.build(HttpStatus.BAD_REQUEST, "Invalid password length", new UserMessageResponse("password_field", "invalid_length"))
					.create();
		}

		if(accountRepository.findByUsername(request.getUsername()).isPresent()) {
			return ApiResponse
					.build(HttpStatus.BAD_REQUEST, "Username not available", new UserMessageResponse("username_field", "username_taken"))
					.create();
		}

		Account account = new Account();
		account.setUsername(request.getUsername());
		account.setPassword(passwordEncoder.encode(request.getPassword()));
		accountRepository.saveAndFlush(account);

		return ApiResponse.ok("Registered successfully, account validation required next.", null).create();
	}

	@PostMapping("/validate/moodle")
	public ResponseEntity<ApiResponse> validateMoodle(@RequestBody MoodleValidationRequest request) throws IOException {
		Account account = accountRepository.findByUsername(request.getAccountUsername())
										   .orElseThrow(() -> new EntityNotFoundException("No account found for validation"));

		if(account.isValidated()) {
			throw new AlreadyValidatedException("Account already validated");
		}

		boolean valid = moodleApi.isAccountValid(request.getMoodleUsername(), request.getMoodlePassword());

		if(!valid) {
			throw new InvalidMoodleAccountException();
		}

		account.setValidated(true);
		accountRepository.saveAndFlush(account);

		return ApiResponse.ok("Validated account", null).create();
	}
}