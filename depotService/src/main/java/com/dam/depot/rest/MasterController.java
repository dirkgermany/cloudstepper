package com.dam.depot.rest;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import org.springframework.stereotype.Controller;

import com.dam.exception.DamServiceException;

public class MasterController {
	protected String decode(String value) throws DamServiceException {
		if (null == value)
			return null;

		try {
			return URLDecoder.decode(value, StandardCharsets.UTF_8.toString()).trim();
		} catch (UnsupportedEncodingException e) {
			throw new DamServiceException(404L, "Value could not be decode for URL", e.getMessage());
		}
	}
	
	protected Map<String, String> decodeUrlMap(Map<String, String> urlParams) throws DamServiceException{
		Map<String, String> decodedMap = new HashMap<>();
		if (null != urlParams && !urlParams.isEmpty()) {
			Iterator<Map.Entry<String, String>> it = urlParams.entrySet().iterator();
			while (it.hasNext()) {
				Map.Entry<String, String> entry = it.next();
				decodedMap.put(decode(entry.getKey()), decode(entry.getValue()));
			}
		}
		return decodedMap;
	}

}
