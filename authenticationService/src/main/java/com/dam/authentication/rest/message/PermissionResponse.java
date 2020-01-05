package com.dam.authentication.rest.message;

import org.springframework.http.HttpStatus;

import com.dam.authentication.model.entity.Permission;

public class PermissionResponse extends RestResponse{
    private Permission permission;
    	
	public PermissionResponse (Long userId, Permission permission) {
		super(HttpStatus.OK, "OK", "User authenticated.");
		
		setPermission(permission);
	}  

	private void setPermission(Permission permission) {
		this.permission = permission;
	}
	
	public Permission getPermission() {
		return this.permission;
	}
	
}
