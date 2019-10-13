package com.dam.coach.rest.message;

public class RestResponse {

	private final String SERVICE_NAME = "CoachService";

	private Long returnCode;
	private String description;
	private String result;


	public RestResponse(Long returnCode, String result, String description) {
		setReturnCode(returnCode);
		setDescription(description);
		setResult(result);
	}

	public String getServiceName() {
		return SERVICE_NAME;
	}

	private void setReturnCode(Long returnCode) {
		this.returnCode = returnCode;
	}

	public Long getReturnCode() {
		return returnCode;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public String getResult() {
		return result;
	}

	public void setResult(String result) {
		this.result = result;
	}

}
