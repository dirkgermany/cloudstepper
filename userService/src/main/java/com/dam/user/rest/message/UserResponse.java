package com.dam.user.rest.message;

import org.springframework.http.HttpStatus;

import com.dam.user.model.entity.User;

public class UserResponse extends RestResponse{
    private User user;
    	
	public UserResponse (User user) {
		super(HttpStatus.OK, "OK", "User exists");
		
		setUser(user);
	}  

	private void setUser(User user) {
		this.user = user;
	}
	
	public User getUser() {
		return this.user;
	}
	
}
