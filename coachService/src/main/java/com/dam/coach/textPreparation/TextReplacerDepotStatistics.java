package com.dam.coach.textPreparation;

import com.dam.coach.types.Category;

public class TextReplacerDepotStatistics extends TextReplacerDepot {
	public String replace(String stringToReplace, String[] localVariables) {
		// find out matching replacer
		Category category = lookupCategory(stringToReplace);
		String path = lookupCategoryPath(stringToReplace);
		String []subVariables = lookupVariable(path);

		switch (category) {
		case ACCOUNT:
			return new TextReplacerDepotStatisticsAccount().replace(path, subVariables);

		case BOND:
			return new TextReplacerDepotStatisticsLoan().replace(path, subVariables);
			
		case DEPOT:
			return new TextReplacerDepotStatisticsDepot().replace(path, subVariables);


		case DEFAULT:
		default:
			return defaultReplacement();
		}
	}
}
