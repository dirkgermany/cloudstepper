package com.dam.authentication.rest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import com.dam.authentication.PingFactory;
import com.fasterxml.jackson.databind.JsonNode;

@CrossOrigin
@RestController
public class Ping {
	
	@Autowired
	private PingFactory pingFactory;

	/**
	 * Retrieves informations about service healthy.
	 * 
	 * @param request
	 * @return
	 */
	@GetMapping("/ping")
	public JsonNode ping() {
		pingFactory.init("AuthenticationService");			
		return pingFactory.getNode();
	}
	
}