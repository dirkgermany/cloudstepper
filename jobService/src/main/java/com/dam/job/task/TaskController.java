package com.dam.job.task;

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
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.dam.job.TaskConfiguration;
import com.dam.job.type.ActionType;

@Component
public class TaskController {

	@Autowired
	private TaskConfiguration taskConfiguration;

	private static Map<ActionType, List<ActionType>> dependencyList = new HashMap<>();
	private Map<ActionType, Thread> runningJobList = new ConcurrentHashMap<>();
	private static final Logger logger = LoggerFactory.getLogger(ScheduledTasks.class);
	private static final DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("HH:mm:ss");

	public TaskController() {
	}

	/**
	 * Delivers a list of TaskType if a dependency exists. Otherwise NULL.
	 * 
	 * @param dependentTask
	 * @return
	 */
	public List<ActionType> getPredecessorListForTask(ActionType successorTask) {
		return getDependencyList().get(successorTask);
	}

	public int getRunningJobListSize() {
		return null == runningJobList ? 0 : runningJobList.size();
	}

	public void addToRunningJobList(ActionType activeTask, Thread t) {
		runningJobList.put(activeTask, t);
	}

	/**
	 * Takes a list of predecessors and checks if one of them has an active thread.
	 * 
	 * @param predecessorList
	 * @return
	 */
	public Thread getSyncThread(List<ActionType> predecessorList) {
		if (null == runningJobList || 0 == runningJobList.size() || null == predecessorList
				|| 0 == predecessorList.size()) {
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

	private Map<ActionType, List<ActionType>> getDependencyList() {
		if (dependencyList.size() == 0) {
			initDependencies();
		}
		return dependencyList;
	}

	private void initDependencies() {
		if (null != taskConfiguration.getSuccessor()) {
			Iterator<String> it = taskConfiguration.getSuccessor().iterator();
			int index = 0;
			while (it.hasNext()) {
				ActionType successor = ActionType.valueOf(it.next());
				ActionType predecessor = ActionType.valueOf(taskConfiguration.getPredecessor().get(index));

				List<ActionType> predecessorTaskList = dependencyList.get(successor);
				if (null == predecessorTaskList) {
					predecessorTaskList = new ArrayList<>();
				}
				predecessorTaskList.add(ActionType.valueOf(predecessor.name()));

				dependencyList.put(successor, predecessorTaskList);
				index++;
			}
		}
	}

	public SyncWaiter getSyncWaiter(ActionType successor) {
		return new SyncWaiter(successor);
	}

	public SyncRunner getSyncRunner(ActionType action) {
		return new SyncRunner(action);
	}

	public class SyncRunner extends Thread {
		private ActionType action;

		public SyncRunner(ActionType action) {
			this.action = action;
			addToRunningJobList(this.action, this);
			this.start();
		}

		@Override
		public void run() {
			while (true) {
				try {
					Thread.sleep(1000);

				} catch (InterruptedException e) {
					removeFromRunningJobList(this.action);
					break;
				}
			}
		}
	}

	public class SyncWaiter {
		private final Long maxWaitTime = 1000 * 60 * 5L; // 5 minutes

		public SyncWaiter(ActionType successor) {
			this.wait(successor);
		}

		public void wait(ActionType successor) {

			List<ActionType> precedessorList = getPredecessorListForTask(successor);
			Thread precedessorThread = getSyncThread(precedessorList);
			while (null != precedessorThread) {
				logger.info("Job Service :: {}: Job {} - Status: {}", dateTimeFormatter.format(LocalDateTime.now()),
						successor, "waiting for thread" + precedessorThread);

				try {
					precedessorThread.join(maxWaitTime);
				} catch (InterruptedException e) {
				}
				precedessorThread = getSyncThread(precedessorList);
			}
		}
	}

}
