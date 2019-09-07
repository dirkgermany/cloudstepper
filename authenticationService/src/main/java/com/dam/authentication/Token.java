package com.dam.authentication;

import java.util.UUID;

import org.springframework.stereotype.Component;

import com.dam.authentication.model.User;

@Component
public class Token {

	private UUID tokenId;
	private User user;
	private Long creationTime;
	private Long expireTime;
	private String rights;
	

	public Token() {

	}

	public Token(UUID tokenId, User user, Long creationTime, Long expireTime) {
		if (null == creationTime) {
			creationTime = System.currentTimeMillis();
		}
		setTokenId(tokenId);
		setUser(user);
		setCreationTime(creationTime);
		setExpireTime(expireTime);
	}

	public Token(UUID tokenId, User user, Long expireTime) {
		this(tokenId, user, System.currentTimeMillis(), expireTime);
	}

	public Token(UUID tokenId, User user) {
		this(tokenId, user, System.currentTimeMillis(), System.currentTimeMillis());
	}

	public UUID getTokenId() {
		return tokenId;
	}

	public void setTokenId(UUID tokenId) {
		this.tokenId = tokenId;
	}

	public Long getCreationTime() {
		return creationTime;
	}

	public void setCreationTime(Long creationTime) {
		this.creationTime = creationTime;
	}

	public Long getExpireTime() {
		return expireTime;
	}

	public void setExpireTime(Long expireTime) {
		this.expireTime = expireTime;
	}

	public String getRights() {
		return rights;
	}

	public void setRights(String rights) {
		this.rights = rights;
	}

	public User getUser() {
		return user;
	}

	public void setUser(User user) {
		this.user = user;
	}

}
