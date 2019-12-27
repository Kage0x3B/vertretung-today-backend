package de.syscy.vertretungtoday.controller;

import de.syscy.vertretungtoday.moodle.MoodleApi;
import de.syscy.vertretungtoday.security.request.MoodleValidationRequest;
import de.syscy.vertretungtoday.security.JwtAuthentication;
import de.syscy.vertretungtoday.exception.EntityNotFoundException;
import de.syscy.vertretungtoday.security.model.Account;
import de.syscy.vertretungtoday.security.repository.AccountRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;

@RestController
@RequestMapping("/user")
public class UserController {
	private static final Logger LOGGER = LoggerFactory.getLogger(UserController.class);

	private AccountRepository accountRepository;
	private MoodleApi moodleApi;

	public UserController(AccountRepository accountRepository, MoodleApi moodleApi) {
		this.accountRepository = accountRepository;
		this.moodleApi = moodleApi;
	}
}