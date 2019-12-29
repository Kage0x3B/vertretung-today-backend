package de.syscy.vertretungtoday.controller;

import de.syscy.vertretungtoday.response.ApiResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class AuthCheckController {
	@GetMapping("/checkAuth")
	public ResponseEntity<ApiResponse> checkAuth() {
		return ApiResponse.okMsg("Auth okay").create();
	}
}