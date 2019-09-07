package com.dam.exception;

public class PermissionCheckException extends DamServiceException{

	/**
	 * 
	 */
	private static final long serialVersionUID = 5752623877346372405L;

	public PermissionCheckException(Long errorId, String shortMsg, String description) {
		super(errorId, shortMsg, description);
	}

}
