package de.syscy.vertretungtoday.controller;

import de.syscy.vertretungtoday.moodle.MoodleApi;
import de.syscy.vertretungtoday.moodle.MoodleApiException;
import de.syscy.vertretungtoday.security.request.LoginRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

//Noch ein paar überbleibsel vom Testen einiger Funktionen wie der Moodle Account Verifizierung..
//Eigentlich ein Sicherheitsrisiko deswegen wirds auch bald rausgenommen
@RestController
@RequestMapping("/hello")
public class HelloWorldController {
	private MoodleApi moodleApi;

	public HelloWorldController(MoodleApi moodleApi) {
		this.moodleApi = moodleApi;
	}

	@PostMapping("/checkacc")
	public ResponseEntity<String> checkacc(@RequestBody LoginRequest loginRequest) throws IOException {
		boolean valid = moodleApi.isAccountValid(loginRequest.getUsername(), loginRequest.getPassword());
		return new ResponseEntity<>("Valid: " + valid, HttpStatus.OK);
	}

	@GetMapping("/getresources")
	public ResponseEntity<Map<String, String>> getResources() throws IOException, MoodleApiException {
		Map<String, String> map = new HashMap<>();
		List<Integer> sectionIds = moodleApi.getSectionIds(MoodleApi.INFORMATION_COURSE_ID);

		for(int sectionId : sectionIds) {
			List<Integer> resourceIds = moodleApi.getResourceIds(MoodleApi.INFORMATION_COURSE_ID, sectionId);

			for(int resourceId : resourceIds) {
				map.put(MoodleApi.INFORMATION_COURSE_ID + "-" + sectionId + "-" + resourceId, moodleApi.getResourceInfo(resourceId).toString());
			}
		}
		return new ResponseEntity<>(map, HttpStatus.OK);
	}
}