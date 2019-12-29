package com.dam.user.rest.message;

import com.dam.user.model.entity.User;

public class CreateResponse extends RestResponse{
    private User user;
    	
	public CreateResponse (User user) {
		super(new Long(200), "OK", "User created");
		
		setUser(user);
	}  

	private void setUser(User user) {
		this.user = user;
	}
	
	public User getUser() {
		return user;
	}
	
}
