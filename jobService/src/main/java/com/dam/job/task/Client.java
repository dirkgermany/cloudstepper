package com.dam.job.task;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.dam.exception.DamServiceException;
import com.dam.job.JsonHelper;
import com.dam.job.TaskConfiguration;
import com.dam.job.messageClass.Intent;
import com.dam.job.rest.consumer.Consumer;
import com.dam.job.rest.message.RestRequestIntent;
import com.dam.job.type.ActionType;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;

/**
 * Client of Service Provider. Used to communicate with the service.
 * 
 * @author dirk
 *
 */
@Component
public abstract class Client {
	@Autowired
	TaskConfiguration taskConfiguration;

	@Autowired
	Consumer consumer;

	private static String token;
	protected JsonHelper jsonHelper = new JsonHelper();
	protected final static Integer MAX_RETRIES = 10;

	public abstract void executeJob() throws DamServiceException;

	protected static final DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("HH:mm:ss");
	protected static final Logger logger = LoggerFactory.getLogger(Client.class);

	public void logout(String className) throws DamServiceException {
		logger.info("Job Service Client :: {}: Logout {}", dateTimeFormatter.format(LocalDateTime.now()), className);
		token = null;
	}

	/*
	 * Login to Service Provider. Tries to use the Token if set.
	 */
	protected void login(String className) throws DamServiceException {
		if (null != token) {
			logger.info("Job Service Client :: {}: Token: {} Class: {}", dateTimeFormatter.format(LocalDateTime.now()),
					Client.token, className);
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
			String result = jsonHelper.extractStringFromJsonNode(response, "result");
			String serviceName = jsonHelper.extractStringFromJsonNode(response, "serviceName");
			throw new DamServiceException(403L, "Login to  Service Provider failed",
					result + "; Service: " + serviceName);
		}
		token = jsonHelper.extractStringFromJsonNode(response, "tokenId");
	}

	/*
	 * Send request to Service Provider.
	 */
	protected JsonNode sendRequest(JsonNode node, String path) throws DamServiceException {
		String url = getServiceProviderUrl();

		jsonHelper.addToJsonNode(node, "tokenId", Client.token);

		JsonNode response = consumer.retrieveResponse(jsonHelper.extractStringFromJsonNode(node), url, path);
		long returnCode = jsonHelper.extractLongFromNode(response, "returnCode");
		if (200L != returnCode) {
			String result = jsonHelper.extractStringFromJsonNode(response, "result");
			String serviceName = jsonHelper.extractStringFromJsonNode(response, "serviceName");
			throw new DamServiceException(403L, "Request " + path + " failed", result + "; Service: " + serviceName);
		}
		return response;
	}

	protected String getServiceProviderUrl() {
		return taskConfiguration.getServiceProviderProtocol() + "://" + taskConfiguration.getServiceProviderHost() + ":"
				+ taskConfiguration.getServiceProviderPort();
	}

	/*
	 * Requests the Service Provider for a List with TransferToDepot Intents.
	 */
	protected List<Intent> getIntentList(ActionType action, String domain, String path, String nodeName)
			throws DamServiceException {
		Intent searchIntent = new Intent();
		searchIntent.setAction(action);
		searchIntent.setBooked(false);

		RestRequestIntent restRequest = new RestRequestIntent(searchIntent);

		JsonNode node = jsonHelper.getObjectMapper().valueToTree(restRequest);
		JsonNode response = sendRequest(node, domain + "/" + path);

		JsonNode jsonIntentList = jsonHelper.extractNodeFromNode(response, nodeName);

		List<Intent> intentList = new ArrayList<>();
		if (null != jsonIntentList) {
			try {
				if (jsonIntentList.isArray()) {
					for (JsonNode arrayItem : jsonIntentList) {
						intentList.add(jsonHelper.getObjectMapper().treeToValue(arrayItem, Intent.class));
					}
				} else if (jsonIntentList.isObject()) {
					intentList.add(jsonHelper.getObjectMapper().treeToValue(jsonIntentList, Intent.class));
				}
			} catch (JsonProcessingException e) {
			throw new DamServiceException(500L, "Job :: Fehler bei Bearbeitung der Response", e.getMessage());
			}
		}
		return intentList;
	}

	/*
	 * Call Service Provider for updating the status of the dependent entities.
	 */
	protected void confirmIntent(Intent intent, ActionType action, String domain, String path)
			throws DamServiceException {
		intent.setAction(action);
		intent.setAccepted(true);
		intent.setBookingDate(LocalDateTime.now());

		RestRequestIntent restRequest = new RestRequestIntent(intent);

		JsonNode node = jsonHelper.getObjectMapper().valueToTree(restRequest);

		boolean success = false;
		int retryCount = 0;
		while (!success && retryCount++ < MAX_RETRIES) {
			try {
				sendRequest(node, domain + "/" + path);
			} catch (DamServiceException d) {
				logger.info("Job Service Client :: {}: confirmIntent retry {} of {}",
						dateTimeFormatter.format(LocalDateTime.now()), retryCount, MAX_RETRIES);
			}
			success = true;
		}
		if (retryCount >= MAX_RETRIES) {
			throw new DamServiceException(400L, "Confirming intent failed",
					"Max. retries reached but access to Intents was not successfull");
		}
	}

	/**
	 * Call Service Provider and tell him that the intent was declined.
	 * 
	 * @param intent
	 * @param action
	 * @param domain
	 * @param path
	 * @throws DamServiceException
	 */
	protected void declineIntent(Intent intent, ActionType action, String domain, String path)
			throws DamServiceException {
		intent.setAction(action);
		intent.setAccepted(false);
		intent.setBookingDate(LocalDateTime.now());

		RestRequestIntent restRequest = new RestRequestIntent(intent);
		JsonNode node = jsonHelper.getObjectMapper().valueToTree(restRequest);
		sendRequest(node, domain + "/" + path);
	}
}
