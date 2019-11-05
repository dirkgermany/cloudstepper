package com.dam.coach.textPreparation;

import com.dam.coach.types.Category;

public class TextReplacerDepotStatisticsLoan extends TextReplacerDepotStatistics{
	public String replace(String stringToReplace, String[] localVariables) {
		// find out matching replacer
		Category category = lookupCategory(stringToReplace);
		String path = lookupCategoryPath(stringToReplace);
		String []subVariables = lookupVariable(path);

		switch (category) {
		case INVESTPERCENTAGE:
			return returnLoanInvestPercentage();
			
		case DEFAULT:
			default:
				return defaultReplacement();
		}
	}
	
	private String returnLoanInvestPercentage() {
		return Replacements.getReplacement("depot.loan.investPercentage");
	}

}
