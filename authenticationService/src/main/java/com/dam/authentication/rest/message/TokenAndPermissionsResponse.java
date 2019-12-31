package com.dam.authentication.rest.message;

import java.util.UUID;

import com.dam.authentication.model.entity.Permission;

public class TokenAndPermissionsResponse extends TokenValidationResponse {
	
	private String permissions;
    private Permission permission;

	
	public TokenAndPermissionsResponse (Long userId, UUID tokenId, String permissions, Permission permission) {
		super(userId, tokenId);
		setPermissions(permissions);
		setPermission(permission);

	}

	public String getPermissions() {
		return permissions;
	}

	public void setPermissions(String permissions) {
		this.permissions = permissions;
	}

	public Permission getPermission() {
		return permission;
	}

	public void setPermission(Permission permission) {
		this.permission = permission;
	}

}
