package com.dam.stock.rest.consumer;

import java.time.format.DateTimeFormatter;
import java.util.Iterator;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import com.dam.exception.DamServiceException;
import com.dam.stock.Configuration;
import com.dam.stock.client.Client;
import com.dam.stock.model.entity.AssetClass;

@EnableScheduling
@Component
public class ScheduledHistoryUpdater {
	
	@Autowired
	private Configuration configuration;
	
	@Autowired
	private Client client;

	private static final Logger logger = LoggerFactory.getLogger(ScheduledHistoryUpdater.class);
	private static final DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("HH:mm:ss");
	
	private static boolean isActive = false;

	public ScheduledHistoryUpdater() {
	}
	
	
	@Scheduled(cron = "${cron.history.update}")
	public void updateHistoryScheduled() throws DamServiceException {
		if (isActive) {
			return;
		}
		isActive = true;
		
		updateHistory();
		
		isActive = false;
	}

	private void updateHistory() throws DamServiceException {
		
		// 1. Hole Liste der Assets
		List<AssetClass> assetClasses = client.getAssetClassList();
		
		// 2. Hole Stockdaten
		Iterator<AssetClass> it = assetClasses.iterator();
		while (it.hasNext()) {
			AssetClass assetClass = it.next();
			
			
		}
		
	}

}
