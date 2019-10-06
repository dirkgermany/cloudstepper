package com.dam.stock.client;

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
import com.dam.stock.Configuration;
import com.dam.stock.JsonHelper;
import com.dam.stock.model.entity.AssetClass;
import com.dam.stock.rest.consumer.Consumer;
import com.dam.stock.rest.message.RestRequest;
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
	private Configuration configuration;

	@Autowired
	private Consumer consumer;
	
	private static String token;
	protected JsonHelper jsonHelper = new JsonHelper();
	
	private static final Logger logger = LoggerFactory.getLogger(Client.class);
	private static final DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("HH:mm:ss");
	
	public List<AssetClass> getAssetClassList() throws DamServiceException {
		String url = getPortfolioServiceUrl();
		String domain = configuration.getDomain();
		String path = "getAssetClassList";
		String nodeName = "assetClassList";
		
		return getAssetClassList(domain, url, path, nodeName);
	}
	
	/*
	 * Send request to Service Provider.
	 */
	protected JsonNode sendRequestToPortfolio(JsonNode node, String domain, String url, String path) throws DamServiceException {
		path = domain + "/" + path;
		
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
	
	protected String getPortfolioServiceUrl() {
		return configuration.getPortfolioServiceProtocol() + "://" +  configuration.getPortfolioServiceHost() + ":" + configuration.getPortfolioServicePort();
	}
	
	/*
	 * Requests the Service Provider for a List with Asset Classes.
	 */
	protected List<AssetClass> getAssetClassList(String domain, String url, String path, String nodeName) throws DamServiceException {

		RestRequest request = new RestRequest("DAM 2.0");

		JsonNode node = jsonHelper.getObjectMapper().valueToTree(request);
		JsonNode response = sendRequestToPortfolio(node, domain, url, path);

		JsonNode jsonAssetClassList = jsonHelper.extractNodeFromNode(response, nodeName);

		List<AssetClass> assetClassList = new ArrayList<>();
		if (null != jsonAssetClassList) {
			try {
				if (jsonAssetClassList.isArray()) {
					for (JsonNode arrayItem : jsonAssetClassList) {
						assetClassList.add(jsonHelper.getObjectMapper().treeToValue(arrayItem, AssetClass.class));
					}
				} else if (jsonAssetClassList.isObject()) {
					assetClassList.add(jsonHelper.getObjectMapper().treeToValue(jsonAssetClassList, AssetClass.class));
				}
			} catch (JsonProcessingException e) {
				throw new DamServiceException(500L, "Job :: Fehler bei Bearbeitung der Response",
						e.getMessage());
			}
		}
		return assetClassList;
	}
}
