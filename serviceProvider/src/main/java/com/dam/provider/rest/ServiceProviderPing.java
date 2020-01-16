package com.dam.provider.rest;

import java.util.Iterator;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import com.dam.exception.CsServiceException;
import com.dam.provider.ConfigProperties;
import com.dam.provider.PingFactory;
import com.dam.provider.rest.consumer.Client;
import com.dam.provider.rest.service.message.PingResponse;
import com.fasterxml.jackson.databind.JsonNode;

@CrossOrigin(origins="*")
@RestController
public class ServiceProviderPing {
	@Autowired
	ConfigProperties config;

	@Autowired
	Client client;
	
	@Autowired
	PingFactory pingFactory;

	/**
	 * Retrieves informations about service healthy.
	 * 
	 * @param request
	 * @return
	 */
	@GetMapping("/ping")
	public PingResponse ping() throws CsServiceException{
		pingFactory.setInfo("ServiceProvider");

		JsonNode response = pingFactory.getNode();

		PingResponse pingResponse = new PingResponse();
		pingResponse.setMessage("PERFECT");
		pingResponse.setDescription("All configured Microservices are reachable");
		pingResponse.getServiceInfos().add(response);
		
		// all other microservices
		Iterator<String> it = config.getServiceList().iterator();
		Integer index = 0;
		while (it.hasNext()) {
			String serviceName = it.next();
			index = config.getIndexPerService(serviceName);
//			pingResponse = pingToService(pingResponse, config.getServiceUrl(index), serviceName.toUpperCase());
			index++;
		}
		
		return pingResponse;
	}

//	private PingResponse pingToService(PingResponse pingResponse, String url, String domainName) {
//		Map<String, String> pingInfo = new HashMap<String, String>();
//		JsonNode response = null;
//
//		try {
//			response = consumer.retrievePostResponse(null, url, "ping", null);
//			pingResponse.getServiceInfos().add(response);
//		} catch (Exception ex) {
//			pingResponse.setMessage("WARNING");
//			pingResponse.setDescription("Not all configured Microservices are reachable");
//			pingResponse.setReturnCode(new Long(500));
//			pingInfo = new HashMap<String, String>();
//			pingInfo.put("service", domainName);
//			pingInfo.put("status", "NO RESPONSE");
//			response = new JsonHelper().createNodeFromMap(pingInfo);
//			pingResponse.getServiceInfos().add(response);
//		}
//
//		return pingResponse;
//	}

}