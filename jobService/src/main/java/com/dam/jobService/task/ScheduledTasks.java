package com.dam.jobService.task;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import com.dam.exception.DamServiceException;
import com.dam.jobService.TaskConfiguration;
import com.dam.jobService.task.TaskController.SyncRunner;
import com.dam.jobService.task.jobs.JobDepositIntentFinalize;
import com.dam.jobService.task.jobs.JobInvestIntentFinalize;
import com.dam.jobService.task.jobs.JobTransferToDepotIntent;
import com.dam.jobService.type.ActionType;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@EnableScheduling
@Component
public class ScheduledTasks {

	@Autowired
	TaskConfiguration taskConfiguration;

	@Autowired
	TaskController taskController;

	@Autowired
	JobInvestIntentFinalize jobInvestIntentFinalize;

	@Autowired
	JobTransferToDepotIntent jobTransferToDepotIntent;
	
	@Autowired
	JobDepositIntentFinalize jobDepositIntentFinalize;

	private static final Logger logger = LoggerFactory.getLogger(ScheduledTasks.class);
	private static final DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("HH:mm:ss");
	private static final int MAX_RETRIES = 3;
	
	public ScheduledTasks () {
	}

	/*
	 * Waits for precedessor jobs, blocks successors if required, executes a job and
	 * logs the scheduling. If no other process is active, logs out from Service
	 * Provider
	 */
	private boolean executeJob(ActionType task, Client client) throws DamServiceException {
		logger.info("Job Service :: {}: Job {} - Status: {}", dateTimeFormatter.format(LocalDateTime.now()), task,
				"scheduled");
		
		boolean success = true;
		
		// wait if another Job blocks this one
		taskController.getSyncWaiter(task);
		logger.info("Job Service :: {}: Job {} - Status: {}", dateTimeFormatter.format(LocalDateTime.now()), task,
				"started");

		// Start runner for blocking dependent Jobs
		SyncRunner runner = taskController.getSyncRunner(task);

		try {
			client.executeJob();
		} catch (Exception ex) {
			logger.error("Scheduled Task", ex);
			client.logout(this.getClass().getName());
			success = false;
		} finally {
			logger.info("Job Service :: {}: Job {} - Status: {}", dateTimeFormatter.format(LocalDateTime.now()), task,
					"finished");
			// Other Jobs perhaps are waiting
			runner.interrupt();
		}
		return success;
	}
	
	/*
	 * Starts a job and makes retries if it was not successfull.
	 * Two errors are most presumably:
	 * - another job/task for the same user is still in progress
	 * - the token has expired
	 */
	private void startJob(ActionType action, Client client) throws DamServiceException {
		boolean success = false;
		int retries = 0;
		while (!success && retries <= MAX_RETRIES) {
			success = executeJob(action, jobInvestIntentFinalize);
			if (!success) {
				try {
					Thread.sleep(10000);
				} catch (InterruptedException e) {
				}
			}
		}
	}

	@Scheduled(cron = "${cron.depot.investIntentFinalize}")
	public void scheduleInvestIntentsFinalize() throws DamServiceException {
		if (taskConfiguration.isInvestIntentFinalizeActive()) {
			startJob( ActionType.INVEST_INTENT, jobInvestIntentFinalize);
		}
	}

	@Scheduled(cron = "${cron.depot.depositIntentFinalize}")
	public void scheduleDepositIntentsFinalize() throws DamServiceException {
		if (taskConfiguration.isDepositIntentFinalizeActive()) {
			startJob(ActionType.INVEST_INTENT, jobDepositIntentFinalize);
		}
	}

	@Scheduled(cron = "${cron.depot.transferToDepotIntent}")
	public void scheduleTransferToDepotIntents() throws DamServiceException {
		if (taskConfiguration.isTransferToDepotIntentActive()) {
			startJob(ActionType.TRANSFER_TO_DEPOT_INTENT, jobTransferToDepotIntent);
		}
	}

}
