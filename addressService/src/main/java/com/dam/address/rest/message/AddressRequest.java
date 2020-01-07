package com.dam.address.rest.message;

import com.dam.address.types.AddressType;

public class AddressRequest extends RestRequest {
    private Long personId;
    private AddressType addressType;

    public AddressRequest( Long requestorUserId, Long personId, AddressType addressType) {
		super("CS 0.0.1");
		setRequestorUserId(requestorUserId);
		setPersonId(personId);
		setAddressType(addressType);
    }

	public Long getPersonId() {
		return personId;
	}

	public void setPersonId(Long personId) {
		this.personId = personId;
	}

	public AddressType getAddressType() {
		return addressType;
	}

	public void setAddressType(AddressType addressType) {
		this.addressType = addressType;
	}
    
}