package com.dam.authentication.rest;

import java.io.IOException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import com.dam.authentication.ConfigProperties;
import com.dam.authentication.JsonHelper;
import com.dam.authentication.model.User;
import com.dam.authentication.rest.message.GetUserResponse;
import com.dam.authentication.rest.message.LoginRequest;
import com.dam.exception.DamServiceException;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

@Component
public class UserServiceConsumer {
	@Autowired
	ConfigProperties config;

	public User readUser(LoginRequest loginRequest) throws DamServiceException {
		RestTemplate restTemplate = new RestTemplate();

		try {
			HttpHeaders headers = new HttpHeaders();
			headers.setContentType(MediaType.APPLICATION_JSON);

			String jsonUser = null;
			jsonUser = new ObjectMapper().writeValueAsString(loginRequest);

			HttpEntity<String> requestBody = new HttpEntity<String>(jsonUser, headers);
			String uri = config.getUserService().getServiceUrl() + "/checkUser";
			String response = restTemplate.postForObject(uri, requestBody, String.class);

			Long returnCode = new JsonHelper().extractLongFromRequest(response, "returnCode");

			if (null == returnCode || 0 != returnCode.longValue()) {
				// token could not be validated
				throw new DamServiceException(new Long(416), "Invalid Returncode received",
						"User Service sent empty returnCode or 0 as value");
			}

			GetUserResponse userResponse = null;
			User user = null;

			userResponse = new ObjectMapper().readValue(response, GetUserResponse.class);
			user = userResponse.getUser();

			if (null == user) {
				throw new DamServiceException(new Long(500), "User could not be evaluated", "User is null");
			}

			return user;

		} catch (Exception e) {
			throw new DamServiceException(new Long(500), "User could not be evaluated", e.getMessage());
		}

	}

}
