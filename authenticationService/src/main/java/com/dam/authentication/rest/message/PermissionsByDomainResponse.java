package com.dam.authentication.rest.message;

import java.util.List;

import org.springframework.http.HttpStatus;

import com.dam.authentication.model.entity.Permission;

public class PermissionsByServiceResponse extends RestResponse{
    private List<Permission> permissions;
    	
	public PermissionsByServiceResponse (List<Permission> permissions) {
		super(HttpStatus.OK, "OK", "Permission list read");
		
		setPermissions(permissions);
	}  

	private void setPermissions(List<Permission> permissions) {
		this.permissions = permissions;
	}
	
	public List<Permission> getPermissions() {
		return this.permissions;
	}
}
