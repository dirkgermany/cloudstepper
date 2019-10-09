package com.dam.provider.rest.service.message;

import com.fasterxml.jackson.databind.JsonNode;

public class WrappedResponse {

	private JsonNode jsonMessage;
	
	public WrappedResponse (JsonNode jsonMessage) {
		setJsonMessage(jsonMessage);
	}

	public JsonNode getJsonMessage() {
		return jsonMessage;
	}

	public void setJsonMessage(JsonNode jsonMessage) {
		this.jsonMessage = jsonMessage;
	}

}
