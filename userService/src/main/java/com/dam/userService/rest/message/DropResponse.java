package com.dam.userService.rest.message;

public class DropResponse extends RestResponse{
    	
	public DropResponse (Long result) {
		super(result, "OK", "User dropped");
	}  
}
