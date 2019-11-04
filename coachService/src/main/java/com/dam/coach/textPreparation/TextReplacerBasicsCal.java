package com.dam.coach.textPreparation;

import java.time.LocalDateTime;
import java.time.format.TextStyle;
import java.util.Locale;

import com.dam.coach.types.Category;

public class TextReplacerBasicsCal extends TextReplacerBasics {
	
	public String replace() {
		// find out matching replacer
		Category category = lookupCategory();

		switch (category) {
		case GREETING:
			return calculateGreeting();
			
		case LAST_MONTHNAME:
			return calculateLastMonthName();
			
		default:
			break;
		}

		return null;
	}
	
	private String calculateLastMonthName () {
		LocalDateTime localDateTime = LocalDateTime.now().minusMonths(1);
		return localDateTime.getMonth().getDisplayName(TextStyle.FULL, Locale.GERMAN);
	}
	
	private String calculateGreeting () {
		// calc minutes of day (e.g. 12:30am = 750)
		LocalDateTime localDateTime = LocalDateTime.now();
		int hourNow = localDateTime.getHour();
		int minuteNow = localDateTime.getMinute();
		int minutesOfDay = (hourNow * 60) + minuteNow;
		
		if (minutesOfDay <= 690) {
			return "Guten Morgen";
		}
		if (minutesOfDay >= 1080) {
			return "Guten Abend";
		}

		return "Hallo";
	}

}
