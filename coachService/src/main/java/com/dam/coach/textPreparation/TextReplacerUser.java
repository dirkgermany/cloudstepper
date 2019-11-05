package com.dam.coach.textPreparation;

import com.dam.coach.types.Category;

public class TextReplacerUser extends TextReplacerImpl{
	public String replace(String stringToReplace, String[] localVariables) {
		// find out matching replacer
		Category category = lookupCategory(stringToReplace);
		String path = lookupCategoryPath(stringToReplace);
		String []subVariables = lookupVariable(path);

		switch (category) {
		case FIRSTNAME:
			return getUserFirstName();
				
		case DEFAULT:
		default:
			return stringToReplace;
		}
	}

	private String getUserFirstName() {
		return "Udo";
	}
}
