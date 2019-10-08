package com.dam.stock.task;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.dam.exception.DamServiceException;
import com.dam.stock.JsonHelper;
import com.dam.stock.Configuration;
import com.dam.stock.messageClass.Intent;
import com.dam.stock.model.entity.AssetClass;
import com.dam.stock.model.entity.StockHistory;
import com.dam.stock.rest.consumer.Consumer;
import com.dam.stock.rest.message.stock.StockHistoryRequest;
import com.dam.stock.type.ActionType;
import com.dam.stock.type.Symbol;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;

/**
 * Client of Service Provider.
 * Used to communicate with the service.
 * @author dirk
 *
 */
@Component
public abstract class Client {
	@Autowired
	Configuration configuration;

	@Autowired
	Consumer consumer;
	
	private static String token;
	protected JsonHelper jsonHelper = new JsonHelper();
	
	public abstract void executeJob () throws DamServiceException;
	private static final Logger logger = LoggerFactory.getLogger(ScheduledTasks.class);
	private static final DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("HH:mm:ss");
	
	
	public void logout (String className) throws DamServiceException {
		logger.info("Job Service Client :: {}: Logout {}", dateTimeFormatter.format(LocalDateTime.now()), className);
		token = null;
	}
		
	/*
	 * Login to Service Provider.
	 * Tries to use the Token if set.
	 */
	protected void login (String className) throws DamServiceException {
		if (null != token) {
			logger.info("Job Service Client :: {}: Token: {} Class: {}", dateTimeFormatter.format(LocalDateTime.now()), Client.token, className);
			return;
		}
		logger.info("Job Service Client :: {}: Login {}", dateTimeFormatter.format(LocalDateTime.now()), className);
		
		String userName = configuration.getUserName();
		String password = configuration.getPassword();

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
	
	/*
	 * Send request to Service Provider.
	 */
	protected JsonNode sendRequest(JsonNode node, String path) throws DamServiceException {
		String url = getServiceProviderUrl();
		
		jsonHelper.addToJsonNode(node, "tokenId", Client.token);
		
		JsonNode response = consumer.retrieveResponse(jsonHelper.extractStringFromJsonNode(node), url, path);
		long returnCode = jsonHelper.extractLongFromNode(response, "returnCode");
		if (200L != returnCode) {
			String result  = jsonHelper.extractStringFromJsonNode(response, "result");
			String serviceName = jsonHelper.extractStringFromJsonNode(response, "serviceName");
			throw new DamServiceException(403L, "Request " + path + " failed", result + "; Service: " + serviceName);
		}
		return response;
	}
	
	protected String getServiceProviderUrl() {
		return configuration.getServiceProviderProtocol() + "://" +  configuration.getServiceProviderHost() + ":" + configuration.getServiceProviderPort();
	}
	
	/*
	 * Requests the Service Provider for a List with TransferToDepot Intents.
	 */
	protected List<Intent> getIntentList(Symbol symbol, String domain, String path, String nodeName) throws DamServiceException {
		StockHistory stockHistory = new StockHistory();

		StockHistoryRequest restRequest = new StockHistoryRequest(stockHistory);

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
				throw new DamServiceException(500L, "Job :: Fehler bei Bearbeitung der Response",
						e.getMessage());
			}
		}
		return intentList;
	}

	/*
	 * Call Service Provider for updating the status of the dependent entities.
	 */
	protected void confirmIntent(StockHistory stockHistory, Symbol symbol, String domain, String path) throws DamServiceException {
		stockHistory.setSymbol(symbol);

		StockHistoryRequest restRequest = new StockHistoryRequest(stockHistory);

		JsonNode node = jsonHelper.getObjectMapper().valueToTree(restRequest);
		sendRequest(node, domain + "/" + path);
	}

	/**
	 * Call Service Provider and tell him that the intent was declined.
	 * @param intent
	 * @param action
	 * @param domain
	 * @param path
	 * @throws DamServiceException
	 */
	protected void declineIntent(StockHistory stockHistory, Symbol symbol, String domain, String path) throws DamServiceException {
		stockHistory.setSymbol(symbol);
		
		StockHistoryRequest restRequest = new StockHistoryRequest(stockHistory);

		JsonNode node = jsonHelper.getObjectMapper().valueToTree(restRequest);
		sendRequest(node, domain + "/" + path);
	}
}
