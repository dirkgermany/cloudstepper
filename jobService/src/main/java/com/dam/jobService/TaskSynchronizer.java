package com.dam.jobService;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.springframework.boot.context.properties.ConfigurationProperties;

import com.dam.jobService.types.TaskType;

/**
 * Does two missions:
 * 
 * - Holding a list with active running tasks
 * - Holding a dependency chain for the tasks
 * @author dirk
 *
 */
@ConfigurationProperties(prefix = "tasks.linked-list")
public class TaskSynchronizer {
	
	private Map<String, TaskType> activeTaskList = new ConcurrentHashMap<>();
	private List<TaskType>dependencyList = new LinkedList<>();
	
	private List<String>entry;
	
	private void initLinkedList() {
		Iterator<String>it = entry.iterator();
		while (it.hasNext()) {
			TaskType task = TaskType.valueOf(it.next());
			dependencyList.add(task);
		}
	}
	
	/**
	 * Delivers a TaskType if a dependency exists. Otherwise NULL.
	 * @param dependentTask
	 * @return
	 */
	public TaskType getPredessorForTask(TaskType dependentTask) {
		if (dependencyList.size() == 0) {
			initLinkedList();
		}
		
		int index = dependencyList.indexOf(dependentTask);
		if (-1 != index && dependencyList.size() > 1) {
			int preIndex = 0 == index ? dependencyList.size()-1 : index -1;
			return dependencyList.get(preIndex);
		}
		return null;
	}
	
	public void addToActiveTaskList(TaskType activeTask) {
		activeTaskList.put(activeTask.name(), activeTask);
	}
	
	public void removeFromActiveTaskList(TaskType inactiveTask) {
		activeTaskList.remove(inactiveTask.name());
	}
	
	public boolean isTaskActive(TaskType task) {
		if (null != activeTaskList.get(task.name())) {
			return true;
		}
		return false;
	}

	public List<String> getEntry() {
		return entry;
	}

	public void setEntry(List<String> entry) {
		this.entry = entry;
	}


}
