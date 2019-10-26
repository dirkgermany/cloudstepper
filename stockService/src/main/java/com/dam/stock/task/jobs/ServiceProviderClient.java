package com.dam.stock.task.jobs;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.dam.exception.DamServiceException;
import com.dam.stock.Configuration;
import com.dam.stock.messageClass.AssetClass;
import com.dam.stock.model.entity.StockHistory;
import com.dam.stock.rest.message.stock.StockHistoryRequest;
import com.dam.stock.task.Client;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;

@Component
public class ServiceProviderClient extends Client {
	@Autowired
	Configuration configuration;

	protected final static String STOCK_DOMAIN = "ASSET";
	protected final static String NODE_RESPONSE_ASSET_CLASS_LIST = "assetClassList";
	protected final static String PATH_LIST_ASSET_CLASS = "getAssetClassList";
		
	private static final Logger logger = LoggerFactory.getLogger(ServiceProviderClient.class);
	private static final DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("HH:mm:ss");
	/*
	 * Requests the Service Provider for a List with TransferToDepot Intents.
	 */
	public List<AssetClass> getAssetClassList() throws DamServiceException {
		StockHistory stockHistory = new StockHistory();

		StockHistoryRequest restRequest = new StockHistoryRequest(stockHistory, null, null);

		JsonNode node = jsonHelper.getObjectMapper().valueToTree(restRequest);
		JsonNode response = sendPostRequest(node, getServiceProviderUrl(), STOCK_DOMAIN + "/" + PATH_LIST_ASSET_CLASS);

		JsonNode jsonIntentList = jsonHelper.extractNodeFromNode(response, NODE_RESPONSE_ASSET_CLASS_LIST);

		List<AssetClass> assetClassList = new ArrayList<>();
		if (null != jsonIntentList) {
			try {
				if (jsonIntentList.isArray()) {
					for (JsonNode arrayItem : jsonIntentList) {
						assetClassList.add(jsonHelper.getObjectMapper().treeToValue(arrayItem, AssetClass.class));
					}
				} else if (jsonIntentList.isObject()) {
					assetClassList.add(jsonHelper.getObjectMapper().treeToValue(jsonIntentList, AssetClass.class));
				}
			} catch (JsonProcessingException e) {
				throw new DamServiceException(500L, "Job :: Fehler bei Bearbeitung der Response",
						e.getMessage());
			}
		}
		return assetClassList;
	}
	
	private String getServiceProviderUrl() {
		return configuration.getServiceProviderProtocol() + "://" +  configuration.getServiceProviderHost() + ":" + configuration.getServiceProviderPort();
	}
	
	/*
	 * Login to Service Provider.
	 * Tries to use the Token if set.
	 */
	public void login (String className) throws DamServiceException {
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
		
		JsonNode response = serviceProviderConsumer.retrieveResponse(jsonHelper.extractStringFromJsonNode(node), url, "login");
		long returnCode = jsonHelper.extractLongFromNode(response, "returnCode");
		if (200L != returnCode) {
			String result  = jsonHelper.extractStringFromJsonNode(response, "result");
			String serviceName = jsonHelper.extractStringFromJsonNode(response, "serviceName");
			throw new DamServiceException(403L, "Login to  Service Provider failed", result + "; Service: " + serviceName);
		}
		token = jsonHelper.extractStringFromJsonNode(response, "tokenId");
	}

	public void logout (String className) throws DamServiceException {
		logger.info("Job Service Client :: {}: Logout {}", dateTimeFormatter.format(LocalDateTime.now()), className);
		token = null;
	}
}
