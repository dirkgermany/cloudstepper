package com.dam.provider.rest.service.message;

import com.fasterxml.jackson.databind.JsonNode;

/**
 * LoginRequest is the typical request to authenticate the user against the system.
 * 
 * Response: TokenResponse
 * 
 * Scope: External Calls
 * 
 * @author dirk
 *
 */

public class WrappedRequest { // extends RestRequest {

    private JsonNode wrappedMsg;
    private String token;

    public WrappedRequest(JsonNode wrappedMsg, String token) {
//		super("DAM 2.0");
 //       this.ipAddress = ipAddress;
    	this.wrappedMsg=wrappedMsg;
    	this.setToken(token);
    }
    

	public JsonNode getWrappedMsg() {
		return wrappedMsg;
	}

	public void setWrappedMsg(JsonNode wrappedMsg) {
		this.wrappedMsg = wrappedMsg;
	}

	public String getToken() {
		return token;
	}


	public void setToken(String token) {
		this.token = token;
	}

}