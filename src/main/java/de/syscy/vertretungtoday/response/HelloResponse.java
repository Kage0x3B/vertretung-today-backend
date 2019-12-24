package de.syscy.vertretungtoday.response;

import lombok.Data;

@Data
public class HelloResponse {
	private final boolean success;
	private final String message;
}