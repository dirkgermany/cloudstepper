package com.dam.serviceProvider.rest.service.message;

public class RestResponse {

	private final String SERVICE_NAME = "ServiceProvider";

	private String message;
	private String description;
	private Long returnCode;
	
	public RestResponse (Long returnCode, String message, String description) {
		setReturnCode(returnCode);
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

	public Long getReturnCode() {
		return returnCode;
	}

	public void setReturnCode(Long returnCode) {
		this.returnCode = returnCode;
	}

}
