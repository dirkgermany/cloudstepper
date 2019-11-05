package com.dam.coach.textPreparation;

import com.dam.coach.types.Category;

public class TextReplacerEditorialInfoLoan extends TextReplacerEditorialInfo {
	public String replace(String stringToReplace, String[] localVariables) {
		
		// find out matching replacer
		Category category = lookupCategory(stringToReplace);
		String path = lookupCategoryPath(stringToReplace);
		String []subVariables = lookupVariable(path);

		switch (category) {
		case BASICS:
			return loanInfoBasics();
			
		case DEFAULT:
		default:
			return defaultReplacement();
		}
	}
		
	private String loanInfoBasics() {
		Long userId = this.userId;
		
		return Replacements.getReplacement("editorial.info.loan.basics");
	}
}
