package com.dam.address.rest.message;

import com.dam.address.model.entity.Address;

public abstract class WriteResponse extends RestResponse{
    private Address address;
	
	public WriteResponse(Long result, String shortStatus, String longStatus, Address address) {
		super(result, shortStatus, longStatus);
		setAddress(address);
	}

	private void setAddress(Address address) {
		this.address = address;
	}
	
	public Address getAddress() {
		return address;
	}
	
}
