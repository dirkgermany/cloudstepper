package com.dam.address.rest.message;

import java.util.List;

import com.dam.address.model.entity.Address;

public class AddressListResponse extends RestResponse{
    private List<Address> adressList;
    	
	public AddressListResponse (List<Address> adressList) {
		super(new Long(200), "OK", "Address(es) found and attached.");
		
		setAdressList(adressList);
	}  

	public List<Address> getAdressList() {
		return adressList;
	}

	public void setAdressList(List<Address> adressList) {
		this.adressList = adressList;
	}
	
}
