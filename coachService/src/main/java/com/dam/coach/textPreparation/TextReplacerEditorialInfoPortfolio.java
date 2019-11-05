package com.dam.coach.textPreparation;

import com.dam.coach.types.Category;

public class TextReplacerEditorialInfoPortfolio extends TextReplacerEditorialInfo {
	public String replace(String stringToReplace, String[] localVariables) {
		
		// find out matching replacer
		Category category = lookupCategory(stringToReplace);
		String path = lookupCategoryPath(stringToReplace);
		String []subVariables = lookupVariable(path);

		switch (category) {
		case BASICS:
			return portfolioInfoBasics();
			
		case STATISTICS:
			return portfolioInfoStatistics(localVariables);

		case DEFAULT:
		default:
			return defaultReplacement();
		}
	}
	
	private String portfolioInfoStatistics(String[] localVariables) {
		Long index = Long.getLong(localVariables[0].trim());
		
		return Replacements.getReplacement("editorial.info.portfolio.statistics(" + localVariables[0].trim() + ")");
	}
	
	private String portfolioInfoBasics() {
		Long userId = this.userId;
		
		return Replacements.getReplacement("editorial.info.portfolio.basics");
	}
	

}
