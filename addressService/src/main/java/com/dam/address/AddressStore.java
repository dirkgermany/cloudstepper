package com.dam.address;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.stereotype.Controller;

import com.dam.address.model.AddressModel;
import com.dam.address.model.entity.Address;
import com.dam.address.rest.message.AddressRequest;
import com.dam.address.rest.message.CreateRequest;
import com.dam.address.rest.message.DropRequest;
import com.dam.address.rest.message.RestRequest;
import com.dam.address.rest.message.UpdateRequest;
import com.dam.address.types.AddressType;
import com.dam.exception.DamServiceException;
import com.dam.exception.PermissionCheckException;

/**
 * Handles active and non active Tokens
 * 
 * @author dirk
 *
 */
@Controller
@ComponentScan
public class AddressStore {

	@Autowired
	private AddressModel addressModel;
	
	public long count() {
		return addressModel.count();
	}

	/**
	 * Retrieves addresses of a person.
	 * 
	 * @param givenName
	 * @param lastName
	 * @return
	 */
	public List<Address> getAddressListSafe(AddressRequest addressRequest) throws DamServiceException {
		checkRequestedParamsRequest_Id_Rights(addressRequest, addressRequest.getRequestorUserId(),
				addressRequest.getRights());

		// Performance Check - saves database calls
		// When the calling user has no permissions return
		PermissionCheck.isReadPermissionSetInGeneral(addressRequest.getRights());

		List<Address> addresses = getAddressListByPersonId(addressRequest.getPersonId());

		if (null == addresses || addresses.size() == 0) {
			throw new DamServiceException( 1L, "Address Unknown", "Address not found or invalid request");
		}

		// Filtering addresses with user permissions
		List<Address> returnList = new ArrayList<>();
		Iterator<Address> it = addresses.iterator();
		while (it.hasNext()) {
			Address address = it.next();
			try {
				PermissionCheck.isReadPermissionSet(addressRequest.getRequestorUserId(), address.getUserId(),
						addressRequest.getRights());
			} catch (PermissionCheckException e) {
				// nothing to do
			}
			returnList.add(address);
		}

		if (null == returnList || returnList.size() == 0) {
			throw new DamServiceException( 404L, "Address not found", "Address does not exist.");
		}

		return returnList;
	}

	/**
	 * Save getter for Address. Checks requesting user
	 * 
	 * @return
	 */
	public Address getAddressSafe(AddressRequest addressRequest) throws DamServiceException {
		checkRequestedParamsRequest_Id_Rights(addressRequest, addressRequest.getRequestorUserId(),
				addressRequest.getRights());

		// Performance Check - saves database calls
		// When the calling user has no permissions return
		PermissionCheck.isReadPermissionSetInGeneral(addressRequest.getRights());

		if (null == addressRequest.getPersonId()) {
			throw new DamServiceException(400L, "Invalid Request", "personId is not set.");
		}

		// deliver PRIMARY ADDRESS if type is not set with parameter
		if (null == addressRequest.getAddressType()) {
			addressRequest.setAddressType(AddressType.PRIMARY);
		}
		Address storedAddress = getAddressByPerson_Type(addressRequest.getPersonId(), addressRequest.getAddressType());

		if (null != storedAddress) {
			PermissionCheck.isReadPermissionSet(addressRequest.getRequestorUserId(), storedAddress.getUserId(),
					addressRequest.getRights());
			return storedAddress;
		}

		throw new DamServiceException(404L, "Address Unknown", "Address not found or invalid request");
	}

	/**
	 * Creation or overwrite of address requests existing userId and personId. If
	 * addressContainer.personId is not null, personId and addressContainer.personId
	 * must match.
	 * 
	 * @param person
	 * @return
	 */
	public Address createAddressSafe(CreateRequest createRequest) throws DamServiceException {
		checkRequestedParamsRequest_Id_Rights(createRequest, createRequest.getRequestorUserId(),
				createRequest.getRights());

		checkRequestedParamsAddress(createRequest.getAddress());

		// Performance Check - saves database calls
		// When the calling user has no permissions return
		PermissionCheck.isWritePermissionSet(createRequest.getRequestorUserId(), createRequest.getAddress().getUserId(),
				createRequest.getRights());

		// check if still exists
		// personId and type are unique
		Address existingAddress = getAddressByUser_Person_Type(createRequest.getAddress().getUserId(),
				createRequest.getAddress().getPersonId(), createRequest.getAddress().getAddressType());

		if (null != existingAddress) {
			return updateAddress(existingAddress, createRequest.getAddress());
		}

		return addressModel.save(createRequest.getAddress());
	}

	/**
	 * Save update; requesting user must be user in storage
	 * 
	 * @return
	 */
	public Address updateAddressSafe(UpdateRequest updateRequest) throws DamServiceException {
		checkRequestedParamsRequest_Id_Rights(updateRequest, updateRequest.getRequestorUserId(),
				updateRequest.getRights());

		checkRequestedParamsAddress(updateRequest.getAddress());

		// Performance Check - saves database calls
		// When the calling user has no permissions return
		PermissionCheck.isWritePermissionSetInGeneral(updateRequest.getRights());

		Address addressForUpdate = getAddressByUser_Person_Type(updateRequest.getAddress().getUserId(),
				updateRequest.getAddress().getPersonId(), updateRequest.getAddress().getAddressType());

		if (null != addressForUpdate) {
			PermissionCheck.isWritePermissionSet(updateRequest.getRequestorUserId(), addressForUpdate.getUserId(),
					updateRequest.getRights());

			return updateAddress(addressForUpdate, updateRequest.getAddress());
		}

		throw new DamServiceException(404L, "Address for update not found",
				"Address with userId and personId and type doesn't exist.");

	}

