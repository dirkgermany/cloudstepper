package com.dam.jobService.task.depot;

import org.springframework.stereotype.Component;

import com.dam.exception.DamServiceException;
import com.dam.jobService.task.Client;

/**
 * Get list of open InvestIntents from Service Provider.
 * Charge money from house bank.
 * Send Update Request per list entry to ServiceProvider.
 * @author dirk
 *
 */
@Component
public class JobInvestIntent extends Client {
	
	public JobInvestIntent() {		
	}


	@Override
	public void executeJob() throws DamServiceException {
		super.login(this.getClass().getName());
		
		//TODO wenn aktion nicht ausgeführt werden konnte (token abgelaufen) dann login forcieren

		try {
			Thread.sleep(10000);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

}
