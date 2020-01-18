package com.cs.config.rest;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.cs.config.ConfigurationStore;
import com.cs.config.model.entity.ConfigEntity;
import com.cs.config.rest.message.DropResponse;
import com.cs.config.rest.message.ListConfigurationResponse;
import com.cs.config.rest.message.RestResponse;
import com.cs.config.rest.message.ConfigResponse;
import com.cs.config.rest.message.WriteRequest;
import com.cs.exception.CsServiceException;

@RestController
public class ConfigurationController extends MasterController {
	@Autowired
	private ConfigurationStore configurationStore;
	
	@GetMapping("/listConfigurations")
	public ResponseEntity<RestResponse> listUsers(@RequestHeader Map<String, String> headers) throws CsServiceException {

		try {
			List<ConfigEntity> configEntities = configurationStore.listAllConfigurationsSafe(headers);
			if (null != configEntities) {
				return new ResponseEntity<RestResponse>(new ListConfigurationResponse(configEntities), HttpStatus.OK);
			}
		} catch (CsServiceException dse) {
			return new ResponseEntity<RestResponse>(
					new RestResponse(HttpStatus.valueOf(dse.getErrorId().intValue()), "Configuration list could not be read", dse.getMessage()),
					HttpStatus.OK);
		}
		return new ResponseEntity<RestResponse>(
				new RestResponse(HttpStatus.INTERNAL_SERVER_ERROR, "Configuration list could not be read", "No configuration found"),
				HttpStatus.OK);
	}



	@GetMapping("/getConfiguration")
	public ResponseEntity<RestResponse> getConfiguration(@RequestParam Map<String, String> params,
			@RequestHeader Map<String, String> headers) throws CsServiceException {

		Map<String, String> decodedParams = null;
		try {
			decodedParams = decodeHttpMap(params);
		} catch (Exception e) {
			return new ResponseEntity<RestResponse>(new RestResponse(HttpStatus.BAD_REQUEST, "Invalid requestParams",
					"RequestParams couldn't be decoded"), HttpStatus.OK);
		}

		try {
			List<ConfigEntity> configEntities = configurationStore.listConfigurationsSafe(decodedParams, headers);
			
			
				return new ResponseEntity<RestResponse>(new ListConfigurationResponse(configEntities), HttpStatus.OK);
			
		} catch (CsServiceException dse) {
			return new ResponseEntity<RestResponse>(
					new RestResponse(HttpStatus.valueOf(dse.getErrorId().intValue()), "Configuration could not be read", dse.getMessage()),
					HttpStatus.OK);
		}
	}
	
//	@PostMapping("createConfiguration")
//   public ResponseEntity<RestResponse> createConfiguration(@RequestBody WriteRequest request) {
//		return new ResponseEntity<RestResponse>(new RestResponse(HttpStatus.ACCEPTED,
//				"Configuration could not be updated", "bla"), HttpStatus.OK);
//	}


	@PostMapping("/createConfiguration")
	public ResponseEntity<RestResponse> createConfiguration(@RequestBody WriteRequest requestBody,
			@RequestParam Map<String, String> params, @RequestHeader Map<String, String> headers) {

		Map<String, String> decodedParams = null;
		try {
			decodedParams = decodeHttpMap(params);
		} catch (Exception e) {
			return new ResponseEntity<RestResponse>(new RestResponse(HttpStatus.BAD_REQUEST, "Invalid requestParams",
					"RequestParams couldn't be decoded"), HttpStatus.OK);
		}

		try {
			ConfigEntity configEntity = configurationStore.createConfigurationSafe(requestBody, decodedParams, headers);
			if (null != configEntity) {
				return new ResponseEntity<RestResponse>(new ConfigResponse(configEntity), HttpStatus.OK);
			}
		} catch (CsServiceException e) {
			return new ResponseEntity<RestResponse>(new RestResponse(HttpStatus.valueOf(e.getErrorId().intValue()),
					"Configuration could not be created", e.getMessage()), HttpStatus.OK);
		}
		return new ResponseEntity<RestResponse>(new RestResponse(HttpStatus.NOT_MODIFIED, "Configuration not created",
				"Configuration still exists, data invalid or not complete"), HttpStatus.OK);
	}

	@PutMapping("/updateConfiguration")
	public ResponseEntity<RestResponse> updateConfiguration(@RequestBody WriteRequest requestBody,
			@RequestParam Map<String, String> params, @RequestHeader Map<String, String> headers) {

		Map<String, String> decodedParams = null;
		try {
			decodedParams = decodeHttpMap(params);
		} catch (Exception e) {
			return new ResponseEntity<RestResponse>(new RestResponse(HttpStatus.BAD_REQUEST, "Invalid requestParams",
					"RequestParams couldn't be decoded"), HttpStatus.OK);
		}

		try {
			ConfigEntity configEntity = configurationStore.updateConfigurationSafe(requestBody, decodedParams, headers);
			if (null != configEntity) {
				return new ResponseEntity<RestResponse>(new ConfigResponse(configEntity), HttpStatus.OK);
			}
		} catch (CsServiceException e) {
			return new ResponseEntity<RestResponse>(new RestResponse(HttpStatus.valueOf(e.getErrorId().intValue()),
					"Configuration could not be updated", e.getMessage()), HttpStatus.OK);
		}
		return new ResponseEntity<RestResponse>(new RestResponse(HttpStatus.NOT_MODIFIED, "Configuration not updated",
				"Configuration does not exist, data invalid or not complete"), HttpStatus.OK);
	}

	@DeleteMapping("/dropConfiguration")
	public ResponseEntity<RestResponse> dropConfiguration(@RequestParam Map<String, String> params,
			@RequestHeader Map<String, String> headers) {
		Map<String, String> decodedParams = null;
		try {
			decodedParams = decodeHttpMap(params);
		} catch (Exception e) {
			return new ResponseEntity<RestResponse>(new RestResponse(HttpStatus.BAD_REQUEST, "Invalid requestParams",
					"RequestParams couldn't be decoded"), HttpStatus.BAD_REQUEST);
		}

		String configurationIdAsString = decodedParams.get("configurationId");
		if (null == configurationIdAsString) {
			return new ResponseEntity<RestResponse>(
					new RestResponse(HttpStatus.INTERNAL_SERVER_ERROR, "Configuration not deleted", "Configuration id is empty"),
					HttpStatus.OK);
		}

		try {
			configurationStore.dropConfigurationSafe(decodedParams, headers);
		} catch (CsServiceException dse) {
			return new ResponseEntity<RestResponse>(
					new RestResponse(HttpStatus.valueOf(dse.getErrorId().intValue()), "Configuration not deleted", dse.getMessage()),
					HttpStatus.OK);
		}

		return new ResponseEntity<RestResponse>(new DropResponse(HttpStatus.NO_CONTENT), HttpStatus.NO_CONTENT);
	}
}