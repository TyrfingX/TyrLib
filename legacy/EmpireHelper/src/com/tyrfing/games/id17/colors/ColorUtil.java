package com.tyrfing.games.id17.colors;

import java.awt.Color;

public class ColorUtil {
	  public static Color lerp(Color c0, Color c1, double weight) {
	    double weight0 = weight;
	    double weight1 = 1-weight;

	    double r = weight0 * c0.getRed() + weight1 * c1.getRed();
	    double g = weight0 * c0.getGreen() + weight1 * c1.getGreen();
	    double b = weight0 * c0.getBlue() + weight1 * c1.getBlue();
	    double a = Math.max(c0.getAlpha(), c1.getAlpha());

	    return new Color((int) r, (int) g, (int) b, (int) a);
	  }
	}
