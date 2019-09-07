package com.dam.address.rest.message;

public class DropResponse extends RestResponse{
    	
	public DropResponse (Long result) {
		super(new Long(200), "OK", "Address(es) dropped.");
	}  
}
