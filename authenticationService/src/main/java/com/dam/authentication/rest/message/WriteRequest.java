package com.dam.authentication.rest.message;

import com.dam.authentication.model.entity.Permission;

public class PermissionCreateRequest extends RestRequest {
    private Permission permission = new Permission();

    public PermissionCreateRequest(String requestorUserId, String role, String serviceDomain, String rights) {
		super("CS 0.0.1");
		permission.setRole(role);
		permission.setServiceDomain(serviceDomain);
		permission.setRights(rights);
		setRequestorUserId(requestorUserId);
	}
    
    public Permission getPermission() {
    	return permission;
    }
    
}