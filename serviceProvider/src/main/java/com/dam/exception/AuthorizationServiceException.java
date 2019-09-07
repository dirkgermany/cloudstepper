package com.dam.exception;

public class AuthorizationServiceException extends DamServiceException{

	/**
	 * 
	 */
	private static final long serialVersionUID = -6389097811340130263L;

	public AuthorizationServiceException(Long errorId, String shortMsg, String description) {
		super(errorId, shortMsg, description);
	}

}
