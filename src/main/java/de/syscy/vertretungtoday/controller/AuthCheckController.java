package de.syscy.vertretungtoday.controller;

import de.syscy.vertretungtoday.response.ApiResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

//Vielleicht für später um zu überprüfen ob der JSON Web Token noch gültig ist oder der Benutzer sich neu einloggen muss und kein anderer Fehler
//bei der Verbindung oder so vorliegt. Obwohl bei Fehlern in der Verbindung eh andere HTTP Status Codes geschickt werden also mal
//schauen ob das wirklich gebraucht wird..
@RestController
public class AuthCheckController {
	@GetMapping("/checkAuth")
	public ResponseEntity<ApiResponse> checkAuth() {
		return ApiResponse.okMsg("Auth okay").create();
	}
}