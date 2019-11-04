package com.dam.coach.textPreparation;

import com.dam.coach.types.Category;

public class TextReplacerUser extends TextReplacerImpl{
	public String replace() {
		// find out matching replacer
		Category category = lookupCategory();

		switch (category) {
		case FIRSTNAME:
			return getUserFirstName();
						
		default:
			break;
		}

		return null;
	}

	private String getUserFirstName() {
		return "Udo";
	}
}
