package de.syscy.vertretungtoday.route;

import de.syscy.vertretungtoday.response.HelloResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/hello")
public class HelloWorldController {
	@GetMapping
	public ResponseEntity<HelloResponse> getHello() {
		return new ResponseEntity<>(new HelloResponse(true, "Hello World"), HttpStatus.OK);
	}
}