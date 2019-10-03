package com.dam.jobService;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;

import com.dam.jobService.task.ScheduledTasks;

/**
 * Does two missions:
 * 
 * - Holding a list with active running tasks
 * - Holding a dependency chain for the tasks
 * @author dirk
 *
 */
@ConfigurationProperties(prefix = "tasks")
public class TaskConfiguration {
	private static final Logger logger = LoggerFactory.getLogger(ScheduledTasks.class);
	private static final DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("HH:mm:ss");
	
	
	@Value("${tasks.activation.task.INVEST_INTENT}")
	private Boolean isInvestIntentActive;
	
	@Value("${tasks.activation.task.TRANSFER_TO_DEPOT_INTENT}")
	private Boolean isTransferToDepotIntentActive;
	
	@Value("${provider.service.protocol}")
	private String serviceProviderProtocol;
	
	@Value("${provider.service.host}")
	private String serviceProviderHost;

	@Value("${provider.service.port}")
	private Long serviceProviderPort;
	
	@Value("${provider.user.name}")
	private String userName;
	
	@Value("${provider.user.password}")
	private String password;
	
	public TaskConfiguration () {
		
	}
	
	// delivered by configuration
	private List<String>successorList;
	
	private List<String>predecessorList;
		
	

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

	public List<String> getSuccessorList() {
		return successorList;
	}

	public void setSuccessorList(List<String> successorList) {
		this.successorList = successorList;
	}

	public List<String> getPredecessorList() {
		return predecessorList;
	}

	public void setPredecessorList(List<String> predecessorList) {
		this.predecessorList = predecessorList;
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

	public Boolean isInvestIntentActive() {
		logger.info("Job Service :: {} : Configuration IsInvestIntentActive {}", dateTimeFormatter.format(LocalDateTime.now()), isInvestIntentActive);
		return isInvestIntentActive;
	}

	public void setInvestIntentActive(Boolean isInvestIntentActive) {
		logger.info("Job Service :: {} : Configuration IsInvestIntentActive {}", dateTimeFormatter.format(LocalDateTime.now()), isInvestIntentActive);
		this.isInvestIntentActive = isInvestIntentActive;
	}

	public Boolean isTransferToDepotIntentActive() {
		logger.info("Job Service :: {} : Configuration IsTransferToDepotIntentActive {}", dateTimeFormatter.format(LocalDateTime.now()), isTransferToDepotIntentActive);
		return isTransferToDepotIntentActive;
	}

	public void setTransferToDepotIntentActive(Boolean isTransferToDepotIntentActive) {
		logger.info("Job Service :: {} : Configuration IsTransferToDepotIntentActive {}", dateTimeFormatter.format(LocalDateTime.now()), isTransferToDepotIntentActive);
		this.isTransferToDepotIntentActive = isTransferToDepotIntentActive;
	}
}
