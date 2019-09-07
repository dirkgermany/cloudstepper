package com.dam.userService.rest.message;

import com.dam.userService.model.entity.UserMessageContainer;

public class UpdateResponse extends RestResponse{
    private UserMessageContainer user;
    	
	public UpdateResponse (UserMessageContainer user) {
		super(new Long(0), "OK", "User updated");
		
		setUser(user);
	}  

	private void setUser(UserMessageContainer user) {
		this.user = user;
	}
	
	public UserMessageContainer getUser() {
		return user;
	}
	
}
