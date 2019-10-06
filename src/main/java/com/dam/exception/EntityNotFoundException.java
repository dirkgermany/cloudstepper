package com.dam.exception;

public class EntityNotFoundException extends DamServiceException{

	/**
	 * 
	 */
	private static final long serialVersionUID = -8597408050956108894L;

	public EntityNotFoundException(Long errorId, String shortMsg, String description) {
		super(errorId, shortMsg, description);
	}

}
