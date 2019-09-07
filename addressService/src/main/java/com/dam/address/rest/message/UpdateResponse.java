package com.dam.address.rest.message;

import com.dam.address.model.entity.Address;

public class UpdateResponse extends WriteResponse{
    	
	public UpdateResponse (Address address) {
		super(new Long(200), "OK", "Address updated and delivered.", address);
	}  

}
