package com.dam.user.rest.message;

import org.springframework.http.HttpStatus;

import com.dam.user.model.entity.User;

public class WriteResponse extends RestResponse{
    private User user;
    	
	public WriteResponse (User user) {
		super(HttpStatus.CREATED, "OK", "User created");
		
		setUser(user);
	}  

	private void setUser(User user) {
		this.user = user;
	}
	
	public User getUser() {
		return user;
	}
	
}
