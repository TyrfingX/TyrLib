package com.tyrfing.games.tyrlib3.util;

import java.util.ArrayList;
import java.util.List;

public class StringUtil {
	
	public static final String GREEN = "<#aaffaa>";
	public static final String RED = "<#ffaaaa>";
	public static final String CODE_END = "\\#";
	
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
			return GREEN + text + CODE_END;
		} else {
			return RED + text + CODE_END;
		}
	}
}
