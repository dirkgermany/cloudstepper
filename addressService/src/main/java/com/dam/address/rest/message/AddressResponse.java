package com.dam.address.rest.message;

import com.dam.address.model.entity.Address;

public class AddressResponse extends RestResponse{
    private Address address;
    	
	public AddressResponse (Address address) {
		super(new Long(200), "OK", "Address found and attached.");
		
		setAddress(address);
	}  

	private void setAddress(Address address) {
		this.address = address;
	}
	
	public Address getAddress() {
		return this.address;
	}
	
}
