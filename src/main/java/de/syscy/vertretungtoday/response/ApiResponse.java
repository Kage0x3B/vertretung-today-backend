package de.syscy.vertretungtoday.response;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.io.Serializable;

@Data
@NoArgsConstructor
@Accessors(fluent = true, chain = true)
public class ApiResponse implements Serializable {
	private int status;
	private String message;
	private Object payload;

	public ApiResponse(HttpStatus httpStatus, String message, Object payload) {
		this.status = httpStatus.value();
		this.message = message;
		this.payload = payload;
	}

	public static ApiResponse build(HttpStatus status) {
		return build(status, status.getReasonPhrase());
	}

	public static ApiResponse build(HttpStatus status, String message) {
		return build(status, message, null);
	}

	public static <T> ApiResponse build(HttpStatus status, String message, T payload) {
		return new ApiResponse(status, message, payload);
	}

	public static <T> ApiResponse ok(T payload) {
		return ok("Ok", payload);
	}

	public static <T> ApiResponse ok(String message, T payload) {
		return build(HttpStatus.OK, message, payload);
	}

	public static ApiResponse notFound(String message) {
		return build(HttpStatus.NOT_FOUND, message);
	}

	public HttpStatus status() {
		return HttpStatus.resolve(status);
	}

	public ApiResponse status(HttpStatus httpStatus) {
		this.status = httpStatus.value();

		return this;
	}

	public ResponseEntity<ApiResponse> create() {
		HttpStatus httpStatus = HttpStatus.resolve(status);

		if(httpStatus == null) {
			httpStatus = HttpStatus.INTERNAL_SERVER_ERROR;
		}

		return new ResponseEntity<>(this, httpStatus);
	}
}