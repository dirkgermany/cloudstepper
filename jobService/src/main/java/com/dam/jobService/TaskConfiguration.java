package com.dam.jobService;

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
	
	private Map<ActionType, Thread> activeTaskList = new ConcurrentHashMap<>();
	private Map<ActionType, List<ActionType>>dependencyList = new HashMap<>();
	
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
	
	public int getActiveTaskListSize () {
		return null == activeTaskList ? 0 : activeTaskList.size();
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
	
	public void addToActiveTaskList(ActionType activeTask, Thread t) {
		activeTaskList.put(activeTask, t);
	}
	
	/**
	 * Takes a list of predecessors and checks if one of them has an active thread.
	 * @param predecessorList
	 * @return
	 */
	public Thread getSyncThread (List<ActionType> predecessorList) {
		if (null == activeTaskList || 0 == activeTaskList.size() || null == predecessorList || 0 == predecessorList.size()) {
			return null;
		}
		
		Iterator<ActionType> it = predecessorList.iterator();
		while (it.hasNext()) {
			ActionType predecessor = it.next();
			Thread thread = activeTaskList.get(predecessor);
			if (null != thread) {
				return thread;
			}
		}
		return null;
	}
	
	public void removeFromActiveTaskList(ActionType inactiveTask) {
		activeTaskList.remove(inactiveTask);
	}
	
	public boolean isTaskActive(ActionType task) {
		if (null != activeTaskList.get(task)) {
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
}
