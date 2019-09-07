package com.dam.authentication.rest.message;

import java.util.UUID;

public class TokenAndPermissionsResponse extends TokenValidationResponse {
	
	private String permissions;
	
	public TokenAndPermissionsResponse (Long userId, UUID tokenId, String permissions) {
		super(userId, tokenId);
		setPermissions(permissions);

	}

	public String getPermissions() {
		return permissions;
	}

	public void setPermissions(String permissions) {
		this.permissions = permissions;
	}

}
