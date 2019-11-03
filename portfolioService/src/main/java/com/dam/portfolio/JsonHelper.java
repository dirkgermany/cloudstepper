package com.dam.portfolio;

import java.io.IOException;
import java.util.Map;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;

public class JsonHelper {
	
	ObjectMapper objectMapper = null;
	
	public ObjectMapper getObjectMapper () {
		if (null == objectMapper) {
			objectMapper = new ObjectMapper();
			this.objectMapper.registerModule(new JavaTimeModule());
			this.objectMapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
		}
		return this.objectMapper;
	}

	public JsonNode createEmptyNode() {
		return JsonNodeFactory.instance.objectNode();
	}

	public JsonNode convertStringToNode(String jsonString) {
		try {
			return getObjectMapper().readTree(jsonString);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;

	}

	public JsonNode createNodeFromMap(Map<String, String> keyValues) {
		JsonNode responseNode = getObjectMapper().createObjectNode();

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
			return getObjectMapper().writeValueAsString(responseTree);
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
		JsonNode node;
		Long longValue = null;
		try {
			node = getObjectMapper().readTree(jsonContent);
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
		JsonNode node;
		String value = null;
		try {
			node = getObjectMapper().readTree(jsonContent);
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

	/**
	 * Returns a JsonNode as String representation
	 * @param jsonContent
	 * @return
	 */
	public String extractStringFromJsonNode(JsonNode jsonContent) {
		try {
			return getObjectMapper().writeValueAsString(jsonContent);
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
			stringContent = getObjectMapper().writeValueAsString(jsonContent);
		} catch (JsonProcessingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return extractLongFromRequest(stringContent, key);
	}

}
