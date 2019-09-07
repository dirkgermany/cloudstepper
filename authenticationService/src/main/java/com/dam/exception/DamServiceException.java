package com.dam.exception;

public class DamServiceException extends Exception{

	/**
	 * 
	 */
	private static final long serialVersionUID = 9166254022620287445L;
		
	private static final String SERVICE_NAME = "authentication_service";
	private Long errorId;
	private String shortMsg;
	private String description;
	
	public DamServiceException (Long errorId, String shortMsg, String description) {
		super (description);
		this.errorId = errorId;
		this.shortMsg = shortMsg;
		this.description = description;
	}

	public Long getErrorId() {
		return errorId;
	}

	public String getShortMsg() {
		return shortMsg;
	}

	public String getDescription() {
		return description;
	}

	public static String getServiceName() {
		return SERVICE_NAME;
	}
}
