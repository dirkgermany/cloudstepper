package com.dam.address.rest.message;

import com.dam.address.model.entity.Address;

public class CreateResponse extends WriteResponse{

	public CreateResponse(Address address) {
		super(new Long(200), "OK", "Address created and delivered.", address);
	}
    	
}
