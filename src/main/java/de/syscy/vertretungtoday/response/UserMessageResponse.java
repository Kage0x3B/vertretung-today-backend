package de.syscy.vertretungtoday.response;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class UserMessageResponse {
	private String target;
	private String message;
}