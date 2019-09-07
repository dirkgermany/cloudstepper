package com.dam.person.rest.message;

public class DropResponse extends RestResponse{
    	
	public DropResponse (Long result) {
		super(result, "OK", "Person dropped");
	}  
}
