package com.dam.coach;

import java.text.SimpleDateFormat;
import java.util.Date;

public class TextReplacer {
	
	private static final String WILD_CARD_HELLO = "{hello}";
	private static final String WILD_CARD_RETURN = "{returnDate}";
	private static final String WILD_CARD_CLOSE = "{closePhrase}";
	
	public static String dayTime (String textToReplace) {
		
		// calc minutes of day (e.g. 12:30am = 750)
		String timeString[] = new SimpleDateFormat("HH:mm").format(new Date()).split(":");
		Long hourNow = Long.getLong(timeString[0]);
		Long minuteNow = Long.getLong(timeString[1]);
		Long minutesOfDay = (hourNow * 60) + minuteNow;
		
		// Hello = Guten Morgen, Guten Tag, Guten Abend
		String replacementHello = "Guten Tag";
		String replacementReturn = "in 2 Stunden";
		String replacementClose = "Tschüss, bis später";
		
		if (minutesOfDay <= 690) {
			replacementHello = "Guten Morgen";
		}
		if (minutesOfDay >= 1080) {
			replacementHello = "Guten Abend";
			replacementReturn = "morgen";
			replacementClose = "Bis morgen, einen schönen Abend";
		}
		
		textToReplace.replace(WILD_CARD_HELLO, replacementHello);
		textToReplace.replace(WILD_CARD_RETURN, replacementReturn);
		textToReplace.replace(WILD_CARD_CLOSE, replacementClose);
		
		return textToReplace;
	}
}
