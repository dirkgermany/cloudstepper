package com.dam.coach.textPreparation;

import java.util.ArrayList;
import java.util.List;

import com.dam.coach.model.entity.CoachAction;
import com.dam.coach.rest.message.coachAction.CoachActionRequest;
import com.dam.coach.types.Category;
import com.dam.exception.DamServiceException;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;

public class TextReplacerPortfolio extends TextReplacerImpl {

	public String replace(String stringToReplace, String[] localVariables) {
		// find out matching replacer
		Category category = lookupCategory(stringToReplace);
		String path = lookupCategoryPath(stringToReplace);
		String[] subVariables = lookupVariable(path);

		switch (category) {
		case STATISTICS:
			return new TextReplacerPortfolioStatistics().replace(path, subVariables);

		case PERFORMANCE:
			return new TextReplacerPortfolioPerformance().replace(path, subVariables);

		case DEFAULT:
		default:
			return defaultReplacement();
		}
	}

	private void test() throws DamServiceException {
		String uri = "STOCK" + "/" + "getStockHistory";
		JsonNode node = readJsonObject(new CoachActionRequest(null, null, null), uri);
		
		
		JsonNode jsonHistoryList = jsonHelper.extractNodeFromNode(node, "stockHistoryList");
		List<CoachAction> historyList = new ArrayList<>();
		if (null != jsonHistoryList) {
			try {
				if (jsonHistoryList.isArray()) {
					for (JsonNode arrayItem : jsonHistoryList) {
						historyList.add(jsonHelper.getObjectMapper().treeToValue(arrayItem, CoachAction.class));
					}
				} else if (jsonHistoryList.isObject()) {
					historyList.add(jsonHelper.getObjectMapper().treeToValue(jsonHistoryList, CoachAction.class));
				}
			} catch (JsonProcessingException e) {
				throw new DamServiceException(500L, "Portfolio :: Fehler bei Bearbeitung der Response", e.getMessage());
			}
		}

	}
}
