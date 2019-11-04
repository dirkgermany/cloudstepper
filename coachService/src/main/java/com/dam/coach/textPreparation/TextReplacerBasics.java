package com.dam.coach.textPreparation;

import com.dam.coach.types.Category;

public class TextReplacerBasics extends TextReplacerImpl{
	
	public String replace() {
		// find out matching replacer
		Category category = lookupCategory();
		
		switch (category) {
		case CAL:
			return new TextReplacerBasicsCal().replace();
			
		default:
			break;
		}

		return null;
	}

}
