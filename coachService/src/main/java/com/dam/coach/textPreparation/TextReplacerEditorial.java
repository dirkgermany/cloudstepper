package com.dam.coach.textPreparation;

import com.dam.coach.types.Category;

public class TextReplacerEditorial extends TextReplacerImpl {

	public String replace(String stringToReplace, String[] localVariables) {
		// find out matching replacer
		Category category = lookupCategory(stringToReplace);
		String path = lookupCategoryPath(stringToReplace);
		String []subVariables = lookupVariable(path);

		switch (category) {
		case INFO:
			return new TextReplacerEditorialInfo().replace(path, subVariables);
			
		case BOND:

		case DEFAULT:
		default:
			return defaultReplacement();
		}
	}

}
