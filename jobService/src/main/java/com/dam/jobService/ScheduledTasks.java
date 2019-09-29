package com.dam.jobService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.AutoConfigureBefore;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import com.dam.exception.DamServiceException;
import com.dam.jobService.task.depot.DepotJob;
import com.dam.jobService.type.TaskType;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@EnableScheduling
@Component
public class ScheduledTasks {

	@Autowired
	TaskConfiguration taskConfiguration;
	
	@Autowired
	DepotJob depotJob;

	
	private final Long maxWaitTime = 1000*60*20L; // 20 minutes

	private static final Logger logger = LoggerFactory.getLogger(ScheduledTasks.class);
	private static final DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("HH:mm:ss");

	@Scheduled(cron = "${cron.depot.investIntent}")
	public  void scheduleInvestIntents() throws DamServiceException {
		TaskType task = TaskType.INVEST_INTENT;
		
		// wait if another Job blocks this one
		new SyncWaiter(task);
		
		// Start runner for blocking dependent Jobs
		SyncRunner runner = new SyncRunner(task);
		
		try {
		depotJob.login();
		} catch (Exception e) {
			runner.interrupt();
			throw e;
		}

		logger.info("scheduled Job :: {}: {}", dateTimeFormatter.format(LocalDateTime.now()), task);
		
		
		// Other Jobs perhaps are waiting
		runner.interrupt();
	}

	@Scheduled(cron = "${cron.depot.transferToDepotIntent}")
	public void scheduleTransferToDepotIntents() {
		TaskType task = TaskType.TRANSFER_TO_DEPOT_INTENT;
		
		// wait if another Job blocks this one
		new SyncWaiter(task);

		// Start runner for blocking dependent Jobs
		SyncRunner runner = new SyncRunner(task);

		logger.info("scheduled Job :: {}: {}", dateTimeFormatter.format(LocalDateTime.now()), task);
		
		// Other Jobs perhaps are waiting
		runner.interrupt();
	}
	

	/*
	 * Class to wait for blocking Jobs
	 */
	private class SyncWaiter {
		public SyncWaiter (TaskType successor) {
			this.wait(successor);
		}
		
		public void wait (TaskType successor) {

			List<TaskType> precedessorList = taskConfiguration.getPredecessorListForTask(successor);
			
			Thread precedessorThread = taskConfiguration.getSyncThread(precedessorList);
			while (null != precedessorThread) {
				try {
					precedessorThread.join(maxWaitTime);
				} catch (InterruptedException e) {
				}
				precedessorThread = taskConfiguration.getSyncThread(precedessorList);
			}

		}
	}

	// Synchronization Class
	private class SyncRunner extends Thread {			
		private TaskType activeTask;
		
		public SyncRunner (TaskType activeTask) {
			
			this.activeTask = activeTask;
			taskConfiguration.addToActiveTaskList(this.activeTask, this);
			this.start();
		}
		
		@Override
		public void run() {
			while (true) {
				try {
					Thread.sleep(1000);

				} catch (InterruptedException e) {
					taskConfiguration.removeFromActiveTaskList(this.activeTask);
					break;
				}
			}
		}
	}

}
