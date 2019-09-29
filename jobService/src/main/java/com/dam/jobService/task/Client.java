package com.dam.jobService.task;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.dam.exception.DamServiceException;
import com.dam.jobService.JsonHelper;
import com.dam.jobService.TaskConfiguration;
import com.dam.jobService.rest.consumer.Consumer;
import com.fasterxml.jackson.databind.JsonNode;

@Component
public class Client {
	@Autowired
	TaskConfiguration taskConfiguration;

	@Autowired
	Consumer consumer;
	
	private String token;
	private JsonHelper jsonHelper = new JsonHelper();

	protected JsonNode retrieveResponse(JsonNode node, String action) throws DamServiceException {
		
		if (null == token) {
			throw new DamServiceException(500L, "Request could not be sent", "Task Client is not logged in, Token is null");
		}

		String url = getServiceProviderUrl();
		node = jsonHelper.addToJsonNode(node, "tokenId", token);
		
		
		return consumer.retrieveResponse(jsonHelper.extractStringFromJsonNode(node), url, action);
	}
	
	protected void login () throws DamServiceException {
		String userName = taskConfiguration.getUserName();
		String password = taskConfiguration.getPassword();

		JsonNode node = jsonHelper.createEmptyNode();
		node = jsonHelper.addToJsonNode(node, "userName", userName);
		node = jsonHelper.addToJsonNode(node, "password", password);
		
		String url = getServiceProviderUrl();
		
		JsonNode response = consumer.retrieveResponse(jsonHelper.extractStringFromJsonNode(node), url, "login");
		token = jsonHelper.extractStringFromJsonNode(response, "tokenId");
	}
	
	protected String getServiceProviderUrl() {
		return taskConfiguration.getServiceProviderProtocol() + "://" +  taskConfiguration.getServiceProviderHost() + ":" + taskConfiguration.getServiceProviderPort();
	}

}
