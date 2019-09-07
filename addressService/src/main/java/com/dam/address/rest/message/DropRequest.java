package com.dam.address.rest.message;

public class DropRequest extends RestRequest {

	private Long addressId;
	private Long personId;
	private Long userId;


    public DropRequest(Long requestorUserId, Long personId, Long addressId) {
		super("DAM 2.0");
		setPersonId(personId);
		setAddressId(addressId);
		setRequestorUserId(requestorUserId);
    }

	public Long getPersonId() {
		return personId;
	}

	public void setPersonId(Long personId) {
		this.personId = personId;
	}

	public Long getAddressId() {
		return addressId;
	}

	public void setAddressId(Long addressId) {
		this.addressId = addressId;
	}
	
	public Long getUserId() {
		return userId;
	}
	
	public void setUserId(Long userId) {
		this.userId = userId;
	}

}