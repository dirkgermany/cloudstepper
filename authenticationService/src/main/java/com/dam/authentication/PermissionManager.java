package com.dam.authentication;

import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.dam.authentication.model.PermissionStore;
import com.dam.authentication.model.entity.Permission;
import com.dam.authentication.types.Role;
import com.dam.authentication.types.ServiceDomain;
import com.dam.exception.PermissionCheckException;

@Component
public class PermissionManager {
	
    Logger logger = LogManager.getLogger(getClass());

	@Autowired
	ConfigProperties config;

	@Autowired
	PermissionStore permissionStore;

	private static Map<Role, Map<ServiceDomain, Permission>> rightsAndRoles = new HashMap<>();
	private static Long lastUpdate = new Long(0);
	
	private void updatePermissionMap() throws PermissionCheckException {
		if (!isTimeForUpdate()) {
			return;
		}
		
		logger.debug("PermissionStore::updatePermissionMap");

		List<Permission> permissions = permissionStore.getPermissionList();

		if (null == permissions || permissions.isEmpty()) {
			throw new PermissionCheckException(new Long(416), "No Permissions", "Permissions not configured");
		}

		Iterator<Permission> it = permissions.iterator();
		while (it.hasNext()) {
			Permission permission = it.next();
			Map<ServiceDomain, Permission> permissionForRole=null;
			if (!rightsAndRoles.containsKey(permission.getRole())) {
				permissionForRole = new HashMap<>();
			} else {
				permissionForRole = rightsAndRoles.get(permission.getRole());
			}
			permissionForRole.put(permission.getServiceDomain(), permission);
			rightsAndRoles.put(permission.getRole(), permissionForRole);
		}
	}

	/*
	 * Calculates if the time for an update has come
	 */
	private  Boolean isTimeForUpdate() {
		if (System.currentTimeMillis() - lastUpdate > config.getPermissionUpdateInterval()) {
			lastUpdate = System.currentTimeMillis();
			return true;
		}
		return false;
	}

	/**
	 * Adds Components with Permissions to a dedicated Role
	 * 
	 * @param role
	 * @param serviceDomain
	 * @param rights
	 */
	public  synchronized void addPermission(Role role, ServiceDomain serviceDomain, Permission permission) {
		Map<ServiceDomain, Permission> rightsForRole = rightsAndRoles.get(role);
		if (null == rightsForRole) {
			rightsForRole = new HashMap<>();
		}
		rightsForRole.put(serviceDomain, permission);
		rightsAndRoles.put(role, rightsForRole);
	}

	/**
	 * Delivers Components with Permissions for a Role
	 * 
	 * @param role
	 * @return
	 */
	public  synchronized Map<ServiceDomain, Permission> getPermissionForRole(Role role) {
		return rightsAndRoles.get(role);
	}

	/**
	 * Delivers Permissions to Component for a Role
	 * 
	 * @param role
	 * @param serviceDomain
	 * @return
	 * @throws PermissionCheckException
	 */
	public  synchronized Permission getRolePermission(Role role, ServiceDomain serviceDomain)
			throws PermissionCheckException {
		updatePermissionMap();
		
		Map<ServiceDomain, Permission> map = rightsAndRoles.get(role);
		if (null == map) {
			return new Permission(Role.UNKNOWN, ServiceDomain.UNKNOWN, "---+---");
		}
		Permission permission = map.get(serviceDomain);
		if (null == permission) {
			return new Permission(Role.UNKNOWN, ServiceDomain.UNKNOWN, "---+---");
		}
		return permission;
	}
}
