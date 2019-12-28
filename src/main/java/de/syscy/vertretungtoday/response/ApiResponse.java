package de.syscy.vertretungtoday.response;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.Accessors;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ResponseStatus;

import java.io.Serializable;

@Data
@NoArgsConstructor
@Accessors(chain = true)
public class ApiResponse implements Serializable {
	private static @Setter boolean originalExceptionInPayload = false;

	private int status;
	private String message;
	private String error;
	private Object payload;

	public ApiResponse(HttpStatus httpStatus, String message, Object payload) {
		this(httpStatus, message, null, payload);
	}

	public ApiResponse(HttpStatus httpStatus, String message, String error, Object payload) {
		this.status = httpStatus.value();
		this.message = message;
		this.error = error == null ? "" : error;
		this.payload = payload;
	}

	public static ApiResponse build(HttpStatus status) {
		return build(status, status.getReasonPhrase());
	}

	public static ApiResponse build(HttpStatus status, String message) {
		return build(status, message, null);
	}

	public static <T> ApiResponse build(HttpStatus status, String message, T payload) {
		return build(status, message, null, payload);
	}

	public static <T> ApiResponse build(HttpStatus status, String message, String error, T payload) {
		return new ApiResponse(status, message, error, payload);
	}

	public static <T> ApiResponse ok(T payload) {
		return ok("Ok", payload);
	}

	public static ApiResponse okMsg(String message) {
		return ok(message, null);
	}

	public static <T> ApiResponse ok(String message, T payload) {
		return build(HttpStatus.OK, message, payload);
	}

	public static ApiResponse exception() {
		return exception(null);
	}

	public static ApiResponse exception(Throwable ex) {
		return exception(ex, null);
	}

	public static ApiResponse exception(Throwable ex, HttpStatus forceHttpStatus) {
		HttpStatus httpStatus = forceHttpStatus != null ? forceHttpStatus : HttpStatus.INTERNAL_SERVER_ERROR;
		String message = httpStatus.getReasonPhrase();
		String error = ex == null ? "InternalServerError" : ex.getClass().getSimpleName();

		ResponseStatus[] responseStatuses = ex == null ? new ResponseStatus[0] : ex.getClass().getAnnotationsByType(ResponseStatus.class);

		if(responseStatuses.length > 0) {
			ResponseStatus responseStatus = responseStatuses[0];

			if(responseStatus.value() != HttpStatus.INTERNAL_SERVER_ERROR && forceHttpStatus == null) {
				httpStatus = responseStatus.value();
			} else if(responseStatus.code() != HttpStatus.INTERNAL_SERVER_ERROR && forceHttpStatus == null) {
				httpStatus = responseStatus.code();
			}

			message = !responseStatus.reason().isEmpty() ? responseStatus.reason() : httpStatus.getReasonPhrase();
		}

		if(ex != null) {
			if(ex.getMessage() != null && !ex.getMessage().isEmpty()) {
				message = ex.getMessage();
			} else if(httpStatus == HttpStatus.INTERNAL_SERVER_ERROR) {
				message = error;
			}
		}

		return build(httpStatus, message, error, originalExceptionInPayload ? ex : null);
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

	public ResponseEntity<Object> createObjectType() {
		HttpStatus httpStatus = HttpStatus.resolve(status);

		if(httpStatus == null) {
			httpStatus = HttpStatus.INTERNAL_SERVER_ERROR;
		}

		return new ResponseEntity<>(this, httpStatus);
	}
}