package com.dam.address.rest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.dam.address.AddressStore;
import com.dam.address.rest.message.CreateRequest;
import com.dam.address.rest.message.CreateResponse;
import com.dam.address.rest.message.DropRequest;
import com.dam.address.rest.message.DropResponse;
import com.dam.address.rest.message.AddressListResponse;
import com.dam.address.rest.message.AddressRequest;
import com.dam.address.rest.message.AddressResponse;
import com.dam.address.rest.message.RestResponse;
import com.dam.address.rest.message.UpdateRequest;
import com.dam.address.rest.message.UpdateResponse;
import com.dam.exception.DamServiceException;

@RestController
public class AddressController {
	@Autowired
	private AddressStore addressStore;

	/**
	 * Retrieves an address
	 * 
	 * @param addressRequest
	 * @return
	 */
	@PostMapping("/getAddress")
	public RestResponse getAddressResponse(@RequestBody AddressRequest addressRequest) throws DamServiceException {
		try {
			return new AddressResponse(addressStore.getAddressSafe(addressRequest));
		} catch (DamServiceException e) {
			return new RestResponse(e.getErrorId(), e.getShortMsg(), e.getDescription());
		}
	}

	/**
	 * Retrieves one or more addresses
	 * 
	 * @param addressRequest
	 * @return
	 */
	@PostMapping("/listAddresses")
	public RestResponse listAddressesResponse(@RequestBody AddressRequest addressRequest) throws DamServiceException {
		try {
			return new AddressListResponse(addressStore.getAddressListSafe(addressRequest));
		} catch (DamServiceException e) {
			return new RestResponse(e.getErrorId(), e.getShortMsg(), e.getDescription());
		}
	}

	@PostMapping("/createAddress")
	public RestResponse createAddress(@RequestBody CreateRequest createRequest) throws DamServiceException {
		try {
			return new CreateResponse(addressStore.createAddressSafe(createRequest));
		} catch (DamServiceException e) {
			return new RestResponse(e.getErrorId(), e.getShortMsg(), e.getDescription());
		}
	}

	@PostMapping("/dropAddress")
	public RestResponse dropAddress(@RequestBody DropRequest dropRequest) throws DamServiceException {
		try {
			return new DropResponse(addressStore.dropAddressSafe(dropRequest));
		} catch (DamServiceException e) {
			return new RestResponse(e.getErrorId(), e.getShortMsg(), e.getDescription());
		}
	}

	@PostMapping("/updateAddress")
	public RestResponse updateAddress(@RequestBody UpdateRequest updateRequest) throws DamServiceException {
		try {
			return new UpdateResponse(addressStore.updateAddressSafe(updateRequest));
		} catch (DamServiceException e) {
			return new RestResponse(e.getErrorId(), e.getShortMsg(), e.getDescription());
		}
	}
}