package com.dam.job;

import java.time.format.DateTimeFormatter;
import java.util.List;
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
public class TaskConfiguration {
	private static final Logger logger = LoggerFactory.getLogger(TaskConfiguration.class);
	private static final DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("HH:mm:ss");
	
	@Value("${server.port}")
	String serverPort;
	
	@Value("${tasks.activation.task.INVEST_INTENT_FINALIZE}")
	private Boolean investIntentActive;
	
	@Value("${tasks.activation.task.SELL_INTENT_FINALIZE}")
	private Boolean sellIntentActive;
	
	@Value("${tasks.activation.task.DEPOSIT_INTENT_FINALIZE}")
	private Boolean depositIntentActive;
	
	@Value("${tasks.activation.task.TRANSFER_TO_DEPOT_INTENT}")
	private Boolean transferToDepotIntentActive;
	
	@Value("${tasks.activation.task.TRANSFER_TO_ACCOUNT_INTENT}")
	private Boolean transferToAccountIntentActive;
	
	@Value("${tasks.activation.task.DEBIT_INTENT_FINALIZE}")
	private Boolean debitIntentActive;
	
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
	private List<String>successor;
	
	private List<String>predecessor;

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

	public List<String> getSuccessor() {
		return successor;
	}

	public void setSuccessor(List<String> successor) {
		this.successor = successor;
	}

	public List<String> getPredecessor() {
		return predecessor;
	}

	public void setPredecessor(List<String> predecessor) {
		this.predecessor = predecessor;
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

	public Boolean isInvestIntentFinalizeActive() {
		return investIntentActive;
	}

	public void setInvestIntentFinalizeActive(Boolean investIntentActive) {
		this.investIntentActive = investIntentActive;
	}

	public Boolean isSellIntentFinalizeActive() {
		return sellIntentActive;
	}

	public void setSellIntentFinalizeActive(Boolean sellIntentActive) {
		this.sellIntentActive = sellIntentActive;
	}

	public Boolean isDepositIntentFinalizeActive() {
		return depositIntentActive;
	}

	public void setDepositIntentFinalizeActive(Boolean depositIntentActive) {
		this.depositIntentActive = depositIntentActive;
	}

	public Boolean isDebitIntentFinalizeActive() {
		return debitIntentActive;
	}

	public void setDebitIntentFinalizeActive(Boolean debitIntentActive) {
		this.debitIntentActive = debitIntentActive;
	}

	public Boolean isTransferToDepotIntentActive() {
		return transferToDepotIntentActive;
	}

	public void setTransferToDepotIntentActive(Boolean transferToDepotIntentActive) {
		this.transferToDepotIntentActive = transferToDepotIntentActive;
	}
	
	public Boolean isTransferToAccountIntentActive() {
		return transferToAccountIntentActive;
	}

	public void setTransferToAccountIntentActive(Boolean transferToAccountIntentActive) {
		this.transferToAccountIntentActive = transferToAccountIntentActive;
	}

	public String getServerPort() {
		return serverPort;
	}

	public void setServerPort(String serverPort) {
		this.serverPort = serverPort;
	}
}
