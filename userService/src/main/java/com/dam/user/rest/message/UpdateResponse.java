package com.dam.user.rest.message;

import com.dam.user.model.entity.UserMessageContainer;

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
