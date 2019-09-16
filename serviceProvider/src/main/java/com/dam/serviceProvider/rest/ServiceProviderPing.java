package com.dam.serviceProvider.rest;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import com.dam.exception.DamServiceException;
import com.dam.serviceProvider.ConfigProperties;
import com.dam.serviceProvider.JsonHelper;
import com.dam.serviceProvider.PingFactory;
import com.dam.serviceProvider.rest.consumer.Consumer;
import com.dam.serviceProvider.rest.service.message.PingResponse;
import com.dam.serviceProvider.types.ServiceDomain;
import com.fasterxml.jackson.databind.JsonNode;

@RestController
public class ServiceProviderPing {
	@Autowired
	ConfigProperties config;

	@Autowired
	Consumer consumer;

	private static String serviceName = "serviceProvider";

	/**
	 * Retrieves informations about service healthy.
	 * 
	 * @param request
	 * @return
	 */
	@GetMapping("/ping")
	public PingResponse ping() throws DamServiceException{
		PingFactory pingFactory = new PingFactory("ServiceProvider");

		JsonNode response = pingFactory.getNode();

		PingResponse pingResponse = new PingResponse();
		pingResponse.setMessage("PERFECT");
		pingResponse.setDescription("All configured Microservices are reachable");
		pingResponse.getServiceInfos().add(response);
		
		// all other microservices
		Iterator<String> it = config.getDomainList().iterator();
		Integer index = 0;
		while (it.hasNext()) {
			String domainName = it.next();
			index = config.getIndexPerDomain(domainName);
			pingResponse = pingToService(pingResponse, config.getServiceUrl(index), domainName);
			index++;
		}
		
		return pingResponse;
	}

	private PingResponse pingToService(PingResponse pingResponse, String url, String domainName) {
		Map<String, String> pingInfo = new HashMap<String, String>();
		JsonNode response = null;

		try {
			response = consumer.retrieveResponse(null, url, "ping");
			pingResponse.getServiceInfos().add(response);
		} catch (Exception ex) {
			pingResponse.setMessage("WARNING");
			pingResponse.setDescription("Not all configured Microservices are reachable");
			pingResponse.setReturnCode(new Long(500));
			pingInfo = new HashMap<String, String>();
			pingInfo.put("service", domainName);
			pingInfo.put("status", "NO RESPONSE");
			response = new JsonHelper().createNodeFromMap(pingInfo);
			pingResponse.getServiceInfos().add(response);
		}

		return pingResponse;
	}

}