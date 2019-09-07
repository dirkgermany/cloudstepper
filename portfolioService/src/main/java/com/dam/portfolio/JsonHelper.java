package com.dam.portfolio;

import java.io.IOException;
import java.util.Map;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;

public class JsonHelper {

	/**
	 * Gets a single value from a String represented Json structure
	 * 
	 * @param request
	 * @param key
	 * @return
	 */
	public Long extractLongFromRequest(String jsonContent, String key) {
		ObjectMapper objectMapper = new ObjectMapper();
		JsonNode node;
		Long longValue = null;
		try {
			node = objectMapper.readTree(jsonContent);
			longValue = node.path(key).asLong();
		} catch (JsonProcessingException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return longValue;
	}
	
	public JsonNode createNodeFromMap(Map<String, String> keyValues) {
		ObjectMapper objectMapper = new ObjectMapper();
		objectMapper.createObjectNode();
		JsonNode responseNode = objectMapper.createObjectNode();
		
		for (Map.Entry<String, String> entry :  keyValues.entrySet()) {
			((ObjectNode) responseNode).put(entry.getKey(), entry.getValue());
		}
		
		return responseNode;
	}



	public String extractStringFromRequest(String jsonContent, String key) {
		ObjectMapper objectMapper = new ObjectMapper();
		JsonNode node;
		String value = null;
		try {
			node = objectMapper.readTree(jsonContent);
			value = node.path(key).textValue();
		} catch (JsonProcessingException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return value;
	}

	/**
	 * Gets a single value from a String represented Json Node
	 * 
	 * @param request
	 * @param key
	 * @return
	 */
	public String extractStringFromRequest(JsonNode jsonContent, String key) {
		String stringContent = null;
		try {
			stringContent = new ObjectMapper().writeValueAsString(jsonContent);
		} catch (JsonProcessingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return extractStringFromRequest(stringContent, key);
	}

	public Long extractLongFromRequest(JsonNode jsonContent, String key) {
		String stringContent = null;
		try {
			stringContent = new ObjectMapper().writeValueAsString(jsonContent);
		} catch (JsonProcessingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return extractLongFromRequest(stringContent, key);
	}

}
