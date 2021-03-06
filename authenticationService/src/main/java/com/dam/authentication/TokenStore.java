package com.dam.authentication;

import java.util.Iterator;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Import;
import org.springframework.stereotype.Component;

import com.dam.authentication.model.User;
import com.dam.authentication.rest.message.LogoutRequest;
import com.dam.exception.CsServiceException;

/**
 * Handles active and non active Tokens
 * 
 * @author dirk
 *
 */
@Component
@Import({ ConfigProperties.class, ConfigProperties.class })
public class TokenStore {

	private static final Logger logger = LoggerFactory.getLogger(TokenStore.class);

	@Autowired
	ConfigProperties config;

	private Long lastStoreUpdate;

	private static Map<UUID, Token> activeTokenMap = new ConcurrentHashMap<>();
	private static Map<UUID, Token> expiredTokenMap = new ConcurrentHashMap<>();
	private static Map<Long, UUID> activeUserMap = new ConcurrentHashMap<>();

	public Token createNewToken(User user) throws CsServiceException {
		if (null == user) {
			throw new CsServiceException(424L, "User is null", "Token for user could not be created");
		}

		// kill active Token of user (user is still logged in)
		UUID userTokenId = activeUserMap.get(user.getUserId());
		if (null != userTokenId) {
			Token existingToken = activeTokenMap.get(userTokenId);
			archiveToken(existingToken);
		}

		// create new token
		Token newToken = new Token(user, getMaxTokenAge());

		activeTokenMap.put(newToken.getTokenId(), newToken);
		activeUserMap.put(user.getUserId(), newToken.getTokenId());

		return newToken;
	}

	public Token getToken(UUID tokenId) {
		return activeTokenMap.get(tokenId);
	}

	/**
	 * Search Token, validate, refresh expireTime
	 * 
	 * @param validationToken
	 * @return
	 * @throws CsServiceException
	 */
	public Token validateAndRefreshToken(Token validationToken) throws CsServiceException {
		refreshTokenStore();

		Token storedToken = activeTokenMap.get(validationToken.getTokenId());
		if (null == storedToken) {
			throw new CsServiceException(500L, "No valid Token", "Token not in TokenStore.");
		}

		// Check age of token
		if (!storedToken.getIsValid()) {
			archiveToken(storedToken);
			throw new CsServiceException(500L, "No valid Token", "Token lifetime exceeded.");
		}

		// refresh expireTime
		// update Map
		// return Token
		if (tokenValidate(validationToken, storedToken)) {
			storedToken.setExpireTime(System.currentTimeMillis() + getMaxTokenAge());
			activeTokenMap.put(storedToken.getTokenId(), storedToken);
			return storedToken;
		}

		throw new CsServiceException(500L, "No valid Token", "Token does not exist.");
	}

	public User getUser(Long userId) {
		UUID tokenId = activeUserMap.get(userId);

		if (null != tokenId) {
			Token token = activeTokenMap.get(tokenId);
			if (null != token) {
				return token.getUser();
			}
		}
		return null;
	}

	private void archiveToken(Token tokenToArchive) {
		if (null == tokenToArchive) {
			return;
		}
		activeTokenMap.remove(tokenToArchive.getTokenId());
		expiredTokenMap.put(tokenToArchive.getTokenId(), tokenToArchive);

		activeUserMap.remove(tokenToArchive.getUser().getUserId());
	}

	/**
	 * Validates age, userName and userId
	 * 
	 * @param validationToken
	 * @param storedToken
	 * @return
	 */
	private Boolean tokenValidate(Token validationToken, Token storedToken) {

		if (null == validationToken || null == storedToken) {
			return Boolean.FALSE;
		}

		if (storedToken.getUser().getUserName().equals(validationToken.getUser().getUserName())
				&& storedToken.getTokenId().equals(validationToken.getTokenId())) {
			return Boolean.TRUE;
		}

		return Boolean.FALSE;
	}

	public Long logout(LogoutRequest logoutRequest) {
		Token storedToken = activeTokenMap.get(logoutRequest.getTokenId());
		if (null != storedToken && logoutRequest.getUserName().equals(storedToken.getUser().getUserName())) {
			archiveToken(storedToken);
			return 200L;
		}
		return 7L;
	}

	private void refreshTokenStore() {
		if (null == this.lastStoreUpdate) {
			this.lastStoreUpdate = System.currentTimeMillis();
			return;
		}

		logger.debug("TokenStore::refreshTokenStore@activeTokenMap: ", activeTokenMap.entrySet());
		Long currentTime = System.currentTimeMillis();
		if (this.lastStoreUpdate + config.getTokenConfiguration().getCheckOldTokenInterval() < currentTime) {
			Iterator<Token> tokens = activeTokenMap.values().iterator();
			while (tokens.hasNext()) {
				Token nextToken = tokens.next();
				logger.debug("SysTime: " + currentTime + "; Token: " + nextToken.getTokenId() + " ;expireTime: "
						+ nextToken.getExpireTime());
				if (null != nextToken && !(nextToken.getIsValid() && nextToken.getIsValid())) {
					logger.debug("Token expired: " + nextToken.getTokenId());
					nextToken.setIsValid(Boolean.FALSE);
					expiredTokenMap.put(nextToken.getTokenId(), nextToken);
					tokens.remove();
				}
			}
		}
		this.lastStoreUpdate = System.currentTimeMillis();
	}

	public Long getMaxTokenAge() {
		return config.getTokenConfiguration().getMaxTokenAge();
	}

}
