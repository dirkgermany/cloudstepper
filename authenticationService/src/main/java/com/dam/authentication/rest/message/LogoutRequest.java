package com.dam.authentication.rest.message;

import java.util.UUID;

import com.dam.authentication.RestRequest;
import com.dam.authentication.model.User;

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

public class LogoutRequest extends RestRequest {

    private UUID tokenId;
    private User user = new User();

    public LogoutRequest(String userName, UUID tokenId) {
		super("DAM 2.0");
        user.setUserName(userName);
        setTokenId(tokenId);
    }

    public String getUserName() {
        return user.getUserName();
    }

	public UUID getTokenId() {
		return tokenId;
	}

	public void setTokenId(UUID tokenId) {
		this.tokenId = tokenId;
	}

}