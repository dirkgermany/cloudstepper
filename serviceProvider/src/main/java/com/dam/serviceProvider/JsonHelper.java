package com.dam.serviceProvider;

import java.io.IOException;
import java.util.Map;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.fasterxml.jackson.databind.node.ObjectNode;

public class JsonHelper {

	public JsonNode createEmptyNode() {
		return JsonNodeFactory.instance.objectNode();
	}

	public JsonNode convertStringToNode(String jsonString) {
		ObjectMapper objectMapper = new ObjectMapper();
		try {
			return objectMapper.readTree(jsonString);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;

	}

	public JsonNode createNodeFromMap(Map<String, String> keyValues) {
		ObjectMapper objectMapper = new ObjectMapper();
		JsonNode responseNode = objectMapper.createObjectNode();

		for (Map.Entry<String, String> entry : keyValues.entrySet()) {
			((ObjectNode) responseNode).put(entry.getKey(), entry.getValue());
		}

		return responseNode;
	}
	
	public JsonNode addToJsonNode(JsonNode node, String key, String value) {
		((ObjectNode) node).put(key, value);
		return node;
	}

	public JsonNode addToJsonNode(JsonNode node, String key, Long value) {
		((ObjectNode) node).put(key, value);
		return node;
	}

	public JsonNode putToJsonString(String jsonString, String key, String value) {
		JsonNode responseTree = null;
		responseTree = convertStringToNode(jsonString);
		((ObjectNode) responseTree).put(key, value);
		return responseTree;
	}

	public String putToJsonNode(String jsonString, String key, String value) {
		JsonNode responseTree = null;
		responseTree = convertStringToNode(jsonString);
		((ObjectNode) responseTree).put(key, value);
		try {
			return new ObjectMapper().writeValueAsString(responseTree);
		} catch (JsonProcessingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}

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
			if (null != node) {
				JsonNode valueNode = node.path(key);
				if (null != valueNode) {
					return valueNode.asLong();
				}
			}
		} catch (JsonProcessingException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return longValue;
	}

	public String extractStringFromRequest(String jsonContent, String key) {
		ObjectMapper objectMapper = new ObjectMapper();
		JsonNode node;
		String value = null;
		try {
			node = objectMapper.readTree(jsonContent);
			if (null != node) {
				JsonNode valueNode = node.path(key);
				if (null != valueNode) {
					return valueNode.textValue();
				}
			}
		} catch (JsonProcessingException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return value;
	}

	public JsonNode extractNodeFromNode(JsonNode nodeParent, String nodePath) {
		try {
			JsonNode foundNode = nodeParent.findPath(nodePath);
			return foundNode;
		} catch (Exception ex) {
			ex.printStackTrace();
		}
		return null;
	}

	public String extractStringFromJsonNode(JsonNode jsonContent) {
		try {
			return new ObjectMapper().writeValueAsString(jsonContent);
		} catch (JsonProcessingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}

	/**
	 * Gets a single value from a String represented Json Node
	 * 
	 * @param request
	 * @param key
	 * @return
	 */
	public String extractStringFromJsonNode(JsonNode jsonContent, String key) {
		String stringContent = extractStringFromJsonNode(jsonContent);
		return extractStringFromRequest(stringContent, key);
	}

	public Long extractLongFromNode(JsonNode jsonContent, String key) {
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
