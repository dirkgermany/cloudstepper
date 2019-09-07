package com.dam.authentication.model.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

import org.springframework.stereotype.Component;

import com.dam.authentication.types.Role;
import com.dam.authentication.types.ServiceDomain;


@Entity
@Component
@Table(name = "Permission")
public class Permission {
	
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private Long _id;
	
	@Column(nullable=false)
	@Enumerated(EnumType.STRING)
	private ServiceDomain serviceDomain;

	@Column(nullable=false)
	@Enumerated(EnumType.STRING)
	private Role role;
	
	@Column(nullable=false)
	private String rights;
	
	public Permission () {
	}
	
	public Permission (Role role, ServiceDomain service, String rights) {
		setRole(role);
		setServiceDomain(service);
		setRights(rights);
	}
	
	
	public ServiceDomain getServiceDomain() {
		return serviceDomain;
	}
	public void setServiceDomain(ServiceDomain serviceDomain) {
		this.serviceDomain = serviceDomain;
	}
	
	//TODO zwei Methoden
	public Role getRole() {
		return role;
	}
	
	public void setRole(Role role) {
		this.role = role;
	}
	
	public Long get_id() {
		return _id;
	}
	public String getRights() {
		return rights;
	}
	public void setRights(String rights) {
		this.rights = rights;
	}
	
	public void updateEntity (Permission updatePermission) {
		setServiceDomain(updatePermission.getServiceDomain());
		// TODO Zeile
		setRole(updatePermission.getRole());
	}

}
