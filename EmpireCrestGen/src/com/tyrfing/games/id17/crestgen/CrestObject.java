package com.tyrfing.games.id17.crestgen;

import java.awt.Color;
import java.awt.image.BufferedImage;

public class CrestObject {
	private static int ID;
	
	public final BufferedImage image;
	public final Color color;
	public final String hash = "" + (ID++) + ";";
	
	public CrestObject(BufferedImage image, Color color) {
		this.image = image;
		this.color = color;
	}
	
	public CrestObject(BufferedImage image) {
		this.image = image;
		this.color = null;
	}
}
