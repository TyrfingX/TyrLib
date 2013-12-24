package com.tyrlib2.graphics.text;

public class Font {
	public GLText glText;
	public String name;
	public String source;
	
	public Font(String source, String name, GLText glText) {
		this.source = source;
		this.name = name;
		this.glText = glText;
	}
}
