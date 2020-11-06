package com.dam.provider;

import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.databind.JsonNode;

@Component
public class TokenStore {

//	private static final Logger logger = LoggerFactory.getLogger(TokenStore.class);
//	private static final DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("HH:mm:ss");

	private Map<String, JsonNode> tokens = new HashMap<>();
	private Map<String, Long> tokenAges = new HashMap<>();
	private Long maxAge = 60000L; // 1 minute = default

	public ResponseEntity<JsonNode> validateCachedToken(String tokenId, String serviceDomain, Long maxAge, Boolean isTokenCacheActive) {
		if (!isTokenCacheActive) {
			return null;
		}
		
		if (null != maxAge) {
			setMaxAge(maxAge);
		}
		JsonNode node = tokens.get(tokenId + serviceDomain);

		if (node == null) {
			return null;
		}

		// check age
		if (tokenAges.get(tokenId + serviceDomain) + maxAge < System.currentTimeMillis()) {
			tokens.remove(tokenId + serviceDomain);
			tokenAges.remove(tokenId + serviceDomain);
			return null;
		}
		return new ResponseEntity<JsonNode>(node, HttpStatus.OK);
	}

	public void cacheToken(JsonNode jsonBody, String tokenId, String serviceDomain) {
		tokens.put(tokenId + serviceDomain, jsonBody);
		tokenAges.put(tokenId + serviceDomain, System.currentTimeMillis());
	}

	public Long getMaxAge() {
		return maxAge;
	}

	public void setMaxAge(Long maxAge) {
		this.maxAge = maxAge;
	}
}
