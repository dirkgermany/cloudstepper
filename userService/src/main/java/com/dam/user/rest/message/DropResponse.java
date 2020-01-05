package com.dam.user.rest.message;

import org.springframework.http.HttpStatus;

public class DropResponse extends RestResponse{
    	
	public DropResponse (HttpStatus result) {
		super(result, "OK", "User dropped");
	}  
}
