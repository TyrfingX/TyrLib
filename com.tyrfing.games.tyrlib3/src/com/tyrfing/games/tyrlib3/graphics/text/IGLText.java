package com.tyrfing.games.tyrlib3.graphics.text;


public interface IGLText {
	
	//--Load Font--//
	// description
	//    this will load the specified font file, create a texture for the defined
	//    character range, and setup all required values used to render with it.
	// arguments:
	//    file - Filename of the font (.ttf, .otf) to use. In 'Assets' folder.
	//    size - Requested pixel size of font (height)
	//    padX, padY - Extra padding per character (X+Y Axis); to prevent overlapping characters.
	public boolean load(String file, int size, int padX, int padY);

	public void toTexture();
	
	//--Begin/End Text Drawing--//
	// D: call these methods before/after (respectively all draw() calls using a text instance
	//    NOTE: color is set on a per-batch basis, and fonts should be 8-bit alpha only!!!
	// A: red, green, blue - RGB values for font (default = 1.0)
	//    alpha - optional alpha value for font (default = 1.0)
	// 	  vpMatrix - View and projection matrix to use
	// R: [none]
	public void begin(float[] vpMatrix);

	public void begin(float alpha, float[] vpMatrix);

	public void begin(float red, float green, float blue, float alpha,
			float[] vpMatrix);

	public void end();

	//--Draw Text--//
	// D: draw text at the specified x,y position
	// A: text - the string to draw
	//    x, y - the x,y position to draw text at (bottom left of text; including descent)
	//    angleDeg - angle to rotate the text
	// R: [none]
	public void draw(String text, float x, float y, float[] rotMatrix);

	public void draw(String text, float x, float y);

	//--Draw Text Centered--//
	// D: draw text CENTERED at the specified x,y position
	// A: text - the string to draw
	//    x, y - the x,y position to draw text at (bottom left of text)
	//    angleDeg - angle to rotate the text
	// R: the total width of the text that was drawn
	public float drawC(String text, float x, float y, float[] rotMatrix);

	public float drawC(String text, float x, float y);

	public float drawCX(String text, float x, float y);

	public void drawCY(String text, float x, float y);

	//--Set Scale--//
	// D: set the scaling to use for the font
	// A: scale - uniform scale for both x and y axis scaling
	//    sx, sy - separate x and y axis scaling factors
	// R: [none]
	public void setScale(float scale);

	public void setScale(float sx, float sy);

	//--Get Scale--//
	// D: get the current scaling used for the font
	// A: [none]
	// R: the x/y scale currently used for scale
	public float getScaleX();

	public float getScaleY();

	//--Set Space--//
	// D: set the spacing (unscaled; ie. pixel size) to use for the font
	// A: space - space for x axis spacing
	// R: [none]
	public void setSpace(float space);

	//--Get Space--//
	// D: get the current spacing used for the font
	// A: [none]
	// R: the x/y space currently used for scale
	public float getSpace();

	//--Get Length of a String--//
	// D: return the length of the specified string if rendered using current settings
	// A: text - the string to get length for
	// R: the length of the specified string (pixels)
	public float getLength(String text);

	//--Get Width/Height of Character--//
	// D: return the scaled width/height of a character, or max character width
	//    NOTE: since all characters are the same height, no character index is required!
	//    NOTE: excludes spacing!!
	// A: chr - the character to get width for
	// R: the requested character size (scaled)
	public float getCharWidth(char chr);

	public float getCharWidthMax();

	public float getCharHeight();

	//--Get Font Metrics--//
	// D: return the specified (scaled) font metric
	// A: [none]
	// R: the requested font metric (scaled)
	public float getAscent();

	public float getDescent();

	public float getHeight();

	//--Draw Font Texture--//
	// D: draw the entire font texture (NOTE: for testing purposes only)
	// A: width, height - the width and height of the area to draw to. this is used
	//    to draw the texture to the top-left corner.
	//    vpMatrix - View and projection matrix to use
	public void drawTexture(float width, float height, float[] vpMatrix);

	public int getSize();

}