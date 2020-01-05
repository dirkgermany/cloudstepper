package com.dam.user.rest.message;

import org.springframework.http.HttpStatus;

public class ErrorResponse extends RestResponse{
    	
	public ErrorResponse (HttpStatus result, String shortMsg, String description) {
		super(result, shortMsg, description);

	}  
}
