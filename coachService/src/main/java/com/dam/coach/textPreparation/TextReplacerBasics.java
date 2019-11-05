package com.dam.coach.textPreparation;

import com.dam.coach.types.Category;

public class TextReplacerBasics extends TextReplacerImpl {

	public String replace(String stringToReplace, String[] localVariables) {
		// find out matching replacer

		Category category = lookupCategory(stringToReplace);
		String catPath = lookupCategoryPath(stringToReplace);
		String []subVariables = lookupVariable(catPath);

		switch (category) {
		case CAL:
			return new TextReplacerBasicsCal().replace(catPath, subVariables);

		case DEFAULT:
		default:
			return "";
		}
	}
}
