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
	
	@Value("${server.port}")
	String serverPort;

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
	
	@Value("${stock.service.protocol}")
	private String stockServiceProtocol;
	
	@Value("${stock.service.host}")
	private String stockServiceHost;
	
	@Value("${stock.service.key}")
	private String stockServiceKey;
	
	@Value("${stock.service.function}")
	private String stockServiceFunction;
	
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

	public String getStockServiceProtocol() {
		return stockServiceProtocol;
	}

	public void setStockServiceProtocol(String stockServiceProtocol) {
		this.stockServiceProtocol = stockServiceProtocol;
	}

	public String getStockServiceHost() {
		return stockServiceHost;
	}

	public void setStockServiceHost(String stockServiceHost) {
		this.stockServiceHost = stockServiceHost;
	}

	public String getStockServiceKey() {
		return stockServiceKey;
	}

	public void setStockServiceKey(String stockServiceKey) {
		this.stockServiceKey = stockServiceKey;
	}

	public String getStockServiceFunction() {
		return stockServiceFunction;
	}

	public void setStockServiceFunction(String stockServiceFunction) {
		this.stockServiceFunction = stockServiceFunction;
	}

	public Boolean getImportStockActive() {
		return importStockActive;
	}

	public String getServerPort() {
		return serverPort;
	}

	public void setServerPort(String serverPort) {
		this.serverPort = serverPort;
	}
}
