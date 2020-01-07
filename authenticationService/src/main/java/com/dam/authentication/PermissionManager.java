package com.dam.authentication;

import java.util.HashMap;
import java.util.Map;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.dam.authentication.model.PermissionStore;
import com.dam.authentication.model.entity.Permission;
import com.dam.exception.PermissionCheckException;

@Component
public class PermissionManager {

	Logger logger = LogManager.getLogger(getClass());

	@Autowired
	ConfigProperties config;

	@Autowired
	PermissionStore permissionStore;

	private static Map<String, Map<String, Permission>> rightsAndRoles = new HashMap<>();
	private static Long lastUpdate = new Long(0);

	/*
	 * Calculates if the time for an update has come
	 */
	private Boolean isTimeForUpdate() {
		if (System.currentTimeMillis() - lastUpdate > config.getPermissionUpdateInterval()) {
			lastUpdate = System.currentTimeMillis();
			return true;
		}
		return false;
	}

	/**
	 * Delivers Permissions to Component for a String
	 * 
	 * @param role
	 * @param serviceDomain
	 * @return
	 * @throws PermissionCheckException
	 */
	public synchronized Permission getRolePermission(String role, String serviceDomain)
			throws PermissionCheckException {

		if (isTimeForUpdate()) {
			rightsAndRoles = new HashMap<>();
		}

		Permission permission = null;
		Map<String, Permission> permissionForRole = rightsAndRoles.get(role);
		
		if (null != permissionForRole) {
			permission = permissionForRole.get(serviceDomain);
			if (null != permission) {
				return permission;
			}
		} else {
			permissionForRole = new HashMap<>();
		}
		permission = permissionStore.getPermission(role, serviceDomain);

		if (null != permission) {
			permissionForRole.put(permission.getServiceDomain(), permission);
			rightsAndRoles.put(role, permissionForRole);
		}		
		return permission;
	}
}
