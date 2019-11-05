package com.dam.coach.textPreparation;

import com.dam.coach.types.Category;

public class TextReplacerPortfolio extends TextReplacerImpl {

	public String replace(String stringToReplace, String[] localVariables) {
		// find out matching replacer
		Category category = lookupCategory(stringToReplace);
		String path = lookupCategoryPath(stringToReplace);
		String []subVariables = lookupVariable(path);

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
}
