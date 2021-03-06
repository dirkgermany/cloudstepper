package com.dam.authentication;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "token")
public class ConfigProperties {

	private TokenConfiguration tokenConfiguration;
	
	@Value("${server.port}")
	String serverPort;
	
	@Value("${permission.updateInterval}")
	Long permissionUpdateInterval;

	
	// Superclass
	public static class Service {
		private String port;
		private String host;
		private String protocol;

		public String getPort() {return port;}
		public void setPort(String port) {this.port = port;}

		public String getHost() {return host;}
		public void setHost(String host) {this.host = host;}

		public String getProtocol() {return protocol;}
		public void setProtocol(String protocol) {this.protocol = protocol;}
		
		public String getServiceUrl() {
			return getProtocol() + "://" + getHost() + ":" + getPort();
		}
	}
	
	
	public static class TokenConfiguration {
		private Long maxTokenAge;
		private Long checkOldTokenInterval;

		public Long getMaxTokenAge() {
			return maxTokenAge;
		}

		public void setMaxTokenAge(Long maxTokenAge) {
			this.maxTokenAge = maxTokenAge;
		}

		public Long getCheckOldTokenInterval() {
			return checkOldTokenInterval;
		}

		public void setCheckOldTokenInterval(Long checkOldTokenInterval) {
			this.checkOldTokenInterval = checkOldTokenInterval;
		}
	}


	public TokenConfiguration getTokenConfiguration() {
		return tokenConfiguration;
	}

	public void setTokenConfiguration(TokenConfiguration tokenConfiguration) {
		this.tokenConfiguration = tokenConfiguration;
	}

	
	/* 
	 * Derived Services
	 * Each Service is represented by
	 * a member
	 * a class
	 * a getter for the class 
	 */
	public static class UserService extends Service {}
	private UserService userService = new UserService();
	public UserService getUserService() {return userService;}

	public static class PermissionService extends Service {}
	private PermissionService permissionService = new PermissionService();
	public PermissionService getPermissionService() {return permissionService;}

	public String getServerPort() {
		return serverPort;
	}

	public void setServerPort(String serverPort) {
		this.serverPort = serverPort;
	}
	
	public Long getPermissionUpdateInterval() {
		return this.permissionUpdateInterval;
	}
	
	public void setPermissionUpdateInterval(Long updateInterval) {
		this.permissionUpdateInterval = updateInterval;
	}
	
}
