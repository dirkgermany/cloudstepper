package com.dam.coach.textPreparation;

import com.dam.coach.types.Category;

public class TextReplacerPortfolioStatistics extends TextReplacerPortfolio{
	public String replace(String stringToReplace, String[] localVariables) {
		// find out matching replacer
		Category category = lookupCategory(stringToReplace);
		String path = lookupCategoryPath(stringToReplace);
		String []subVariables = lookupVariable(path);

		switch (category) {
		case PERFORMANCE:
			return portfolioPerformancePercentage();
		
		case INCREASE:
			return portfolioIncrease(localVariables);
			
		case DEFAULT:
		default:
			return defaultReplacement();
		}
	}
	
	private String portfolioPerformancePercentage() {
		return "4.77";
	}
	
	private String firstInvestDate() {
		return "01.03.2018";
	}
	
	private String portfolioIncrease(String[] localVariables) {
		// Month is part of the variable
		// basic.cal.lastMonthName
		TextReplacerImpl textReplacer = new TextReplacerImpl();
		String Month = textReplacer.replace("{" + localVariables[0] + "}");
		
		return "3.47";
	}
}