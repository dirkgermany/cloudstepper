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
import com.fasterxml.jackson.annotation.JsonIgnore;


@Entity
@Component
@Table(name = "Permission")
public class Permission {
	
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	@JsonIgnore
	private Long _id;
	
	@Column(nullable=false)
	private String serviceDomain;

	@Column(nullable=false)
	private String role;
	
	@Column(nullable=false)
	private String rights;
	
	public Permission () {
	}
	
	public Permission (String role, String service, String rights) {
		setRole(role);
		setServiceDomain(service);
		setRights(rights);
	}
	
	
	public String getServiceDomain() {
		return serviceDomain;
	}
	public void setServiceDomain(String serviceDomain) {
		this.serviceDomain = serviceDomain;
	}
	
	//TODO zwei Methoden
	public String getRole() {
		return role;
	}
	
	public void setRole(String role) {
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
