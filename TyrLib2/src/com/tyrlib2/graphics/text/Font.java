package com.tyrlib2.graphics.text;

public class Font {
	public IGLText glText;
	public String name;
	public String source;
	
	public Font(String source, String name, IGLText glText) {
		this.source = source;
		this.name = name;
		this.glText = glText;
	}
}
