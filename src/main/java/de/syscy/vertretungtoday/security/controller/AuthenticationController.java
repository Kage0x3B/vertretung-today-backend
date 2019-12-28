package de.syscy.vertretungtoday.security.controller;

import de.syscy.vertretungtoday.exception.*;
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
import java.util.Optional;

@RestController
@RequestMapping("/auth")
public class AuthenticationController {
	private static final Logger LOGGER = LoggerFactory.getLogger(AuthenticationController.class);

	private AuthenticationService authenticationService;
	private AccountRepository accountRepository;
	private PasswordEncoder passwordEncoder;
	private MoodleApi moodleApi;

	public AuthenticationController(AuthenticationService authenticationService, AccountRepository accountRepository,
									PasswordEncoder passwordEncoder, MoodleApi moodleApi) {
		this.authenticationService = authenticationService;
		this.accountRepository = accountRepository;
		this.passwordEncoder = passwordEncoder;
		this.moodleApi = moodleApi;
	}

	@PostMapping("/login")
	public ResponseEntity<ApiResponse> login(@RequestBody LoginRequest request) {
		try {
			JwtTokenResponse jwtTokenResponse = authenticationService.generateJwtToken(request.getUsername(), request.getPassword());

			return ApiResponse.ok("Valid authentication", jwtTokenResponse).create();
		} catch(EntityNotFoundException ex) {
			return ApiResponse
					.build(HttpStatus.BAD_REQUEST, "Account not found", new UserMessageResponse("usernameField", "accountNotFound"))
					.create();
		} catch(UnauthorizedException ex) {
			return ApiResponse
					.build(HttpStatus.BAD_REQUEST, "Invalid password", new UserMessageResponse("passwordField", "invalidPassword"))
					.create();
		} catch(NotValidatedException ex) {
			return ApiResponse.exception(ex).create();
		}
	}

	@PostMapping("/register")
	public ResponseEntity<ApiResponse> register(@RequestBody RegisterRequest request) {
		if(request.getUsername().length() < 3) {
			return ApiResponse
					.build(HttpStatus.BAD_REQUEST, "Invalid username length", new UserMessageResponse("usernameField", "invalidUsernameLength"))
					.create();
		}

		if(request.getPassword().length() < 6) {
			return ApiResponse
					.build(HttpStatus.BAD_REQUEST, "Invalid password length", new UserMessageResponse("passwordField", "invalidPasswordLength"))
					.create();
		}

		if(accountRepository.findByUsername(request.getUsername()).isPresent()) {
			return ApiResponse
					.build(HttpStatus.BAD_REQUEST, "Username not available", new UserMessageResponse("usernameField", "usernameTaken"))
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
		Optional<Account> accountOpt = accountRepository.findByUsername(request.getAccountUsername());

		if(!accountOpt.isPresent()) {
			return ApiResponse
					.build(HttpStatus.BAD_REQUEST, "Account not found", new UserMessageResponse("accountUsernameField", "accountNotFound"))
					.create();
		}

		Account account = accountOpt.get();

		if(account.isValidated()) {
			return ApiResponse
					.build(HttpStatus.BAD_REQUEST, "Account already validated", new UserMessageResponse("accountUsernameField", "accountAlreadyValidated"))
					.create();
		}

		boolean valid = moodleApi.isAccountValid(request.getMoodleUsername(), request.getMoodlePassword());

		if(!valid) {
			return ApiResponse
					.build(HttpStatus.BAD_REQUEST, "Invalid Moodle Authentication", new UserMessageResponse("general", "invalidMoodleAuthentication"))
					.create();
		}

		account.setValidated(true);
		accountRepository.saveAndFlush(account);

		return ApiResponse.ok("Validated account", null).create();
	}
}