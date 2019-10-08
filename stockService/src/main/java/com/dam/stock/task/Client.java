package com.dam.stock.task;

import java.time.format.DateTimeFormatter;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import com.dam.exception.DamServiceException;
import com.dam.stock.JsonHelper;
import com.dam.stock.rest.consumer.ServiceProviderConsumer;
import com.fasterxml.jackson.databind.JsonNode;

/**
 * Client of Service Provider.
 * Used to communicate with the service.
 * @author dirk
 *
 */
@Component
public class Client {

	@Autowired
	protected ServiceProviderConsumer serviceProviderConsumer;
	
	protected JsonHelper jsonHelper = new JsonHelper();
	protected static String token;

	private static final Logger logger = LoggerFactory.getLogger(Client.class);
	private static final DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("HH:mm:ss");
	
	/*
	 * Send request to Service Provider.
	 */
	protected JsonNode sendPostRequest(JsonNode node, String url, String path) throws DamServiceException {		
		jsonHelper.addToJsonNode(node, "tokenId", Client.token);
		
		JsonNode response = serviceProviderConsumer.retrieveResponse(jsonHelper.extractStringFromJsonNode(node), url, path);
		long returnCode = jsonHelper.extractLongFromNode(response, "returnCode");
		if (200L != returnCode) {
			String result  = jsonHelper.extractStringFromJsonNode(response, "result");
			String serviceName = jsonHelper.extractStringFromJsonNode(response, "serviceName");
			throw new DamServiceException(403L, "Request " + path + " failed", result + "; Service: " + serviceName);
		}
		return response;
	}
	
	protected JsonNode sendGetRequest(String url, String params) throws DamServiceException {
		return serviceProviderConsumer.retrieveGetResponse(url, params);
	}
	
}
