package com.dam.authentication.rest.message;

import com.dam.authentication.model.entity.Permission;

public class WriteRequest extends RestRequest {
    private Permission permission;

    public WriteRequest(Permission permission) {
		super("CS 0.0.1");
		setPermission(permission);
	}
    
    public void setPermission (Permission permission) {
    	this.permission = permission;
    }
    
    public Permission getPermission() {
    	return permission;
    }
}