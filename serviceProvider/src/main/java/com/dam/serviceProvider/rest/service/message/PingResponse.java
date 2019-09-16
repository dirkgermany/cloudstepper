package com.dam.serviceProvider.rest.service.message;

import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.databind.JsonNode;

public class PingResponse extends RestResponse {
	
	private List<JsonNode> serviceInfos = new ArrayList<JsonNode>();

	public PingResponse() {
		super(new Long(200), "OK", "Service Gateway Is Alive");
	}

	public List<JsonNode> getServiceInfos() {
		return serviceInfos;
	}

	public void setServiceInfos(List<JsonNode> serviceInfos) {
		this.serviceInfos = serviceInfos;
	}

}
