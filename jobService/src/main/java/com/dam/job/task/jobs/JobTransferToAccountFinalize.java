package com.dam.job.task.jobs;

import java.util.Iterator;

import org.springframework.stereotype.Component;

import com.dam.exception.DamServiceException;
import com.dam.job.messageClass.Intent;
import com.dam.job.rest.consumer.ExternalApiConsumer;
import com.dam.job.type.ActionType;

/**
 * Get list of open InvestIntents from Service Provider.
 * Charge money from house bank.
 * Send Update Request per list entry to ServiceProvider.
 * @author dirk
 *
 */
@Component
public class JobTransferToAccountFinalize extends DepotClient {
	
	private final static String PATH_INTENT_TRANSFER_TO_ACCOUNT_CONFIRMED = "transferToAccountConfirmed";
	private final static String PATH_INTENT_TRANSFER_TO_ACCOUNT_DECLINED = "transferToAccountDeclined";
	
	public JobTransferToAccountFinalize() {		
	}


	@Override
	public void executeJob() throws DamServiceException {
		super.login(this.getClass().getName());

		// TODO wenn aktion nicht ausgeführt werden konnte (token abgelaufen) dann login
		// forcieren

		Iterator<Intent> it = getIntentList(ActionType.TRANSFER_TO_ACCOUNT_INTENT, DOMAIN_DEPOT, PATH_LIST_INTENT, NODE_RESPONSE_INTENT_LIST).iterator();
		while (it.hasNext()) {
			Intent intent = it.next();
			
			if (ExternalApiConsumer.stockSellAssets(intent.getUserId(), intent.getAmount(), intent.getPortfolioId())) {
				// house bank accepted
				confirmIntent(intent, ActionType.TRANSFER_TO_ACCOUNT_CONFIRMED, DOMAIN_DEPOT, PATH_INTENT_TRANSFER_TO_ACCOUNT_CONFIRMED);
			}
			else {
				declineIntent(intent, ActionType.TRANSFER_TO_ACCOUNT_DECLINED, DOMAIN_DEPOT, PATH_INTENT_TRANSFER_TO_ACCOUNT_DECLINED);
			}
		}
	}
	

	

}
