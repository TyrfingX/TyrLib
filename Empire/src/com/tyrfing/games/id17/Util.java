package com.tyrfing.games.id17;

public class Util {
	
	public static final String GREEN_TEXT = "<#50ff80>";
	public static final String RED_TEXT = "<#ffb550>";
	
	public static String getFlaggedText(String text, boolean flag) {
		if (flag) {
			return GREEN_TEXT + text + "\\#";
		} else {
			return RED_TEXT + text + "\\#";
		}
	}
	
	public static String getSignedText(float value) {
		String sign = "+";
		if (value < 0) {
			sign = "";
		}
		
		return sign + value;
	}
	
	public static String getSignedText(int value) {
		String sign = "+";
		if (value < 0) {
			sign = "";
		}
		
		return sign + value;
	}
	
	public static String getRankedText(int number) {
		if (number % 10 == 1) {
			return number + "st";
		} else if (number % 10 == 2) {
			return number + "nd";
		} else if (number % 10 == 3) {
			return number + "rd";
		} else {
			return number + "th";
		}
	}

	public static String getFlaggedText(int relationHit, boolean flag) {
		return getFlaggedText(String.valueOf(relationHit), flag);
	}
}
