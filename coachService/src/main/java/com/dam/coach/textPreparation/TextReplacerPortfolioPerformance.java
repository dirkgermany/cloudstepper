package com.dam.coach.textPreparation;

import com.dam.coach.types.Category;

public class TextReplacerPortfolioPerformance extends TextReplacerPortfolio{

	public String replace() {
		// find out matching replacer
		Category category = lookupCategory();

		switch (category) {
		case FIRSTINVESTDATE:
			return firstInvestDate();
			
		case INCREASE:
			return portfolioIncrease();
			
		default:
			break;
		}

		return null;
	}
	
	private String firstInvestDate() {
		return "01.03.2018";
	}
	
	private String portfolioIncrease() {
		// Month is part of the variable
		// basic.cal.lastMonthName
		TextReplacerImpl textReplacer = new TextReplacerImpl();
		textReplacer.setPlaceholder(variable[0]);
		String Month = textReplacer.replace();
		
		return "3.47";
	}
}
