package com.dam.user.rest.message;

import java.util.List;

import org.springframework.http.HttpStatus;

import com.dam.user.model.entity.User;

public class ListUserResponse extends RestResponse{
    private List<User> users;
    	
	public ListUserResponse (List<User> users) {
		super(HttpStatus.OK, "OK", "Users found");
		
		setUsers(users);
	}  

	private void setUsers(List<User> users) {
		this.users = users;
	}
	
	public List<User> getUsers() {
		return this.users;
	}
	
}
