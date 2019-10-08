package com.dam.stock.task;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import com.dam.exception.DamServiceException;
import com.dam.stock.Configuration;
import com.dam.stock.task.jobs.JobSellIntentFinalize;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@EnableScheduling
@Component
public class ScheduledTasks {

	@Autowired
	Configuration configuration;

	@Autowired
	TaskController taskController;
	
	@Autowired
	JobSellIntentFinalize jobSellIntentFinalize;

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
	private boolean executeJob(Client client) throws DamServiceException {
		logger.info("Stock Importer :: {}: - Status: {}", dateTimeFormatter.format(LocalDateTime.now()), "started");
		
		boolean success = true;

		try {
			client.executeJob();
		} catch (Exception ex) {
			logger.error("Scheduled Task", ex);
			client.logout(this.getClass().getName());
			success = false;
		} finally {
			logger.info("Stock Importer :: {}: - Status: {}", dateTimeFormatter.format(LocalDateTime.now()), "finished");

		}
		return success;
	}
	
	/*
	 * Starts a job and makes retries if it was not successfull.
	 * Two errors are most presumably:
	 * - another job/task for the same user is still in progress
	 * - the token has expired
	 */
	private void startJob(Client client) throws DamServiceException {
		boolean success = false;
		int retries = 0;
		while (!success && retries <= MAX_RETRIES) {
			success = executeJob(client);
			if (!success) {
				try {
					Thread.sleep(10000);
				} catch (InterruptedException e) {
				}
			}
		}
	}

	@Scheduled(cron = "${cron.depot.sellIntentFinalize}")
	public void scheduleSellIntentFinalize() throws DamServiceException {
		if (configuration.isImportStockActive()) {
			startJob(jobSellIntentFinalize);
		}
	}
}
