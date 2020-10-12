package com.dam.person.rest;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.dam.exception.DamServiceException;
import com.dam.person.PersonStore;
import com.dam.person.rest.message.CreateRequest;
import com.dam.person.rest.message.CreateResponse;
import com.dam.person.rest.message.DropRequest;
import com.dam.person.rest.message.DropResponse;
import com.dam.person.rest.message.RestResponse;
import com.dam.person.rest.message.UpdateRequest;
import com.dam.person.rest.message.UpdateResponse;
import com.dam.person.rest.message.PersonRequest;
import com.dam.person.rest.message.PersonResponse;

@RestController
public class PersonController {
	@Autowired
	private PersonStore personStore;

	/**
	 * Retrieves a person
	 * 
	 * @param personRequest
	 * @return
	 */
	@GetMapping("/getPerson")
	public RestResponse getPersonResponse(@RequestParam Map<String, String> params, @RequestHeader Map<String, String> headers) throws DamServiceException {
		try {
			return new PersonResponse(personStore.getPersonSafe(params, headers));
		} catch (DamServiceException e) {
			return new RestResponse(e.getErrorId(), e.getShortMsg(), e.getDescription());
		}
	}

	@PostMapping("/createPerson")
	public RestResponse createPerson(@RequestBody CreateRequest createRequest) throws DamServiceException {
		try {
			return new CreateResponse(personStore.createPersonSafe(createRequest));
		} catch (DamServiceException e) {
			return new RestResponse(e.getErrorId(), e.getShortMsg(), e.getDescription());
		}
	}

	@PostMapping("/dropPerson")
	public RestResponse dropPerson(@RequestBody DropRequest dropRequest) throws DamServiceException {
		try {
			return new DropResponse(personStore.dropPersonSafe(dropRequest));
		} catch (DamServiceException e) {
			return new RestResponse(e.getErrorId(), e.getShortMsg(), e.getDescription());
		}
	}

	@PostMapping("/updatePerson")
	public RestResponse updatePerson(@RequestBody UpdateRequest updateRequest) throws DamServiceException {
		try {
			return new UpdateResponse(personStore.updatePersonSafe(updateRequest));
		} catch (DamServiceException e) {
			return new RestResponse(e.getErrorId(), e.getShortMsg(), e.getDescription());
		}
	}

}