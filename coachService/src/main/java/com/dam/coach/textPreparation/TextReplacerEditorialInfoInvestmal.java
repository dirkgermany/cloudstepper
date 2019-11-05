package com.dam.coach.textPreparation;

import com.dam.coach.types.Category;

public class TextReplacerEditorialInfoInvestmal extends TextReplacerEditorialInfo{
	public String replace(String stringToReplace, String[] localVariables) {
		// find out matching replacer
		Category category = lookupCategory(stringToReplace);
		String path = lookupCategoryPath(stringToReplace);
		String []subVariables = lookupVariable(path);

		switch (category) {
		case BASICS	:
			return investmalInfoBasics(localVariables);
			
		case COACH:
			return coachInfo();
			
		case CREDITCARD:
			return investmalInfoCreditCard(localVariables);

		case GOAL:
			return goalInfo();

		case DEFAULT:
		default:
			return defaultReplacement();
		}
	}
	
	private String investmalInfoBasics(String[] localVariables) {		
		return Replacements.getReplacement("editorial.info.investmal.basics(" + localVariables[0].trim() + ")");
	}
	
	private String coachInfo() {
		return Replacements.getReplacement("editorial.info.investmal.coach");
	}
	
	private String goalInfo() {
		return Replacements.getReplacement("editorial.info.investmal.goal");
	}
	
	private String investmalInfoCreditCard(String[] localVariables) {
		Long index = Long.valueOf(localVariables[0].trim());
		
		return Replacements.getReplacement("editorial.info.investmal.creditCard(" + localVariables[0].trim() + ")");
	}

}
