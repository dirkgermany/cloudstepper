package com.dam.person;

import java.util.Iterator;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.stereotype.Controller;

import com.dam.exception.DamServiceException;
import com.dam.exception.PermissionCheckException;
import com.dam.person.model.PersonModel;
import com.dam.person.model.entity.Person;
import com.dam.person.rest.message.CreateRequest;
import com.dam.person.rest.message.DropRequest;
import com.dam.person.rest.message.PersonRequest;
import com.dam.person.rest.message.RestRequest;
import com.dam.person.rest.message.UpdateRequest;

/**
 * Handles active and non active Tokens
 * 
 * @author dirk
 *
 */
@Controller
@ComponentScan
public class PersonStore {

	@Autowired
	private PersonModel personModel;
	
	public long count() {
		return personModel.count();
	}

	/**
	 * Save getter for Person. Checks requesting user
	 * 
	 * @param userId
	 * @param person
	 * @return
	 */
	public Person getPersonSafe(PersonRequest personRequest) throws DamServiceException {
		checkRequestedParamsRequest_Id_Rights(personRequest, personRequest.getRequestorUserId(),
				personRequest.getRights());
	
		if (null == personRequest.getUserId() || null == personRequest.getPersonId()) {
			throw new DamServiceException(new Long(400), "Invalid Request", "userId or personId is not set.");
		}

		// Check if the permissions is set
		PermissionCheck.isReadPermissionSet(personRequest.getRequestorUserId(), personRequest.getUserId(),
				personRequest.getRights());

		Person person = getPersonById(personRequest.getPersonId());

		if (null == person) {
			throw new DamServiceException(new Long(404), "Person Unknown", "Person not found or invalid request");
		}

		return person;
	}


	/**
	 * Creation of person requests existing userId, givenName and lastName. userId
	 * in Entity Container mustn't be null
	 * 
	 * @param personContainer
	 * @return
	 * @throws PermissionCheckException
	 */
	public Person createPersonSafe(CreateRequest createRequest) throws DamServiceException {
		checkRequestedParamsRequest_Id_Rights(createRequest, createRequest.getRequestorUserId(),
				createRequest.getRights());
		
		checkRequestedParamsPerson(createRequest.getPerson());
		
		// Save database requests
		PermissionCheck.isWritePermissionSet(createRequest.getRequestorUserId(), createRequest.getPerson().getUserId(),
				createRequest.getRights());

		if (null == createRequest.getPerson().getGivenName() || createRequest.getPerson().getGivenName().isEmpty()
				|| null == createRequest.getPerson().getLastName()
				|| createRequest.getPerson().getLastName().isEmpty()) {
			throw new DamServiceException(new Long(400), "Invalid Request", "Person data not complete");
		}

		// check if still exists
		// direct by personId
		Person existingPerson = getPersonById(createRequest.getPerson().getPersonId());

		// searching by userId
		// One user may admin different persons
		if (null == existingPerson) {
			List<Person> personList = getPersonByUserId(createRequest.getPerson().getUserId());
			if (null != personList && !personList.isEmpty()) {
				Iterator<Person> it = personList.iterator();
				while (it.hasNext()) {
					Person person = it.next();
					if (person.getGivenName().equalsIgnoreCase(createRequest.getPerson().getGivenName())
							&& person.getLastName().equalsIgnoreCase(createRequest.getPerson().getLastName())) {
						existingPerson = person;
						break;
					}
				}
			}
		}

		// userId ist not permutable
		if (null != existingPerson) {
			// userId ist not permutable
			checkRequestorIdAndUserId(existingPerson.getUserId(), createRequest.getPerson().getUserId());

			return updatePerson(existingPerson, createRequest.getPerson());
		}

		// save if all checks are ok and the person doesn't exist
		Person person = personModel.save(createRequest.getPerson());

		if (null == person) {
			throw new DamServiceException(new Long(422), "Person not created",
					"Person still exists, data invalid or not complete");
		}
		return person;

	}

