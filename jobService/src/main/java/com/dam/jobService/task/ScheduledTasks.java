package com.dam.jobService.task;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import com.dam.exception.DamServiceException;
import com.dam.jobService.TaskConfiguration;
import com.dam.jobService.task.TaskController.SyncRunner;
import com.dam.jobService.task.jobs.JobInvestIntent;
import com.dam.jobService.task.jobs.JobTransferToDepotIntent;
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
	TaskController taskController;

	@Autowired
	JobInvestIntent jobInvestIntent;

	@Autowired
	JobTransferToDepotIntent jobTransferToDepotIntent;

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
		taskController.getSyncWaiter(task);
		logger.info("Job Service :: {}: Job {} - Status: {}", dateTimeFormatter.format(LocalDateTime.now()), task,
				"started");

		// Start runner for blocking dependent Jobs
		SyncRunner runner = taskController.getSyncRunner(task);

		try {
			client.executeJob();
		} catch (Exception ex) {
			logger.error("Scheduled Task", ex);
		} finally {
			logger.info("Job Service :: {}: Job {} - Status: {}", dateTimeFormatter.format(LocalDateTime.now()), task,
					"finished");
			// Other Jobs perhaps are waiting
			runner.interrupt();
		}
	}

	@Scheduled(cron = "${cron.depot.investIntent}")
	public void scheduleInvestIntents() throws DamServiceException {
		if (taskConfiguration.isInvestIntentActive()) {
			ActionType action = ActionType.INVEST_INTENT;
			executeJob(action, jobInvestIntent);
		}
	}

	@Scheduled(cron = "${cron.depot.transferToDepotIntent}")
	public void scheduleTransferToDepotIntents() throws DamServiceException {
		if (taskConfiguration.isTransferToDepotIntentActive()) {
			ActionType action = ActionType.TRANSFER_TO_DEPOT_INTENT;
			executeJob(action, jobTransferToDepotIntent);
		}
	}

}
