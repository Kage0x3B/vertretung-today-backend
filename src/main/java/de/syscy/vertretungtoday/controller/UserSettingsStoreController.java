package de.syscy.vertretungtoday.controller;

import de.syscy.vertretungtoday.exception.EntityNotFoundException;
import de.syscy.vertretungtoday.model.UserSettings;
import de.syscy.vertretungtoday.repository.UserSettingsRepository;
import de.syscy.vertretungtoday.request.StoreUserSettingsRequest;
import de.syscy.vertretungtoday.response.ApiResponse;
import de.syscy.vertretungtoday.security.JwtAuthentication;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@RestController
@RequestMapping("/userSettings")
public class UserSettingsStoreController {
	private static final Logger LOGGER = LoggerFactory.getLogger(UserSettingsStoreController.class);

	private UserSettingsRepository settingsRepository;

	public UserSettingsStoreController(UserSettingsRepository settingsRepository) {
		this.settingsRepository = settingsRepository;
	}

	@GetMapping("/get")
	public ResponseEntity<ApiResponse> getSettings() {
		String username = ((JwtAuthentication) SecurityContextHolder.getContext().getAuthentication()).getUsername().toLowerCase();

		Optional<UserSettings> userSettings = settingsRepository.findById(username);

		if(userSettings.isPresent()) {
			return ApiResponse.ok(userSettings.get()).create();
		} else {
			return ApiResponse.exception(new EntityNotFoundException("No settings stored")).create();
		}
	}

	@PostMapping("/store")
	public ResponseEntity<ApiResponse> storeSettings(@RequestBody StoreUserSettingsRequest storeRequest) {
		String username = ((JwtAuthentication) SecurityContextHolder.getContext().getAuthentication()).getUsername().toLowerCase();

		Optional<UserSettings> optUserSettings = settingsRepository.findById(username);
		UserSettings userSettings = optUserSettings.orElseGet(() -> new UserSettings(username));
		userSettings.setSettingsData(storeRequest.getSettingsData());

		settingsRepository.saveAndFlush(userSettings);

		return ApiResponse.okMsg("Stored settings").create();
	}
}