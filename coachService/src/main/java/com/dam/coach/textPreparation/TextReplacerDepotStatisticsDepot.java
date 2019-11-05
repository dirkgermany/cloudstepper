package com.dam.coach.textPreparation;

import com.dam.coach.types.Category;

public class TextReplacerDepotStatisticsDepot extends TextReplacerDepotStatistics{
	public String replace(String stringToReplace, String[] localVariables) {
		// find out matching replacer
		Category category = lookupCategory(stringToReplace);
		String path = lookupCategoryPath(stringToReplace);
		String []subVariables = lookupVariable(path);

		switch (category) {
		case FIRSTINVESTDATE:
			return firstInvestDate();
			
		case RETURNOFINVEST:
			return returnOfInvest();

		case DEFAULT:
		default:	
			return stringToReplace;
		}
	}

	private String firstInvestDate() {
		return "01.03.2018";
	}
	
	private String returnOfInvest() {
		return "2.467,54";
	}
}
