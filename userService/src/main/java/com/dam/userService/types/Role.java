package com.dam.userService.types;

public enum Role {
	
	INVESTOR ("Investor"),
	USER ("Any other user"),
	ADMIN ("Administrator"),
	ROOT_ADMIN ("Root Administrator"),
	CONTROLLER ("Controller"),
	UNKNOWN("Unknown person");
	
	private String description;

	
	Role (String description) {
		this.description = description;
	}
	
	public String getDescription() {
		return description;
	}

}
