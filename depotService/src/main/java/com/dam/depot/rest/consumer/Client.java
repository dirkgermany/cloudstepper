package com.dam.depot.rest.consumer;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.dam.depot.Configuration;
import com.dam.depot.JsonHelper;
import com.dam.depot.performance.PortfolioPerformance;
import com.dam.depot.rest.message.performance.PortfolioPerformanceRequest;
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

	private static final Integer MAX_RETRIES = 10;
	private static String token;
	private JsonHelper jsonHelper = new JsonHelper();

	private static final Logger logger = LoggerFactory.getLogger(Client.class);
	private static final DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("HH:mm:ss");

	public void logout(String className) throws DamServiceException {
		logger.info("Depot Service Client :: {}: Logout {}", dateTimeFormatter.format(LocalDateTime.now()),
				className);
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
		String password = configuration.getAuthPassword();

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

	public PortfolioPerformance readPortfolioPerformance(Long userId, Long portfolioId, LocalDate startDate, LocalDate endDate)
			throws DamServiceException {

		PortfolioPerformanceRequest performanceRequest = new PortfolioPerformanceRequest(null, portfolioId, null, startDate, endDate, true, false, false);
		JsonNode node = jsonHelper.getObjectMapper().valueToTree(performanceRequest);

		// If the same system user is used by different microservices for login as
		// clients
		// the last produced token can be invalid.
		// So the login must be repeated here.
		boolean success = false;
		int retryCount = 0;
		JsonNode response = null;
		while (!success && retryCount++ < MAX_RETRIES) {
			try {
				login();
				response = sendRequest(node, "portfolio" + "/" + "getPortfolioPerformance");
				success = true;
			} catch (DamServiceException d) {
				logger.info("Depot Service Client :: {}: readPortfolioPerformance retry {} of {}",
						dateTimeFormatter.format(LocalDateTime.now()), retryCount, MAX_RETRIES);
			}
		}
		if (retryCount >= MAX_RETRIES) {
			throw new DamServiceException(400L, "Reading Portfolio Performance failed",
					"Max. retries reached but request to Portfolio Service was not successfull");
		}

		JsonNode jsonPerformance = jsonHelper.extractNodeFromNode(response, "portfolioPerformance");
		PortfolioPerformance performance;
		try {
			performance = jsonHelper.getObjectMapper().treeToValue(jsonPerformance, PortfolioPerformance.class);
		} catch (JsonProcessingException e) {
			throw new DamServiceException(404L, "Object Mapping failed", e.getStackTrace().toString());
		}

		return performance;
	}

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
			throw new DamServiceException(400L, "Sending request to " + url + path + " failed.", ex.getMessage());
		}
	}

	private String getServiceProviderUrl() {
		return configuration.getServiceProviderProtocol() + "://" + configuration.getServiceProviderHost() + ":"
				+ configuration.getServiceProviderPort();
	}

}
