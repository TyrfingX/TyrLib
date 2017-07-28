package com.tyrlib2.util;

import java.util.ArrayList;
import java.util.List;

public class StringUtil {
	public static String[] splitSpaces(String line) {
		if (line == null) 
			return null;
		List<String> result = new ArrayList<String>();
		int firstPos = 0;
		while (firstPos < line.length()) {
			int lastPos = line.indexOf(' ', firstPos);
			if (lastPos == -1)
				lastPos = line.length();
			if (lastPos > firstPos) {
				result.add(line.substring(firstPos, lastPos));
			}
			firstPos = lastPos+1; 
		}
		return (String[]) result.toArray(new String[result.size()]);
	}
	
	
	public static String getFlaggedText(String text, boolean flag) {
		if (flag) {
			return "<#aaffaa>" + text + "\\#";
		} else {
			return "<#ffaaaa>" + text + "\\#";
		}
	}
	
	public static String getFlaggedText(String text, int val1, int val2) {
		if (val1 > val2) {
			return "<#aaffaa>" + text + "\\#";
		} else if (val1 < val2){
			return "<#ffaaaa>" + text + "\\#";
		} else {
			return text;
		}
	}
	
	public static String getFlaggedText(String text, float val1, float val2) {
		if (val1 > val2) {
			return "<#aaffaa>" + text + "\\#";
		} else if (val1 < val2){
			return "<#ffaaaa>" + text + "\\#";
		} else {
			return text;
		}
	}
}
