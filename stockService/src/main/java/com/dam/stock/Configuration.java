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
	
	@Value("${domain}")
	private String domain;
	
	@Value("${portfolio.service.protocol}")
	private String portfolioServiceProtocol;
	
	@Value("${portfolio.service.host}")
	private String portfolioServiceHost;
	
	@Value("${portfolio.service.port}")
	private Long portfolioServicePort;
	
	@Value("${portfolio.user.name}")
	private String portfolioUserName;
	
	@Value("${portfolio.user.password}")
	private String portfolioUserPassword;
	
	
	@Value("${stock.service.protocol}")
	private String stockServiceProtocol;
	
	@Value("${stock.service.host}")
	private String stockServiceHost;
	
	@Value("${stock.user.apikey}")
	private String stockServiceApiKey;
	
	@Value("${stock.service.function}")
	private String stockServiceFunction;
	
	@Value("${stock.service.outputsize}")
	private String stockServiceOutputSize;
	
	public Configuration () {
	}

	public static Logger getLogger() {
		return logger;
	}

	public static DateTimeFormatter getDatetimeformatter() {
		return dateTimeFormatter;
	}

	public String getPortfolioServiceProtocol() {
		return portfolioServiceProtocol;
	}

	public void setPortfolioServiceProtocol(String portfolioServiceProtocol) {
		this.portfolioServiceProtocol = portfolioServiceProtocol;
	}

	public String getPortfolioServiceHost() {
		return portfolioServiceHost;
	}

	public void setPortfolioServiceHost(String portfolioServiceHost) {
		this.portfolioServiceHost = portfolioServiceHost;
	}

	public Long getPortfolioServicePort() {
		return portfolioServicePort;
	}

	public void setPortfolioServicePort(Long portfolioServicePort) {
		this.portfolioServicePort = portfolioServicePort;
	}

	public String getPortfolioUserName() {
		return portfolioUserName;
	}

	public void setPortfolioUserName(String portfolioUserName) {
		this.portfolioUserName = portfolioUserName;
	}

	public String getPortfolioUserPassword() {
		return portfolioUserPassword;
	}

	public void setPortfolioUserPassword(String portfolioUserPassword) {
		this.portfolioUserPassword = portfolioUserPassword;
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

	public String getStockServiceApiKey() {
		return stockServiceApiKey;
	}

	public void setStockServiceApiKey(String stockServiceApiKey) {
		this.stockServiceApiKey = stockServiceApiKey;
	}

	public String getStockServiceFunction() {
		return stockServiceFunction;
	}

	public void setStockServiceFunction(String stockServiceFunction) {
		this.stockServiceFunction = stockServiceFunction;
	}

	public String getStockServiceOutputSize() {
		return stockServiceOutputSize;
	}

	public void setStockServiceOutputSize(String stockServiceOutputSize) {
		this.stockServiceOutputSize = stockServiceOutputSize;
	}

	public String getDomain() {
		return domain;
	}

	public void setDomain(String domain) {
		this.domain = domain;
	}
}
