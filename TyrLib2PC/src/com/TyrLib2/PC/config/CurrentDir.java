package com.TyrLib2.PC.config;

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
