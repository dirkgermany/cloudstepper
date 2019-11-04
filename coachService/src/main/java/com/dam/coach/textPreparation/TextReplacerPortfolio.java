package com.dam.coach.textPreparation;

import com.dam.coach.types.Category;

public class TextReplacerPortfolio extends TextReplacerImpl {

	public String replace() {
		// find out matching replacer
		Category category = lookupCategory();

		switch (category) {
		case PERFORMANCE:
			return new TextReplacerPortfolioPerformance().replace();
						
		default:
			break;
		}

		return null;
	}
}
