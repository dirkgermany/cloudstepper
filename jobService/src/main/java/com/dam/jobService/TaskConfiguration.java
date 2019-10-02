package com.dam.jobService;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;

import com.dam.exception.DamServiceException;
import com.dam.jobService.type.ActionType;

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
	
	private Map<ActionType, Thread> runningJobList = new ConcurrentHashMap<>();
	private Map<ActionType, List<ActionType>>dependencyList = new HashMap<>();
	
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
	
	// delivered by configuration
	private List<String>successorList;
	
	private List<String>predecessorList;
		
	private void initDependencies() {
		Iterator<String> it = successorList.iterator();
		int index = 0;
		while (it.hasNext()) {
			ActionType successor = ActionType.valueOf(it.next());
			ActionType predecessor = ActionType.valueOf(predecessorList.get(index));
			
			List<ActionType> predecessorTaskList = dependencyList.get(successor);
			if (null == predecessorTaskList) {
				predecessorTaskList = new ArrayList<>();
			}
			predecessorTaskList.add(ActionType.valueOf(predecessor.name()));
			
			dependencyList.put(successor, predecessorTaskList);
			index++;
		}
	}
	
	public int getRunningJobListSize () {
		return null == runningJobList ? 0 : runningJobList.size();
	}
	
	/**
	 * Delivers a list of TaskType if a dependency exists. Otherwise NULL.
	 * @param dependentTask
	 * @return
	 */
	public List<ActionType> getPredecessorListForTask(ActionType successorTask) {
		if (dependencyList.size() == 0) {
			initDependencies();
		}		
		if (dependencyList.size() == 0) {
			return null;
		}
		
		return dependencyList.get(successorTask);
	}
	
	public void addToRunningJobList(ActionType activeTask, Thread t) {
		runningJobList.put(activeTask, t);
	}
	
	/**
	 * Takes a list of predecessors and checks if one of them has an active thread.
	 * @param predecessorList
	 * @return
	 */
	public Thread getSyncThread (List<ActionType> predecessorList) {
		if (null == runningJobList || 0 == runningJobList.size() || null == predecessorList || 0 == predecessorList.size()) {
			return null;
		}
		
		Iterator<ActionType> it = predecessorList.iterator();
		while (it.hasNext()) {
			ActionType predecessor = it.next();
			Thread thread = runningJobList.get(predecessor);
			if (null != thread) {
				return thread;
			}
		}
		return null;
	}
	
	public void removeFromRunningJobList(ActionType inactiveTask) {
		runningJobList.remove(inactiveTask);
	}
	
	public boolean isJobRunning(ActionType task) {
		if (null != runningJobList.get(task)) {
			return true;
		}
		return false;
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

	public List<String> getSuccessor() {
		return successorList;
	}

	public void setSuccessor(List<String> successorList) {
		this.successorList = successorList;
	}

	public List<String> getPredecessor() {
		return predecessorList;
	}

	public void setPredecessor(List<String> predecessorList) {
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
