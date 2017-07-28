package com.tyrfing.games.tyrlib3.util;

import java.util.HashMap;
import java.util.Map;

public class Options {
	
	private Map<Integer, Object> options = new HashMap<Integer, Object>();
	
	public void setOption(Integer option, Object value) {
		options.put(option, value);
	}
	
	public Object getSetting(Integer option) {
		return options.get(option);
	}
}
