package com.dam.coach.textPreparation;

import com.dam.coach.types.Category;

public class TextReplacerDepot extends TextReplacerImpl {

	public String replace(String stringToReplace, String[] localVariables) {
		// find out matching replacer
		Category category = lookupCategory(stringToReplace);
		String path = lookupCategoryPath(stringToReplace);
		String []subVariables = lookupVariable(path);

		switch (category) {
		case STATISTICS:
			return new TextReplacerDepotStatistics().replace(path, subVariables);

		case DEFAULT:
		default:
			return stringToReplace;
		}
	}
}
