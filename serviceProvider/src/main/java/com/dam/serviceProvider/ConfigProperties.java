package com.dam.serviceProvider;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "service")
public class ConfigProperties {
	
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

	/* 
	 * Derived Services
	 * Each Service is represented by
	 * a member
	 * a class
	 * a getter for the class 
	 */
	
	private AuthenticationService authenticationService = new AuthenticationService();
	private UserService userService = new UserService();
	private PersonService personService = new PersonService();
	private AddressService addressService = new AddressService();
	private PortfolioService portfolioService = new PortfolioService();

	public static class AuthenticationService extends Service {}
	public static class UserService extends Service {}
	public static class PersonService extends Service{}
	public static class AddressService extends Service {}
	public static class PortfolioService extends Service {}
	
	public UserService getUserService() {return userService;}
	public AuthenticationService getAuthenticationService() {return authenticationService;}
	public PersonService getPersonService() {return personService;}
	public AddressService getAddressService() {return addressService;}
	public PortfolioService getPortfolioService() {return portfolioService;}

}
