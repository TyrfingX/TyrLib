package com.tyrlib2.gui;

import com.tyrlib2.util.Color;

/**
 * Defines the looknfeel for the GUI elements upon creation
 * @author Sascha
 *
 */

public class Skin {
	public String TEXTURE_ATLAS = "GUI";
	
	public Color LABEL_TEXT_COLOR = Color.WHITE;
	public Color LABEL_BG_COLOR = Color.TRANSPARENT;
	
	public String LABEL_FONT = "Roboto-Regular.ttf";
	public String BUTTON_FONT = "Roboto-Regular.ttf";
	
	public String BUTTON_NORMAL_LEFT = "BUTTON_NORMAL_LEFT";
	public String BUTTON_NORMAL_MIDDLE = "BUTTON_NORMAL_MIDDLE";
	public String BUTTON_NORMAL_RIGHT = "BUTTON_NORMAL_RIGHT";
	public float  BUTTON_LEFT_SIZE = 0.02f;
	public float  BUTTON_RIGHT_SIZE = 0.02f;
	
	public String BUTTON_HIGHLIGHT_LEFT = "BUTTON_HIGHLIGHT_LEFT";
	public String BUTTON_HIGHLIGHT_MIDDLE = "BUTTON_HIGHLIGHT_MIDDLE";
	public String BUTTON_HIGHLIGHT_RIGHT = "BUTTON_HIGHLIGHT_RIGHT";
	
	public Color BUTTON_NORMAL_TEXT_COLOR = Color.WHITE;
	public Color BUTTON_HIGHLIGHT_TEXT_COLOR = Color.RED;
}
