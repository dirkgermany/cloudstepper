package com.dam.jobService;

import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import com.dam.jobService.types.TaskType;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Configuration
@EnableScheduling
@Component
public class Tasks {
	private static final Logger logger = LoggerFactory.getLogger(Tasks.class);
	private static final DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("HH:mm:ss");

	@Scheduled(cron = "${cron.depot.investIntent}")
	public void scheduleInvestIntents() {
		TaskType preType = new TaskSynchronizer().getPredessorForTask(TaskType.DEPOT_DEBIT_INTENT);
		System.out.println("---------------------" + preType);
		
	    logger.info("scheduleInvestIntents :: Execution Time - {}", dateTimeFormatter.format(LocalDateTime.now()) );
	}

	@Scheduled(cron = "${cron.depot.investIntent}")
	public void scheduleInvestIntents2() {
	    logger.info("scheduleInvestIntents2 :: Execution Time - {}", dateTimeFormatter.format(LocalDateTime.now()) );
	}

	@Scheduled(cron = "${cron.depot.investIntent}")
	public void scheduleInvestIntents3() {
	    logger.info("scheduleInvestIntents3 :: Execution Time - {}", dateTimeFormatter.format(LocalDateTime.now()) );
	}

}
