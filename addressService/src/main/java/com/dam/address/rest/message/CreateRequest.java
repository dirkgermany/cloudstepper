package com.dam.address.rest.message;

import com.dam.address.model.entity.Address;

public class CreateRequest extends WriteRequest {
	
    public CreateRequest(Long requestorUserId, Long personId, Address address) {
		super(requestorUserId, address);
    }
}