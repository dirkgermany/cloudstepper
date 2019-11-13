package com.dam.portfolio.rest.consumer;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.dam.exception.DamServiceException;
import com.dam.portfolio.Configuration;
import com.dam.portfolio.JsonHelper;
import com.dam.portfolio.model.entity.StockHistory;
import com.dam.portfolio.rest.message.portfolio.StockHistoryRequest;
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

	private static final Integer MAX_RETRIES = 10;
	private String token;
	private JsonHelper jsonHelper = new JsonHelper();

	private static final Logger logger = LoggerFactory.getLogger(Client.class);
	private static final DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("HH:mm:ss");

	public void logout(String className) throws DamServiceException {
		logger.info("Portfolio Service Client :: {}: Logout {}", dateTimeFormatter.format(LocalDateTime.now()),
				className);
		token = null;
	}

	/*
	 * Login to Service Provider. Tries to use the Token if set.
	 */
	protected String login() throws DamServiceException {
		if (null != token) {
			return token;
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
			JsonNode response = consumer.retrieveResponse(jsonHelper.extractStringFromJsonNode(node), url, "login", null);
			long returnCode = jsonHelper.extractLongFromNode(response, "returnCode");
			if (200L != returnCode) {
				String result = jsonHelper.extractStringFromJsonNode(response, "result");
				String serviceName = jsonHelper.extractStringFromJsonNode(response, "serviceName");
				throw new DamServiceException(403L, "Login to  Service Provider failed",
						result + "; Service: " + serviceName);
			}
			token = jsonHelper.extractStringFromJsonNode(response, "tokenId");
			return token;
		} catch (Exception e) {
			token = null;
			return token;
		}

	}

	public List<StockHistory> readAssetStockHistory(StockHistory stockHistory, LocalDate startDate, LocalDate endDate, String tokenId)
			throws DamServiceException {
		if (null == tokenId || tokenId.isEmpty()) {
			tokenId = login();
		} else {
			this.token = tokenId;
		}

		StockHistoryRequest historyRequest = new StockHistoryRequest(stockHistory, startDate, endDate);

		JsonNode node = jsonHelper.getObjectMapper().valueToTree(historyRequest);

		// If the same system user is used by different microservices for login as
		// clients
		// the last produced token can be invalid.
		// So the login must be repeated here.
		boolean success = false;
		int retryCount = 0;
		JsonNode response = null;
		while (!success && retryCount++ < MAX_RETRIES) {
			try {
				response = sendRequest(node, "STOCK" + "/" + "getStockHistory", tokenId);
				success = true;
			} catch (DamServiceException d) {
				logger.info("Portfolio Service Client :: {}: readAssetStockHistory retry {} of {}",
						dateTimeFormatter.format(LocalDateTime.now()), retryCount, MAX_RETRIES);
				login();
			}
		}
		if (retryCount >= MAX_RETRIES) {
			throw new DamServiceException(400L, "Reading StockHistory failed",
					"Max. retries reached but access to StockHistory was not successfull");
		}

		JsonNode jsonHistoryList = jsonHelper.extractNodeFromNode(response, "stockHistoryList");
		List<StockHistory> historyList = new ArrayList<>();
		if (null != jsonHistoryList) {
			try {
				if (jsonHistoryList.isArray()) {
					for (JsonNode arrayItem : jsonHistoryList) {
						historyList.add(jsonHelper.getObjectMapper().treeToValue(arrayItem, StockHistory.class));
					}
				} else if (jsonHistoryList.isObject()) {
					historyList.add(jsonHelper.getObjectMapper().treeToValue(jsonHistoryList, StockHistory.class));
				}
			} catch (JsonProcessingException e) {
				throw new DamServiceException(500L, "Portfolio :: Fehler bei Bearbeitung der Response", e.getMessage());
			}
		}

		return historyList;
	}

	/*
	 * Send request to Service Provider.
	 */
	protected JsonNode sendRequest(JsonNode node, String path, String tokenId) throws DamServiceException {
		String url = getServiceProviderUrl();

		jsonHelper.addToJsonNode(node, "tokenId", this.token);

		try {
			JsonNode response = consumer.retrieveResponse(jsonHelper.extractStringFromJsonNode(node), url, path, tokenId);
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
			this.token = null;
			throw new DamServiceException(400L, "Sending request to " + url + path + " failed.", ex.getMessage());
		}
	}

	private String getServiceProviderUrl() {
		return configuration.getServiceProviderProtocol() + "://" + configuration.getServiceProviderHost() + ":"
				+ configuration.getServiceProviderPort();
	}

}
