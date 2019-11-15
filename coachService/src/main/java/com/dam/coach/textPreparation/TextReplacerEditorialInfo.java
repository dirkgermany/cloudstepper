package com.dam.coach.textPreparation;

import com.dam.coach.types.Category;

public class TextReplacerEditorialInfo extends TextReplacerEditorial {
	public String replace(String stringToReplace, String[] localVariables) {
		// find out matching replacer
		Category category = lookupCategory(stringToReplace);
		String path = lookupCategoryPath(stringToReplace);
		String []subVariables = lookupVariable(path);

		switch (category) {
		case PORTFOLIO:
			return new TextReplacerEditorialInfoPortfolio().replace(path, subVariables);
			
		case BOND:
			return new TextReplacerEditorialInfoLoan().replace(path, subVariables);
			
		case INVESTMAL:
			return new TextReplacerEditorialInfoInvestmal().replace(path, subVariables);
			
		case NEWS:
			return portfolioNews(localVariables);

		case DEFAULT:
		default:
			return defaultReplacement();
		}
	}

	private String portfolioNews(String[] localVariables) {
		if (null != localVariables && localVariables.length > 0 && null != localVariables[0] && !localVariables[0].isEmpty()) {
			Long portfolioId = Long.valueOf(localVariables[0]);
		}
		Long userId = this.userId;
		
		return Replacements.getReplacement("editorial.info.news");
	}

}
