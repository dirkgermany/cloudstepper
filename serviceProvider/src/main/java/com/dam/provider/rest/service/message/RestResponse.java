package com.dam.provider.rest.service.message;

import org.springframework.http.HttpStatus;

public class RestResponse {

	private final String SERVICE_NAME = "ServiceProvider";

	private String message;
	private String description;
	private HttpStatus httpStatus;
	
	public RestResponse (HttpStatus httpStatus, String message, String description) {
		setReturnCode(httpStatus);
		setMessage(message);
		setDescription(description);
	}
	
	public String getServiceName() {
		return SERVICE_NAME;
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public HttpStatus getReturnCode() {
		return httpStatus;
	}

	public void setReturnCode(HttpStatus httpStatus) {
		this.httpStatus = httpStatus;
	}

}
