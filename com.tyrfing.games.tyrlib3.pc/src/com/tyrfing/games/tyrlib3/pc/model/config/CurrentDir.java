package com.tyrfing.games.tyrlib3.pc.model.config;

import java.net.URL;

public class CurrentDir {
	
	private URL url;
	
	public CurrentDir() {
		url = getClass().getResource("/");
	}
	
	public String getCurrentDir() {
		return url.getPath();
	}
	
}
