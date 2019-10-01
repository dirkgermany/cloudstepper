package com.dam.jobService.task;

import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

import org.springframework.stereotype.Component;

import com.dam.exception.DamServiceException;
import com.dam.jobService.messageClass.Intent;
import com.dam.jobService.rest.consumer.ExternalApiConsumer;
import com.dam.jobService.rest.message.RestRequestIntent;
import com.dam.jobService.type.ActionType;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;

/**
 * Get list of open InvestIntents from Service Provider. Charge money from house
 * bank. Send Update Request per list entry to ServiceProvider.
 * 
 * @author dirk
 *
 */
@Component
public class JobInvestIntent extends Client {

	private final static String DOMAIN_DEPOT = "depot";
	private final static String PATH_LIST_INTENT_INVEST = "getListIntentInvest";
	private final static String PATH_INTENT_INVEST_CONFIRMED = "intentInvestConfirmed";
	private final static String PATH_INTENT_INVEST_DECLINED = "intentInvestDeclined";
	private final static String NODE_RESPONSE_INTENT_LIST = "intentList";

	public JobInvestIntent() {
	}

	@Override
	public void executeJob() throws DamServiceException {
		super.login(this.getClass().getName());

		// TODO wenn aktion nicht ausgeführt werden konnte (token abgelaufen) dann login
		// forcieren

		Iterator<Intent> it = getIntentList().iterator();
		while (it.hasNext()) {
			Intent intent = it.next();
			if (ExternalApiConsumer.debit(intent.getUserId(), intent.getAmount())) {
				// house bank accepted
				confirmIntent(intent);
			}
			else {
				declineIntent(intent);
			}
		}
	}
	
	private void declineIntent(Intent intent) throws DamServiceException {
		intent.setAction(ActionType.INVEST_INTENT_DECLINED);
		intent.setAccepted(false);
		intent.setBookingDate(new Date());
		
		RestRequestIntent restRequest = new RestRequestIntent(intent);
		JsonNode node = jsonHelper.getObjectMapper().valueToTree(restRequest);
		sendRequest(node, DOMAIN_DEPOT + "/" + PATH_INTENT_INVEST_DECLINED);
	}

	/*
	 * Call Service Provider for updating the status of the dependent entities.
	 */
	private void confirmIntent(Intent intent) throws DamServiceException {
		intent.setAction(ActionType.INVEST_INTENT_CONFIRMED);
		intent.setAccepted(true);
		intent.setBookingDate(new Date());

		RestRequestIntent restRequest = new RestRequestIntent(intent);

		JsonNode node = jsonHelper.getObjectMapper().valueToTree(restRequest);
		sendRequest(node, DOMAIN_DEPOT + "/" + PATH_INTENT_INVEST_CONFIRMED);
	}

	/*
	 * Requests the Service Provider for a List with Invest Intents.
	 */
	private List<Intent> getIntentList() throws DamServiceException {
		Intent searchIntent = new Intent();
		searchIntent.setAction(ActionType.INVEST_INTENT);
		searchIntent.setBooked(false);

		RestRequestIntent restRequest = new RestRequestIntent(searchIntent);

		JsonNode node = jsonHelper.getObjectMapper().valueToTree(restRequest);
		JsonNode response = sendRequest(node, DOMAIN_DEPOT + "/" + PATH_LIST_INTENT_INVEST);

		JsonNode jsonIntentList = jsonHelper.extractNodeFromNode(response, NODE_RESPONSE_INTENT_LIST);

		List<Intent> intentList = new ArrayList<>();
		if (null != jsonIntentList) {
			try {
				if (jsonIntentList.isArray()) {
					for (JsonNode arrayItem : jsonIntentList) {
						intentList.add(jsonHelper.getObjectMapper().treeToValue(arrayItem, Intent.class));
					}
				} else if (jsonIntentList.isObject()) {
					intentList.add(jsonHelper.getObjectMapper().treeToValue(jsonIntentList, Intent.class));
				}
			} catch (JsonProcessingException e) {
				throw new DamServiceException(500L, "JobInvestIntent :: Fehler bei Bearbeitung der Response",
						e.getMessage());
			}
		}
		return intentList;
	}

}
