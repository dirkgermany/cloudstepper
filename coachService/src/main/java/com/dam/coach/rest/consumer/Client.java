package com.dam.coach.rest.consumer;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.dam.coach.Configuration;
import com.dam.coach.JsonHelper;
import com.dam.exception.DamServiceException;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;

/**
 * Client of Service Provider. Used to communicate with the service.
 * 
 * @author dirk
 *
 */
@Component
public class Client {
	@Autowired
	Configuration configuration;

	@Autowired
	Consumer consumer;

	private static String token;
	protected JsonHelper jsonHelper = new JsonHelper();

	private static final Logger logger = LoggerFactory.getLogger(Client.class);
	private static final DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("HH:mm:ss");

	public void logout(String className) throws DamServiceException {
		logger.info("Coach Service Client :: {}: Logout {}", dateTimeFormatter.format(LocalDateTime.now()), className);
		token = null;
	}

	/*
	 * Login to Service Provider. Tries to use the Token if set.
	 */
	protected void login() throws DamServiceException {
		if (null != token) {
			return;
		}
		logger.info("Portfolio Client :: {}: Login {}", dateTimeFormatter.format(LocalDateTime.now()),
				this.getClass().getName());

		String anotherUserName = configuration.getAuthName();
		String password = configuration.getPassword();

		JsonNode node = jsonHelper.createEmptyNode();
		node = jsonHelper.addToJsonNode(node, "userName", anotherUserName);
		node = jsonHelper.addToJsonNode(node, "password", password);

		String url = getServiceProviderUrl();

		try {
			JsonNode response = consumer.retrieveResponse(jsonHelper.extractStringFromJsonNode(node), url, "login");
			long returnCode = jsonHelper.extractLongFromNode(response, "returnCode");
			if (200L != returnCode) {
				String result = jsonHelper.extractStringFromJsonNode(response, "result");
				String serviceName = jsonHelper.extractStringFromJsonNode(response, "serviceName");
				throw new DamServiceException(403L, "Login to  Service Provider failed",
						result + "; Service: " + serviceName);
			}
			token = jsonHelper.extractStringFromJsonNode(response, "tokenId");
		} catch (Exception e) {
			token = null;
		}

	}
	
	public User readUser(Long userId) throws DamServiceException {
		UserRequest userRequest = new UserRequest(userId);
	}

//	public List<StockHistory> readAssetStockHistory(StockHistory stockHistory, LocalDate startDate, LocalDate endDate)
//			throws DamServiceException {
//		login();
//
//		StockHistoryRequest historyRequest = new StockHistoryRequest(stockHistory, startDate, endDate);
//
////		jsonHelper.getObjectMapper().registerModule(new JavaTimeModule());
////		jsonHelper.getObjectMapper().disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
//
//		JsonNode node = jsonHelper.getObjectMapper().valueToTree(historyRequest);
//		JsonNode response = sendRequest(node, "STOCK" + "/" + "getStockHistory");
//
//		JsonNode jsonHistoryList = jsonHelper.extractNodeFromNode(response, "stockHistoryList");
//		List<StockHistory> historyList = new ArrayList<>();
//		if (null != jsonHistoryList) {
//			try {
//				if (jsonHistoryList.isArray()) {
//					for (JsonNode arrayItem : jsonHistoryList) {
//						historyList.add(jsonHelper.getObjectMapper().treeToValue(arrayItem, StockHistory.class));
//					}
//				} else if (jsonHistoryList.isObject()) {
//					historyList.add(jsonHelper.getObjectMapper().treeToValue(jsonHistoryList, StockHistory.class));
//				}
//			} catch (JsonProcessingException e) {
//				throw new DamServiceException(500L, "Portfolio :: Fehler bei Bearbeitung der Response", e.getMessage());
//			}
//		}
//
//		return historyList;
//	}

	/*
	 * Send request to Service Provider.
	 */
	protected JsonNode sendRequest(JsonNode node, String path) throws DamServiceException {
		String url = getServiceProviderUrl();

		jsonHelper.addToJsonNode(node, "tokenId", Client.token);

		try {
			JsonNode response = consumer.retrieveResponse(jsonHelper.extractStringFromJsonNode(node), url, path);
			long returnCode = jsonHelper.extractLongFromNode(response, "returnCode");
			if (200L != returnCode) {
				String result = jsonHelper.extractStringFromJsonNode(response, "result");
				String serviceName = jsonHelper.extractStringFromJsonNode(response, "serviceName");
				token = null;
				throw new DamServiceException(403L, "Request " + path + " failed",
						result + "; Service: " + serviceName);
			}
			return response;
		} catch (Exception ex) {
			Client.token = null;
			throw new DamServiceException(400L, ex.getMessage(), ex.getStackTrace().toString());
		}
	}

	private String getServiceProviderUrl() {
		return configuration.getServiceProviderProtocol() + "://" + configuration.getServiceProviderHost() + ":"
				+ configuration.getServiceProviderPort();
	}

}
