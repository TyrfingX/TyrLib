package com.tyrlib2.graphics.text;

public class Font {
	public TextRenderer glText;
	public String name;
	public String source;
	
	public Font(String source, String name, TextRenderer glText) {
		this.source = source;
		this.name = name;
		this.glText = glText;
	}
}
