package com.dam.authentication.rest;

import java.net.URI;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import com.dam.authentication.ConfigProperties;
import com.dam.authentication.JsonHelper;
import com.dam.authentication.rest.message.GetUserResponse;
import com.dam.authentication.rest.message.LoginRequest;
import com.dam.exception.CsServiceException;

@Component
public class UserServiceConsumer {
	@Autowired
	ConfigProperties config;

	public GetUserResponse readUser(LoginRequest loginRequest) throws CsServiceException {
		RestTemplate restTemplate = new RestTemplate();

		try {
			HttpHeaders headers = new HttpHeaders();
			headers.setContentType(MediaType.APPLICATION_JSON);

			String jsonLoginRequest = null;
			JsonHelper helper = new JsonHelper();
			jsonLoginRequest = helper.getObjectMapper().writeValueAsString(loginRequest);

			HttpEntity<String> requestBody = new HttpEntity<String>(jsonLoginRequest, headers);
			URI uri = new URI(config.getUserService().getServiceUrl() + "/checkUser");
			ResponseEntity<GetUserResponse> response = restTemplate.exchange(uri, HttpMethod.POST, requestBody,
					GetUserResponse.class);
			return response.getBody();
		} catch (Exception e) {
			throw new CsServiceException(new Long(500), "User could not be evaluated", e.getMessage());
		}
	}
}
