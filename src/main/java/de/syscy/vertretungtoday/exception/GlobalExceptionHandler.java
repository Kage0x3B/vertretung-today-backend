package de.syscy.vertretungtoday.exception;

import de.syscy.vertretungtoday.response.ApiResponse;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;
import org.springframework.web.util.WebUtils;

public class GlobalExceptionHandler extends ResponseEntityExceptionHandler {
	@ExceptionHandler(value = Exception.class)
	public ResponseEntity<ApiResponse> handleGlobalException(Exception ex, WebRequest request) {
		return ApiResponse.exception(ex).create();
	}
}