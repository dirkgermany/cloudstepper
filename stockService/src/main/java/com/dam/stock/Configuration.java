package com.dam.stock;

import java.time.format.DateTimeFormatter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * Does two missions:
 * 
 * - Holding a list with active running tasks
 * - Holding a dependency chain for the tasks
 * @author dirk
 *
 */
@ConfigurationProperties(prefix = "tasks")
public class Configuration {
	private static final Logger logger = LoggerFactory.getLogger(Configuration.class);
	private static final DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("HH:mm:ss");
	
	@Value("${tasks.activation.task.IMPORT_STOCK}")
	private Boolean importStockActive;
	
	@Value("${provider.service.protocol}")
	private String serviceProviderProtocol;
	
	@Value("${provider.service.host}")
	private String serviceProviderHost;

	@Value("${provider.service.port}")
	private Long serviceProviderPort;
	
	@Value("${provider.service.user}")
	private String userName;
	
	@Value("${provider.service.password}")
	private String password;
	
	public Configuration () {
	}
	
	public String getServiceProviderProtocol() {
		return serviceProviderProtocol;
	}

	public void setServiceProviderProtocol(String serviceProviderProtocol) {
		this.serviceProviderProtocol = serviceProviderProtocol;
	}

	public String getServiceProviderHost() {
		return serviceProviderHost;
	}

	public void setServiceProviderHost(String serviceProviderHost) {
		this.serviceProviderHost = serviceProviderHost;
	}

	public Long getServiceProviderPort() {
		return serviceProviderPort;
	}

	public void setServiceProviderPort(Long serviceProviderPort) {
		this.serviceProviderPort = serviceProviderPort;
	}

	public String getUserName() {
		return userName;
	}

	public void setUserName(String userName) {
		this.userName = userName;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public Boolean isImportStockActive() {
		return importStockActive;
	}

	public void setImportStockActive(Boolean importStockActive) {
		this.importStockActive = importStockActive;
	}
}
