package com.dam.userService.rest;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import com.dam.userService.PingFactory;
import com.fasterxml.jackson.databind.JsonNode;

@RestController
public class Ping {

	/**
	 * Retrieves informations about service healthy.
	 * 
	 * @param request
	 * @return
	 */
	@GetMapping("/ping")
	public JsonNode ping() {
		PingFactory pingFactory = new PingFactory("UserRepository");			
		return pingFactory.getNode();
	}
	
}