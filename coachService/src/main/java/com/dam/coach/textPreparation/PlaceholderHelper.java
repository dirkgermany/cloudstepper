package com.dam.coach.textPreparation;

public class PlaceholderHelper {
	
	public static String seperatePlaceholder (String textForPreparation, String replacedText) {
		if (textForPreparation.contains("{")) {
			int posStart = textForPreparation.indexOf("{");
			if (posStart > 0) {
				replacedText = replacedText + textForPreparation.substring(0, posStart);
			}
			
			int posEnd = textForPreparation.indexOf("}"); 
			String dummyText = dummyReplacer(textForPreparation.substring(posStart, posEnd-1));
			replacedText = replacedText + dummyText;
			textForPreparation = textForPreparation.substring(posEnd +1);
			replacedText = seperatePlaceholder(textForPreparation, replacedText);
		}
		else {
			replacedText+=textForPreparation;
		}
		return replacedText;
	}
	
	private static String dummyReplacer (String bla) {
		return "XXX";
	}

}
