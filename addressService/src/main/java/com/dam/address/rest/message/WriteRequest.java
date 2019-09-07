package com.dam.address.rest.message;

import com.dam.address.model.entity.Address;

public abstract class WriteRequest extends RestRequest {

	private Address address = new Address();	

    public WriteRequest(Long requestorUserId, Address address) {
		super("DAM 2.0");
		setAddress(address);
		setRequestorUserId(requestorUserId);
    }
        
    public Address getAddress() {
    	return address;
    }

	public void setAddress(Address address) {
		this.address = address;
	}
    
}