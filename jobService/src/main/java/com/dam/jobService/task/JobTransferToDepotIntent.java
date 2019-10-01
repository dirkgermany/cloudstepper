package com.dam.jobService.task;

import org.springframework.stereotype.Component;

import com.dam.exception.DamServiceException;

/**
 * Get list of open InvestIntents from Service Provider.
 * Charge money from house bank.
 * Send Update Request per list entry to ServiceProvider.
 * @author dirk
 *
 */
@Component
public class JobTransferToDepotIntent extends Client {
	
	public JobTransferToDepotIntent() {		
	}


	@Override
	public void executeJob() throws DamServiceException {
		super.login(this.getClass().getName());

		try {
			Thread.sleep(10000);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

}
