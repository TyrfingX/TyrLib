package com.tyrlib2.gui;

import com.tyrlib2.math.Vector2;
import com.tyrlib2.util.Color;

/**
 * Defines the looknfeel for the GUI elements upon creation
 * @author Sascha
 *
 */

public class Skin {

	public String TEXTURE_ATLAS = "GUI";
	
	/** DEFAULT LABEL SETTINGS **/
	
	public Color LABEL_TEXT_COLOR = Color.WHITE;
	public Color LABEL_BG_COLOR = Color.TRANSPARENT;
	
	public String LABEL_FONT = "Roboto-Regular.ttf";
	
	
	/** DEFAULT BUTTON SETTINGS **/
	
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
	
	/** DEFAULT FRAME SETTINGS **/
	
	public String FRAME_LEFT = "FRAME_LEFT";
	public String FRAME_RIGHT = "FRAME_RIGHT";
	public String FRAME_TOP = "FRAME_TOP";
	public String FRAME_BOTTOM = "FRAME_BOTTOM";
	
	public String FRAME_TOPLEFT = "FRAME_TOPLEFT";
	public String FRAME_TOPRIGHT = "FRAME_TOPRIGHT";
	public String FRAME_BOTTOMLEFT = "FRAME_BOTTOMLEFT";
	public String FRAME_BOTTOMRIGHT = "FRAME_BOTTOMRIGHT";
	
	public String FRAME_MIDDLE = "FRAME_MIDDLE";
	public Vector2 FRAME_MIDDLE_REPEAT = new Vector2(1,1);
	
	public float FRAME_BORDER_SIZE = 0.005f;
	
	/** MESSAGE BOX AND POPUP SETTINGS **/
	
	public Color OVERLAY_COLOR = Color.BLACK;
	public float OVERLAY_MAX_ALPHA = 0.8f;
	public float MESSAGE_BOX_X = 0.2f;
	public float MESSAGE_BOX_Y = 0.35f;
	public float MESSAGE_BOX_W = 0.6f;
	public float MESSAGE_BOX_H = 0.3f;
	
	public float MESSAGE_BOX_BUTTON_W = 0.4f;
	public float MESSAGE_BOX_BUTTON_H = 0.3f;
	public float MESSAGE_BOX_BUTTON_PAD_Y = 0.5f;
	public float MESSAGE_BOX_LABEL_X = 0.05f;
	public float MESSAGE_BOX_LABEL_Y = 0.05f;
	
	/** PROGRESS BAR SETTINGS **/
	
	public Paint PROGRESS_BAR_BG_PAINT = new Paint(new Color(1,1,1,1), new Color(1,1,1,1), 0);
	public Color PROGRESS_BAR_COLOR = Color.RED;
	
	/** TOOLTIP SETTINGS **/
	
	public float TOOLTIP_MAX_ALPHA = 0.95f;
	public float TOOLTIP_FADE_TIME = 0.25f;
	public Vector2 TOOLTIP_PADDING = new Vector2(0.01f, 0.01f);
	public Paint TOOLTIP_PAINT = new Paint(new Color(0.1f, 0.1f, 0.1f, 1), new Color(0.2f, 0.2f, 0.2f, 0.8f), 4);
	
}
