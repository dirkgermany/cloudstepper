package com.dam.coach.textPreparation;

import com.dam.coach.types.Category;
import com.fasterxml.jackson.databind.deser.std.StringArrayDeserializer;

public class TextReplacerImpl implements TextReplacer {
	protected String alterablePlaceholder;
	protected String[] variable;
	
	public void setPlaceholder(String placeholder) {
		this.alterablePlaceholder = placeholder;
	}

	public String replace() {
		// remove braces
		alterablePlaceholder = alterablePlaceholder.replaceAll("{", "");
		alterablePlaceholder = alterablePlaceholder.replaceAll("}", "");

		// Variable may contain dots also (bla.blub.bla)
		// So variable must seperated at first from placeholder
		lookupVariable();
		
		// find out matching replacer
		Category category = lookupCategory();

		switch (category) {
		case BASICS:
			return new TextReplacerBasics().replace();

		case EDITORIAL:
			break;

		case PORTFOLIO:
			return new TextReplacerPortfolio().replace();

		case DEPOT:
			return new TextReplacerDepot().replace();
			
		case USER:
			return new TextReplacerUser().replace();

		default:
			break;
		}

		return null;
	}

	private void lookupVariable() {	
		if (alterablePlaceholder.contains("(")) {
			// (v1, v2, ...)
			String placeholderParts[] = alterablePlaceholder.split("(");
			alterablePlaceholder = placeholderParts[0];
			String variableString = placeholderParts[1];
			variableString = variableString.replace("(", "");
			variableString = variableString.replace(")", "");
			if (variableString.contains(",")) {
				variable = variableString.split(",");
			}
			
			variable = new String[1];
			variable[0] = variableString;
		}
	
	}
	
	protected Category lookupCategory() {
		Category category = Category.DEFAULT;
		if (alterablePlaceholder.contains(".")) {
			String categoryString[] = alterablePlaceholder.split(".");
			category = Category.valueOf(categoryString[0].toUpperCase());
			alterablePlaceholder = categoryString[1];
		} else {
			category = Category.valueOf(alterablePlaceholder);
		}
		return category;
	}
}
