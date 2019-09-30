package com.dam.jobService.task;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.dam.exception.DamServiceException;
import com.dam.jobService.JsonHelper;
import com.dam.jobService.ScheduledTasks;
import com.dam.jobService.TaskConfiguration;
import com.dam.jobService.rest.consumer.Consumer;
import com.fasterxml.jackson.databind.JsonNode;

@Component
public abstract class Client {
	@Autowired
	TaskConfiguration taskConfiguration;

	@Autowired
	Consumer consumer;
	
	private static String token;
	private JsonHelper jsonHelper = new JsonHelper();
	
	public abstract void executeJob () throws DamServiceException;
	private static final Logger logger = LoggerFactory.getLogger(ScheduledTasks.class);
	private static final DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("HH:mm:ss");
	
	protected JsonNode retrieveResponse(JsonNode node, String action) throws DamServiceException {
		
		if (null == token) {
			throw new DamServiceException(500L, "Request could not be sent", "Task Client is not logged in, Token is null");
		}

		String url = getServiceProviderUrl();
		node = jsonHelper.addToJsonNode(node, "tokenId", token);
		
		
		return consumer.retrieveResponse(jsonHelper.extractStringFromJsonNode(node), url, action);
	}
	
	public void logout (String className) throws DamServiceException {
		logger.info("Job Service Client :: {}: Logout {}", dateTimeFormatter.format(LocalDateTime.now()), className);
		token = null;
	}
		
	public void login (String className) throws DamServiceException {
		if (null != token) {
			logger.info("Job Service Client :: {}: Token: {} Class: {}", dateTimeFormatter.format(LocalDateTime.now()), this.token, className);
			return;
		}
		logger.info("Job Service Client :: {}: Login {}", dateTimeFormatter.format(LocalDateTime.now()), className);
		
		String userName = taskConfiguration.getUserName();
		String password = taskConfiguration.getPassword();

		JsonNode node = jsonHelper.createEmptyNode();
		node = jsonHelper.addToJsonNode(node, "userName", userName);
		node = jsonHelper.addToJsonNode(node, "password", password);
		
		String url = getServiceProviderUrl();
		
		JsonNode response = consumer.retrieveResponse(jsonHelper.extractStringFromJsonNode(node), url, "login");
		long returnCode = jsonHelper.extractLongFromNode(response, "returnCode");
		if (200L != returnCode) {
			String result  = jsonHelper.extractStringFromJsonNode(response, "result");
			String serviceName = jsonHelper.extractStringFromJsonNode(response, "serviceName");
			throw new DamServiceException(403L, "Login to  Service Provider failed", result + "; Service: " + serviceName);
		}
		token = jsonHelper.extractStringFromJsonNode(response, "tokenId");
	}
	
	protected String getServiceProviderUrl() {
		return taskConfiguration.getServiceProviderProtocol() + "://" +  taskConfiguration.getServiceProviderHost() + ":" + taskConfiguration.getServiceProviderPort();
	}

}
