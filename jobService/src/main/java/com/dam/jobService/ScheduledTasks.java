package com.dam.jobService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import com.dam.exception.DamServiceException;
import com.dam.jobService.task.Client;
import com.dam.jobService.task.JobInvestIntent;
import com.dam.jobService.task.JobTransferToDepotIntent;
import com.dam.jobService.type.ActionType;

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
	JobInvestIntent jobInvestIntent;

	@Autowired
	JobTransferToDepotIntent jobTransferToDepotIntent;

	private final Long maxWaitTime = 1000 * 60 * 20L; // 20 minutes

	private static final Logger logger = LoggerFactory.getLogger(ScheduledTasks.class);
	private static final DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("HH:mm:ss");

	/*
	 * Waits for precedessor jobs, blocks successors if required, executes a job and
	 * logs the scheduling. If no other process is active, logs out from Service
	 * Provider
	 */
	private void executeJob(ActionType task, Client client) throws DamServiceException {
		logger.info("Job Service :: {}: Job {} - Status: {}", dateTimeFormatter.format(LocalDateTime.now()), task,
				"scheduled");

		// wait if another Job blocks this one
		new SyncWaiter(task);
		logger.info("Job Service :: {}: Job {} - Status: {}", dateTimeFormatter.format(LocalDateTime.now()), task,
				"started");

		// Start runner for blocking dependent Jobs
		SyncRunner runner = new SyncRunner(task);

		try {
			client.executeJob();
		} catch (Exception ex) {
			logger.error("Scheduled Task", ex);
		}
		finally {
			logger.info("Job Service :: {}: Job {} - Status: {}", dateTimeFormatter.format(LocalDateTime.now()), task,
					"finished");
			// Other Jobs perhaps are waiting
			runner.interrupt();
		}


//		// Other Jobs perhaps are waiting
//		runner.interrupt();
	}

	@Scheduled(cron = "${cron.depot.investIntent}")
	public void scheduleInvestIntents() throws DamServiceException {
		ActionType action = ActionType.INVEST_INTENT;
		executeJob(action, jobInvestIntent);
	}

	@Scheduled(cron = "${cron.depot.transferToDepotIntent}")
	public void scheduleTransferToDepotIntents() throws DamServiceException {
		ActionType action = ActionType.TRANSFER_TO_DEPOT_INTENT;
		executeJob(action, jobTransferToDepotIntent);
	}

	/*
	 * Class to wait for blocking Jobs
	 */
	private class SyncWaiter {
		public SyncWaiter(ActionType successor) {
			this.wait(successor);
		}

		public void wait(ActionType successor) {

			List<ActionType> precedessorList = taskConfiguration.getPredecessorListForTask(successor);

			Thread precedessorThread = taskConfiguration.getSyncThread(precedessorList);
			while (null != precedessorThread) {
				logger.info("Job Service :: {}: Job {} - Status: {}", dateTimeFormatter.format(LocalDateTime.now()),
						successor, "waiting for thread" + precedessorThread);

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
		private ActionType action;

		public SyncRunner(ActionType action) {

			this.action = action;
			taskConfiguration.addToActiveTaskList(this.action, this);
			this.start();
		}

		@Override
		public void run() {
			while (true) {
				try {
					Thread.sleep(1000);

				} catch (InterruptedException e) {
					taskConfiguration.removeFromActiveTaskList(this.action);
					break;
				}
			}
		}
	}

}