	/**
	 * Save update; requesting user must be user in storage
	 * 
	 * @param userId
	 * @param userStored
	 * @param userUpdate
	 * @return
	 */
	public Person updatePersonSafe(UpdateRequest updateRequest) throws DamServiceException {
		checkRequestedParamsRequest_Id_Rights(updateRequest, updateRequest.getRequestorUserId(),
				updateRequest.getRights());

		checkRequestedParamsPerson(updateRequest.getPerson());
		
		// Check if the permissions is set
		PermissionCheck.isWritePermissionSet(updateRequest.getRequestorUserId(), updateRequest.getPerson().getUserId(),
				updateRequest.getRights());		

		// check if still exists
		Person existingPerson = getPersonById(updateRequest.getPerson().getPersonId());

		// Person must exist and userId ist not permutable
		if (null == existingPerson) {
			throw new DamServiceException(new Long(404), "Person for update not found",
					"Person with personId doesn't exist.");
		}

		// userId ist not permutable
		checkRequestorIdAndUserId(existingPerson.getUserId(), updateRequest.getPerson().getUserId());

		return updatePerson(existingPerson, updateRequest.getPerson());
	}
	
	/**
	 * Secure drop, user must be owner
	 * 
	 * @param userId
	 * @param person
	 * @return
	 */
	public Long dropPersonSafe(DropRequest dropRequest) throws DamServiceException {
		checkRequestedParamsRequest_Id_Rights(dropRequest, dropRequest.getRequestorUserId(), dropRequest.getRights());
		checkRequestedParamsPerson(dropRequest.getPerson());

		// Save database requests
		PermissionCheck.isDeletePermissionSet(dropRequest.getRequestorUserId(), dropRequest.getPerson().getUserId(),
				dropRequest.getRights());

		Person existingPerson = getPersonById(dropRequest.getPerson().getPersonId());

		if (null == existingPerson) {
			throw new DamServiceException(new Long(404), "Person could not be dropped",
					"Person does not exist or could not be found in database.");
		}

		checkRequestorIdAndUserId(existingPerson.getUserId(), dropRequest.getPerson().getUserId());

		return dropPerson(existingPerson);

	}
	
	private Person getPersonById(Long personId) {
		if (null == personId) {
			return null;
		}

		Optional<Person> optionalPerson = personModel.findById(personId);
		if (null != optionalPerson && optionalPerson.isPresent()) {
			return optionalPerson.get();
		}
		return null;
	}

	private List<Person> getPersonByUserId(Long userId) {
		return personModel.findByUserId(userId);
	}


	/**
	 * Update person with changed values
	 * 
	 * @param personForUpdate
	 * @param personContainer
	 * @return
	 */
	private Person updatePerson(Person personForUpdate, Person personContainer) throws DamServiceException {
		if (null != personForUpdate && null != personContainer) {
			personForUpdate.updateEntity(personContainer);
			try {
				return personModel.save(personForUpdate);
			} catch (Exception e) {
				throw new DamServiceException(new Long(409), 
						"Person could not be saved. Perhaps duplicate keys.", e.getMessage());
			}
		}
		throw new DamServiceException(new Long(404), "Person could not be saved", "Check Person data in request.");
	}

	private Long dropPerson(Person person) {
		if (null != person) {
			personModel.deleteById(person.getPersonId());
			Person deletedPerson = getPersonById(person.getPersonId());
			if (null == deletedPerson) {
				return new Long(200);
			}
		}

		return new Long(10);
	}

	private void checkRequestedParamsRequest_Id_Rights(RestRequest request, Long requestorUserId, String rights)
			throws DamServiceException {
		if (null == request) {
			throw new DamServiceException(new Long(400), "Invalid Request", "Request is null.");
		}
		if (null == requestorUserId) {
			throw new DamServiceException(new Long(400), "Invalid Request",
					"requestorUserId is recommended but not set.");
		}
		if (null == rights || rights.isEmpty()) {
			throw new DamServiceException(new Long(400), "Invalid Request",
					"User rights are recommended but are null or empty.");
		}
	}

	private void checkRequestorIdAndUserId(Long requestedUserId, Long persistentUserId) throws DamServiceException {
		if (null != requestedUserId && null != persistentUserId && !requestedUserId.equals(persistentUserId)) {
			throw new DamServiceException(new Long(400), "Invalid Request",
					"userId in request and userId in database are not the same. userId is not permutable.");
		}
	}

	private void checkRequestedParamsPerson(Person person) throws DamServiceException {
		if (null == person || null == person.getUserId()) {
			throw new DamServiceException(new Long(400), "Invalid Request", "Person is null or person userId is null.");
		}
	}

}
