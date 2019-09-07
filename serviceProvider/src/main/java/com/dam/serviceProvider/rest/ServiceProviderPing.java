package com.dam.serviceProvider.rest;

import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import com.dam.serviceProvider.ConfigProperties;
import com.dam.serviceProvider.JsonHelper;
import com.dam.serviceProvider.PingFactory;
import com.dam.serviceProvider.rest.consumer.Consumer;
import com.dam.serviceProvider.rest.service.message.PingResponse;
import com.fasterxml.jackson.databind.JsonNode;

@RestController
public class ServiceProviderPing {
	@Autowired
	ConfigProperties config;

	@Autowired
	Consumer consumer;

	/**
	 * Retrieves informations about service healthy.
	 * 
	 * @param request
	 * @return
	 */
	@GetMapping("/ping")
	public PingResponse ping() {
		PingFactory pingFactory = new PingFactory("ServiceProvider");

		JsonNode response = pingFactory.getNode();

		PingResponse pingResponse = new PingResponse();
		pingResponse.getServiceInfos().add(response);

		// all other microservices
		pingResponse = pingToService(pingResponse, config.getUserService().getServiceUrl(), "UserService");
		pingResponse = pingToService(pingResponse, config.getAuthenticationService().getServiceUrl(), "AuthenticationService");
		pingResponse = pingToService(pingResponse, config.getPersonService().getServiceUrl(), "PersonService");
		pingResponse = pingToService(pingResponse, config.getAddressService().getServiceUrl(), "AddressService");

		return pingResponse;
	}

	private PingResponse pingToService(PingResponse pingResponse, String url, String serviceName) {
		Map<String, String> pingInfo = new HashMap<String, String>();
		JsonNode response = null;

		try {
			response = consumer.retrieveResponse(null, url, "ping");
			pingResponse.getServiceInfos().add(response);
		} catch (Exception ex) {
			pingResponse.setMessage("WARNING");
			pingResponse.setDescription("Not all required Microservices are reachable");
			pingResponse.setReturnCode(new Long(111));
			pingInfo = new HashMap<String, String>();
			pingInfo.put("service", serviceName);
			pingInfo.put("status", "NO RESPONSE");
			response = new JsonHelper().createNodeFromMap(pingInfo);
			pingResponse.getServiceInfos().add(response);
		}

		return pingResponse;
	}

}