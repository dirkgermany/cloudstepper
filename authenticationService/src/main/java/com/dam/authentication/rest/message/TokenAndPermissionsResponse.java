package com.dam.authentication.rest.message;

import java.util.UUID;

import com.dam.authentication.model.entity.Permission;

public class TokenAndPermissionsResponse extends TokenValidationResponse {
	
	private String rights;
    private Permission permission;

	
	public TokenAndPermissionsResponse (Long userId, UUID tokenId, String rights, Permission permission) {
		super(userId, tokenId);
		setRights(rights);
		setPermission(permission);

	}

	public String getRights() {
		return rights;
	}

	public void setRights(String rights) {
		this.rights = rights;
	}

	public Permission getPermission() {
		return permission;
	}

	public void setPermission(Permission permission) {
		this.permission = permission;
	}

}
