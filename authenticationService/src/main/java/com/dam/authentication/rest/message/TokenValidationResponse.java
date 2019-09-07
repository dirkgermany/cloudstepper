package com.dam.authentication.rest.message;

import java.util.UUID;

public class TokenValidationResponse extends RestResponse {
	
	private UUID tokenId;
	private Long userId;
	
	public TokenValidationResponse (Long userId, UUID tokenId) {
		super(new Long(200), "OK", "User validated.");
		setTokenId(tokenId);
		setUserId(userId);
	}

	public UUID getTokenId() {
		return tokenId;
	}

	public void setTokenId(UUID tokenId) {
		this.tokenId = tokenId;
	}

	public Long getUserId() {
		return userId;
	}

	public void setUserId(Long userId) {
		this.userId = userId;
	}

}
