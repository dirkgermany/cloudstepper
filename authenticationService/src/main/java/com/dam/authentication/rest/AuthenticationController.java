package com.dam.authentication.rest;

import java.util.Map;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import com.dam.authentication.ConfigProperties;
import com.dam.authentication.JsonHelper;
import com.dam.authentication.PermissionManager;
import com.dam.authentication.Token;
import com.dam.authentication.TokenStore;
import com.dam.authentication.model.PermissionStore;
import com.dam.authentication.model.User;
import com.dam.authentication.model.entity.Permission;
import com.dam.authentication.rest.message.GetUserResponse;
import com.dam.authentication.rest.message.LoginRequest;
import com.dam.authentication.rest.message.LogoutRequest;
import com.dam.authentication.rest.message.RestResponse;
import com.dam.authentication.rest.message.TokenAndPermissionsResponse;
import com.dam.authentication.rest.message.TokenValidationResponse;
import com.dam.exception.CsServiceException;

@CrossOrigin
@RestController
@ComponentScan
public class AuthenticationController extends MasterController {

	@Autowired
	TokenStore tokenStore;

	@Autowired
	PermissionStore permisssionStore;

	@Autowired
	PermissionManager permissionManager;

	@Autowired
	UserServiceConsumer userServiceConsumer;

	@Autowired
	ConfigProperties config;

	@PostMapping("/login")
	public RestResponse loginPost(@RequestBody String permissionRequest) throws CsServiceException {
		JsonHelper jsonHelper = new JsonHelper();
		String userName = jsonHelper.extractStringFromRequest(permissionRequest, "userName");
		String password = jsonHelper.extractStringFromRequest(permissionRequest, "password");

		try {
			LoginRequest loginRequest = new LoginRequest(userName, password, null);
			return login(loginRequest);
		} catch (ResponseStatusException e) {
			return new RestResponse(e.getStatus(), "Token not validated", e.getMessage());
		}
	}

	@PostMapping("/logout")
	public RestResponse logoutResponse(@RequestBody LogoutRequest logoutRequest) throws CsServiceException {
		if (new Long(0).longValue() == tokenStore.logout(logoutRequest)) {
			return new RestResponse(HttpStatus.OK, "Logout successfull", logoutRequest.getUserName());
		}
		return new RestResponse(HttpStatus.UNAUTHORIZED, "Logout Failed", "User Unknown or not logged in");
	}

	/**
	 * Only for Internal Usage Response to the question if a Token for a qualified
	 * User is correct and active
	 * 
	 * @param tokenRequest
	 * @return
	 */
	@PostMapping("/validateToken")
	public RestResponse validateToken(@RequestBody(required = false) String tokenRequest,
			@RequestHeader Map<String, String> headers) throws CsServiceException {
		if (null == headers || headers.size() == 0) {
			return new RestResponse(HttpStatus.BAD_REQUEST, "Token not validated", "No token received with request");
		}

		String tokenString = headers.get("tokenid");
		if (null == tokenString) {
			throw new CsServiceException(400L, "Token not validated", "Token missed in request");
		}

		String serviceDomain = headers.get("servicedomain");
		if (null == serviceDomain) {
			throw new CsServiceException(400L, "Token not validated", "domain name missed in request");
		}

		try {
			Token validatedToken = validateAndRefresh(tokenString);
			if (null != validatedToken) {
				Permission permission = permissionManager.getRolePermission(validatedToken.getUser().getRole(),
						serviceDomain);

				if (null == permission) {
					return new RestResponse(HttpStatus.UNAUTHORIZED, "Token valid but user has no permissions to serviceDomain", "Check permissions of role " + validatedToken.getUser().getRole() + " and serviceDomain " + serviceDomain);
				}
				TokenAndPermissionsResponse response = new TokenAndPermissionsResponse(
						validatedToken.getUser().getUserId(), validatedToken.getTokenId(), validatedToken.getRights(),
						permission);
				return response;
			}
		} catch (Exception e) {
			return new RestResponse(HttpStatus.UNAUTHORIZED, "Token not validated", e.getMessage());
		}

		return new RestResponse(HttpStatus.UNAUTHORIZED, "Token not validated",
				"Requested Token could not be matched with a valid active Token");
	}

	private RestResponse login(LoginRequest loginRequest) throws CsServiceException {
		// Erst mit dem ersten Login werden die Rollen und Rechte aus der DB geholt
		try {
			GetUserResponse userResponse = userServiceConsumer.readUser(loginRequest);
			HttpStatus httpStatus = userResponse.getHttpStatus();
			if (httpStatus.is2xxSuccessful()) {
				User user = userResponse.getUser();
				Token token = createToken(user);
				TokenValidationResponse response = new TokenValidationResponse(token.getUser().getUserId(),
						token.getTokenId());
				return response;
			}
			else {
				throw new ResponseStatusException(httpStatus, userResponse.getResult() + "; " + userResponse.getDescription());
			}
		} catch (CsServiceException dse) {
			throw new ResponseStatusException(HttpStatus.NOT_ACCEPTABLE, "ErrorId: " + dse.getErrorId() + "; "
					+ dse.getShortMsg() + "; " + dse.getMessage() + "; Service:" + dse.getServiceName());
		}
	}

	private Token validateAndRefresh(String tokenString) throws CsServiceException {

		UUID tokenId = UUID.fromString(tokenString);
		User user = null;
		Token token = tokenStore.getToken(tokenId);
		if (null == token) {
			throw new CsServiceException(new Long(500), "Token not valid", "Token not found in Tokenstore");
		}

		user = token.getUser();
		Token validationToken = new Token(tokenId, user,
				config.getTokenConfiguration().getMaxTokenAge() + System.currentTimeMillis());
		Token validatedToken = tokenStore.validateAndRefreshToken(validationToken);

		return validatedToken;

	}

	/**
	 * Validate User, create and store Token, create Response
	 * 
	 * @param loginRequest
	 * @return
	 * @throws CsServiceException
	 */
	private Token createToken(User user) throws CsServiceException {
		return tokenStore.createNewToken(user);
	}

}