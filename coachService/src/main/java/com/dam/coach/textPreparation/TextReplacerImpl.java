package com.dam.coach.textPreparation;

import com.dam.coach.types.Category;

public class TextReplacerImpl implements TextReplacer {
//	protected String alterablePlaceholder;
//	protected String categoryPlaceholder;
//	protected String[] variable;
	protected Long userId;

	private String seperatePlaceholder(String textForPreparation, String replacedText) {
		if (textForPreparation.contains("{")) {
			int posStart = textForPreparation.indexOf("{");
			if (posStart > 0) {
				replacedText = replacedText + textForPreparation.substring(0, posStart);
			}

			int posEnd = textForPreparation.indexOf("}");
			String dummyText = partialReplace(textForPreparation.substring(posStart + 1, posEnd));
			replacedText = replacedText + dummyText;
			textForPreparation = textForPreparation.substring(posEnd + 1);
			replacedText = seperatePlaceholder(textForPreparation, replacedText);
		} else {
			replacedText += textForPreparation;
		}
		return replacedText;
	}

	private String partialReplace(String stringToReplace) {

		// find out matching replacer
		Category category = lookupCategory(stringToReplace);
		String path = lookupCategoryPath(stringToReplace);
		String []subVariables = lookupVariable(path);

		switch (category) {
		case BASICS:
			return new TextReplacerBasics().replace(path, subVariables);

		case EDITORIAL:
			return new TextReplacerEditorial().replace(path, subVariables);

		case PORTFOLIO:
			return new TextReplacerPortfolio().replace(path, subVariables);

		case DEPOT:
			return new TextReplacerDepot().replace(path, subVariables);

		case USER:
			return new TextReplacerUser().replace(path, subVariables);

		case DEFAULT:
		default:
			return stringToReplace;
		}

	}

	public String replace(String stringToReplace) {
		// Variable may contain dots also (bla.blub.bla)
		// So variable must seperated at first from placeholder
		return seperatePlaceholder(stringToReplace, "");
	}

	protected String[] lookupVariable(String stringToReplace) {
		if (stringToReplace.contains("(")) {
			// (v1, v2, ...)
			String placeholderParts[] = stringToReplace.split("\\(");
			stringToReplace = placeholderParts[0];
			String variableString = placeholderParts[1];
			variableString = variableString.replace("(", "");
			variableString = variableString.replace(")", "");
			if (variableString.contains(",")) {
				return variableString.split(",");
			} else {
				String variables[] = new String[1];
				variables[0] = variableString.trim();
				return variables;
			}
		}
		return new String[0];
	}

	protected String lookupCategoryPath(String categoryPath) {
		if (categoryPath.contains(".")) {
			int pos = categoryPath.indexOf(".");
			if (pos + 1 > categoryPath.length()) {
				return "";
			}
			return categoryPath.substring(pos + 1);
		} else {
			return "";
		}
	}

	protected Category lookupCategory(String categoryPath) {
		// if there are variables () remove here
		if (categoryPath.contains("(")) {
			int pos = categoryPath.indexOf("(");
			categoryPath = categoryPath.substring(0, pos);
		}
		
		Category category = Category.DEFAULT;
		if (categoryPath.contains(".")) {
			String categoryString[] = categoryPath.split("\\.");
			try {
				category = Category.valueOf(categoryString[0].toUpperCase());
			} catch (Exception ex) {
				return category;
			}
		} else {
			try {
				category = Category.valueOf(categoryPath.toUpperCase());
			} catch (Exception ex) {
				return category;
			}
		}
		return category;
	}
}
