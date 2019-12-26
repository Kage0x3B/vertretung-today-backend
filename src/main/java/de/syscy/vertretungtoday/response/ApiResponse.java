package de.syscy.vertretungtoday.response;

import lombok.Data;

@Data
public class ApiResponse {
	private final int status;
	private String error;
	private Object payload;
}