	/**
	 * Deletes all addresses of a person.
	 * 
	 * @param requestorUserId must be the owner of the records.
	 * @param personId        Identifier for the addresses
	 * @return Long(0) if no person address exists after operation. > 0 if the
	 *         operation couldn't be performed correct or complete.
	 * @throws DamServiceException
	 */
	public Long dropAllAddressesSafe(DropRequest dropRequest) throws DamServiceException {
		checkRequestedParamsRequest_Id_Rights(dropRequest, dropRequest.getRequestorUserId(), dropRequest.getRights());

		// Performance Check - saves database calls
		// When the calling user has no permissions return
		PermissionCheck.isDeletePermissionSetInGeneral(dropRequest.getRights());

		if (null == dropRequest.getUserId() || null == dropRequest.getPersonId()) {
			throw new DamServiceException(400L, "Invalid Request", "userId or personId is null.");
		}

		List<Address> addressList = getAddressListByPersonId(dropRequest.getPersonId());
		if (null != addressList && !addressList.isEmpty()) {
			Iterator<Address> it = addressList.iterator();
			while (it.hasNext()) {
				Address address = it.next();
				PermissionCheck.isDeletePermissionSet(dropRequest.getRequestorUserId(), address.getUserId(),
						dropRequest.getRights());

				addressModel.delete(address);
			}
		}

		throw new DamServiceException(404L, "No Address for deletion found",
				"Address with userId and personId and type doesn't exist.");
	}

	/**
	 * Secure drop, user must be owner
	 * 
	 * @param userId
	 * @param person
	 * @return
	 */
	public Long dropAddressSafe(DropRequest dropRequest) throws DamServiceException {
		checkRequestedParamsRequest_Id_Rights(dropRequest, dropRequest.getRequestorUserId(), dropRequest.getRights());

		// Performance Check - saves database calls
		// When the calling user has no permissions return
		PermissionCheck.isDeletePermissionSetInGeneral(dropRequest.getRights());

		Address addressToDelete = getAddressById(dropRequest.getAddressId());
		
		if (null != addressToDelete) {
			PermissionCheck.isDeletePermissionSet(dropRequest.getRequestorUserId(), addressToDelete.getUserId(),
					dropRequest.getRights());
			
			return dropAddress(addressToDelete);
		}

		throw new DamServiceException(404L, "No Address for deletion found",
				"Address with userId and personId and type doesn't exist.");
	}

	/**
	 * Retrieves addresses of a person.
	 * 
	 * @param givenName
	 * @param lastName
	 * @return
	 */
	private List<Address> getAddressListByPersonId(Long personId) {
		return addressModel.getAddressesByPersonId(personId);
	}

	/**
	 * Update address with changed values
	 * 
	 * @param addressForUpdate
	 * @param addressContainer
	 * @return
	 */
	private Address updateAddress(Address addressForUpdate, Address addressContainer) {
		if (null != addressForUpdate && null != addressContainer) {
			addressForUpdate.updateEntity(addressContainer);
			return addressModel.save(addressForUpdate);
		}
		return null;
	}

	private Address getAddressById(Long addressId) {
		Optional<Address> optionalAddress = addressModel.findById(addressId);
		if (null != optionalAddress && optionalAddress.isPresent()) {
			return optionalAddress.get();
		}
		return null;
	}

	private Address getAddressByPerson_Type(Long personId, AddressType addressType) {
		return addressModel.getAddressByPerson_Type(personId, addressType);
	}

	private Address getAddressByUser_Person_Type(Long userId, Long personId, AddressType addressType) {
		return addressModel.getAddressByUser_Person_Type(userId, personId, addressType);
	}

	private Long dropAddress(Address addressContainer) {
		if (null != addressContainer) {
			addressModel.deleteById(addressContainer.getAddressId());
			Address deletedAddress = getAddressById(addressContainer.getAddressId());
			if (null == deletedAddress) {
				return 200L;
			}
		}

		return 10L;
	}

	private void checkRequestedParamsRequest_Id_Rights(RestRequest request, Long requestorUserId, String rights)
			throws DamServiceException {
		if (null == request) {
			throw new DamServiceException(400L, "Invalid Request", "Request is null.");
		}
		if (null == requestorUserId) {
			throw new DamServiceException(400L, "Invalid Request",
					"requestorUserId is recommended but not set.");
		}
		if (null == rights || rights.isEmpty()) {
			throw new DamServiceException(400L, "Invalid Request",
					"User rights are recommended but are null or empty.");
		}
	}

	private void checkRequestedParamsAddress(Address address) throws DamServiceException {
		if (null == address || null == address.getUserId() || null == address.getPersonId()) {
			throw new DamServiceException(400L, "Invalid Request", "Address, userId or personId is null.");
		}
	}
}
