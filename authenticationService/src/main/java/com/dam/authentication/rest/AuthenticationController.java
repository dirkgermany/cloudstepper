package com.dam.authentication.rest;

import java.util.Map;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestParam;
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
import com.dam.authentication.rest.message.LoginRequest;
import com.dam.authentication.rest.message.LogoutRequest;
import com.dam.authentication.rest.message.PermissionResponse;
import com.dam.authentication.rest.message.RestResponse;
import com.dam.authentication.rest.message.TokenAndPermissionsResponse;
import com.dam.authentication.rest.message.TokenValidationResponse;
import com.dam.authentication.types.ServiceDomain;
import com.dam.exception.DamServiceException;

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

	@GetMapping("/login")
	public RestResponse loginGet(@RequestParam String userName, @RequestParam String password) {
		try {
			userName = decode(userName);
			password = decode(password);
		} catch (DamServiceException dse) {
			throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
					"ErrorId: " + dse.getErrorId() + "; " + dse.getShortMsg() + "; "
							+ dse.getMessage() + "; Service:" + dse.getServiceName());
		}

		try {
			LoginRequest loginRequest = new LoginRequest(userName, password, null);
			return login(loginRequest);
		} catch (Exception e) {
			return new RestResponse(new Long(420), "Token not validated", e.getMessage());
		}

	}
	
	@PostMapping("/login")
	public RestResponse loginPost(@RequestBody String permissionRequest) throws DamServiceException {
		JsonHelper jsonHelper = new JsonHelper();
		String userName = jsonHelper.extractStringFromRequest(permissionRequest, "userName");
		String password = jsonHelper.extractStringFromRequest(permissionRequest, "password");
		
		try {
			LoginRequest loginRequest = new LoginRequest(userName, password, null);
			return login(loginRequest);
		} catch (Exception e) {
			return new RestResponse(new Long(420), "Token not validated", e.getMessage());

		}
	}



	@PostMapping("/logout")
	public RestResponse logoutResponse(@RequestBody LogoutRequest logoutRequest) throws DamServiceException {
		if (new Long(0).longValue() == tokenStore.logout(logoutRequest)) {
			return new RestResponse(new Long(200), "Logout successfull", logoutRequest.getUserName());
		}
		return new RestResponse(new Long(420), "Logout Failed", "User Unknown or not logged in");
	}

	/**
	 * Only for Internal Usage Response to the question if a Token for a qualified
	 * User is correct and active
	 * 
	 * @param tokenRequest
	 * @return
	 */
	@PostMapping("/validateToken")
	public RestResponse validateTokenResponse(@RequestBody String tokenRequest, @RequestHeader Map<String, String> headers) throws DamServiceException {
		if (null == headers || headers.size() == 0) {
			return new RestResponse(new Long(420), "Token not validated",
					"No token received with request");
		}	
		
		String tokenString = headers.get("tokenid");
		if (null == tokenString) {
			throw new DamServiceException(new Long(500), "Token not validated", "Token missed in request");
		}
		
		String domainName = headers.get("servicedomain");
		if (null == domainName) {
			throw new DamServiceException(new Long(500), "Token not validated", "domain name missed in request");
		}

		
		try {
			Token validatedToken = validateAndRefresh(tokenString);
			if (null != validatedToken) {
				ServiceDomain serviceDomain = ServiceDomain.valueOf(domainName);
				Permission permission = permissionManager.getRolePermission(validatedToken.getUser().getRole(), serviceDomain);
				
				TokenAndPermissionsResponse response = new TokenAndPermissionsResponse(
						validatedToken.getUser().getUserId(), validatedToken.getTokenId(), validatedToken.getRights(), permission);
				return response;
			}
		} catch (Exception e) {
			return new RestResponse(new Long(420), "Token not validated", e.getMessage());
		}

		return new RestResponse(new Long(420), "Token not validated",
				"Requested Token could not be matched with a valid active Token");
	}

	@PostMapping("/getUserPermission")
	public RestResponse getUserPermissions(@RequestBody String permissionRequest) throws DamServiceException {
		Long userId = new JsonHelper().extractLongFromRequest(permissionRequest, "userId");
		String domainName = new JsonHelper().extractStringFromRequest(permissionRequest, "serviceDomain");
		ServiceDomain serviceDomain;
		try {
			serviceDomain = ServiceDomain.valueOf(domainName);
		} catch (Exception e) {
			throw new DamServiceException(new Long(500), "Unknown ServiceDomain", e.getMessage());
		}

		User user = tokenStore.getUser(userId);
		Permission permission = permissionManager.getRolePermission(user.getRole(), serviceDomain);

		return new PermissionResponse(userId, permission);
	}

	private TokenValidationResponse login(LoginRequest loginRequest) throws DamServiceException {
		// Erst mit dem ersten Login werden die Rollen und Rechte aus der DB geholt
		try {
			User user = userServiceConsumer.readUser(loginRequest);
			Token token = createToken(user);
			TokenValidationResponse response = new TokenValidationResponse(token.getUser().getUserId(), token.getTokenId());
			
			return response;
		} catch (DamServiceException dse) {
			throw new ResponseStatusException(HttpStatus.NOT_ACCEPTABLE,
					"ErrorId: " + dse.getErrorId() + "; " + dse.getShortMsg() + "; "
							+ dse.getMessage() + "; Service:" + dse.getServiceName());
		}
	}

	private Token validateAndRefresh(String tokenString) throws DamServiceException {
		
		UUID tokenId = UUID.fromString(tokenString);
		User user = null;
		Token token = tokenStore.getToken(tokenId);
		if (null == token) {
			throw new DamServiceException(new Long(500), "Token not valid", "Token not found in Tokenstore");
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
	 * @throws DamServiceException
	 */
	private Token createToken(User user) throws DamServiceException {
		return tokenStore.createNewToken(user);
	}

}