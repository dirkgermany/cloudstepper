package com.dam.coach.textPreparation;

import com.dam.coach.types.Category;

public class TextReplacerDepot extends TextReplacerImpl {

	public String replace() {
		// find out matching replacer
		Category category = lookupCategory();

		switch (category) {
		case RETURN_OF_INVEST:
			return returnOfInvest();

		case DEFAULT:
		default:
			
			break;
		}

		return null;
	}
	
	private String returnOfInvest() {
		return "2.077";
	}

}
