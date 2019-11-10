package com.dam.coach.rest;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;

import com.dam.exception.DamServiceException;

public class CoachController {
	protected String decode(String value) throws DamServiceException {
		if (null == value)
			return null;

		try {
			return URLDecoder.decode(value, StandardCharsets.UTF_8.toString()).trim();
		} catch (UnsupportedEncodingException e) {
			throw new DamServiceException(404L, "Value could not be decode for URL", e.getMessage());
		}
	}

}
