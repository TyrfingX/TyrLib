// This is a OpenGL ES 1.0 dynamic font rendering system. It loads actual font
// files, generates a font map (texture) from them, and allows rendering of
// text strings.
//
// NOTE: the rendering portions of this class uses a sprite batcher in order
// provide decent speed rendering. Also, rendering assumes a BOTTOM-LEFT
// origin, and the (x,y) positions are relative to that, as well as the
// bottom-left of the string to render.

package com.tyrlib2.graphics.text;

import com.tyrlib2.bitmap.ICanvas;
import com.tyrlib2.bitmap.IDrawableBitmap;
import com.tyrlib2.bitmap.IFontMetrics;
import com.tyrlib2.bitmap.IPaint;
import com.tyrlib2.bitmap.ITypeface;
import com.tyrlib2.graphics.renderer.TextureRegion;
import com.tyrlib2.graphics.renderer.TyrGL;
import com.tyrlib2.graphics.text.programs.BatchTextProgram;
import com.tyrlib2.graphics.text.programs.Program;
import com.tyrlib2.main.Media;
import com.tyrlib2.math.Matrix;
import com.tyrlib2.math.Vector2;

public class GLText implements IGLText {

	//--Constants--//
	public final static int CHAR_START = 32;           // First Character (ASCII Code)
	public final static int CHAR_END = 126;            // Last Character (ASCII Code)
	public final static int CHAR_CNT = ( ( ( CHAR_END - CHAR_START ) + 1 ) + 1 );  // Character Count (Including Character to use for Unknown)

	public final static int CHAR_NONE = 32;            // Character to Use for Unknown (ASCII Code)
	public final static int CHAR_UNKNOWN = ( CHAR_CNT - 1 );  // Index of the Unknown Character

	public final static int FONT_SIZE_MIN = 6;         // Minumum Font Size (Pixels)
	public final static int FONT_SIZE_MAX = 180;       // Maximum Font Size (Pixels)

	public final static int CHAR_BATCH_SIZE = 48;     // Number of Characters to Render Per Batch
													  // must be the same as the size of u_MVPMatrix 
													  // in BatchTextProgram
	
	public static float[] modelMatrix = new float[16];

	//--Members--//
	SpriteBatch batch;                                 // Batch Renderer

	int fontPadX, fontPadY;                            // Font Padding (Pixels; On Each Side, ie. Doubled on Both X+Y Axis)

	int size;
	
	float fontHeight;                                  // Font Height (Actual; Pixels)
	float fontAscent;                                  // Font Ascent (Above Baseline; Pixels)
	float fontDescent;                                 // Font Descent (Below Baseline; Pixels)

	int textureId;                                     // Font Texture ID [NOTE: Public for Testing Purposes Only!]
	int textureSize;                                   // Texture Size for Font (Square) [NOTE: Public for Testing Purposes Only!]
	TextureRegion textureRgn;                          // Full Texture Region

	float charWidthMax;                                // Character Width (Maximum; Pixels)
	float charHeight;                                  // Character Height (Maximum; Pixels)
	final float[] charWidths;                          // Width of Each Character (Actual; Pixels)
	TextureRegion[] charRgn;                           // Region of Each Character (Texture Coordinates)
	int cellWidth, cellHeight;                         // Character Cell Width/Height
	int rowCnt, colCnt;                                // Number of Rows/Columns

	float scaleX, scaleY;                              // Font Scale (X,Y Axis)
	float spaceX;                                      // Additional (X,Y Axis) Spacing (Unscaled)
	
	private static Program mProgram; 				   // OpenGL Program object
	private static int mColorHandle;						   // Shader color handle	
	private static int mTextureUniformHandle;                 // Shader texture handle
	private IDrawableBitmap bitmap;

	public static void init() {
		if (mProgram == null) {
			mProgram = new BatchTextProgram();
			mProgram.init();
			mColorHandle = TyrGL.glGetUniformLocation(mProgram.getHandle(), "u_Color");
        	mTextureUniformHandle = TyrGL.glGetUniformLocation(mProgram.getHandle(), "u_Texture");
		}
	}
	
	//--Constructor--//
	// D: save program + asset manager, create arrays, and initialize the members
	public GLText(Program program) {

		charWidths = new float[CHAR_CNT];               // Create the Array of Character Widths
		charRgn = new TextureRegion[CHAR_CNT];          // Create the Array of Character Regions

		// initialize remaining members
		fontPadX = 0;
		fontPadY = 0;

		fontHeight = 0.0f;
		fontAscent = 0.0f;
		fontDescent = 0.0f;

		textureId = -1;
		textureSize = 0;

		charWidthMax = 0;
		charHeight = 0;

		cellWidth = 0;
		cellHeight = 0;
		rowCnt = 0;
		colCnt = 0;

		Vector2 screenSize = Media.CONTEXT.getScreenSize();
		float ratio = screenSize.y / screenSize.x;
		
		scaleX = ratio;                                  // Default Scale = 1 (Unscaled)
		scaleY = 1.0f;                                  // Default Scale = 1 (Unscaled)
		spaceX = 0.0f;
	}
	
	// Constructor using the default program (BatchTextProgram)
	public GLText() {
		this(null);
	}

	//--Load Font--//
	// description
	//    this will load the specified font file, create a texture for the defined
	//    character range, and setup all required values used to render with it.
	// arguments:
	//    file - Filename of the font (.ttf, .otf) to use. In 'Assets' folder.
	//    size - Requested pixel size of font (height)
	//    padX, padY - Extra padding per character (X+Y Axis); to prevent overlapping characters.
	/* (non-Javadoc)
	 * @see com.tyrlib2.graphics.text.TextRenderer#load(java.lang.String, int, int, int)
	 */
	@Override
	public boolean load(String file, int size, int padX, int padY) {

		this.size = size;
		
		// setup requested values
		fontPadX = padX;                                // Set Requested X Axis Padding
		fontPadY = padY;                                // Set Requested Y Axis Padding

		// load the font and setup paint instance for drawing
		ICanvas canvas = Media.CONTEXT.createCanvas();           // Create Canvas for Rendering to Bitmap
		ITypeface tf = Media.CONTEXT.createFromAsset(file);  // Create the Typeface from Font File
		IPaint paint = Media.CONTEXT.createPaint(canvas);                      // Create Android Paint Instance
		paint.setAntiAlias( true );                     // Enable Anti Alias
		paint.setTypeface( tf );                        // Set Typeface
		paint.setTextSize( size );                      // Set Text Size
		paint.setColor( 0xffffffff );                   // Set ARGB (White, Opaque)
		

		// get font metrics
		IFontMetrics fm = paint.getFontMetrics();  // Get Font Metrics
		fontHeight = Math.abs( fm.bottom ) + Math.abs( fm.top ) ;  // Calculate Font Height
		fontAscent = (float)Math.ceil( Math.abs( fm.ascent ) );  // Save Font Ascent
		fontDescent = (float)Math.ceil( Math.abs( fm.descent ) );  // Save Font Descent

		// determine the width of each character (including unknown character)
		// also determine the maximum character width
		char[] s = new char[2];                         // Create Character Array
		charWidthMax = charHeight = 0;                  // Reset Character Width/Height Maximums
		float[] w = new float[2];                       // Working Width Value
		int cnt = 0;                                    // Array Counter
		for ( char c = CHAR_START; c <= CHAR_END; c++ )  {  // FOR Each Character
			s[0] = c;                                    // Set Character
			paint.getTextWidths( s, 0, 1, w );           // Get Character Bounds
			charWidths[cnt] = w[0];                      // Get Width
			if ( charWidths[cnt] > charWidthMax )        // IF Width Larger Than Max Width
				charWidthMax = charWidths[cnt];           // Save New Max Width
			cnt++;                                       // Advance Array Counter
		}
		s[0] = CHAR_NONE;                               // Set Unknown Character
		paint.getTextWidths( s, 0, 1, w );              // Get Character Bounds
		charWidths[cnt] = w[0];                         // Get Width
		if ( charWidths[cnt] > charWidthMax )           // IF Width Larger Than Max Width
			charWidthMax = charWidths[cnt];              // Save New Max Width
		cnt++;                                          // Advance Array Counter

		// set character height to font height
		charHeight = fontHeight;                        // Set Character Height

		// find the maximum size, validate, and setup cell sizes
		cellWidth = (int)charWidthMax + ( 2 * fontPadX );  // Set Cell Width
		cellHeight = (int)charHeight + ( 2 * fontPadY );  // Set Cell Height
		int maxSize = cellWidth > cellHeight ? cellWidth : cellHeight;  // Save Max Size (Width/Height)
		if ( maxSize < FONT_SIZE_MIN || maxSize > FONT_SIZE_MAX )  // IF Maximum Size Outside Valid Bounds
			return false;                                // Return Error

		// set texture size based on max font size (width or height)
		// NOTE: these values are fixed, based on the defined characters. when
		// changing start/end characters (CHAR_START/CHAR_END) this will need adjustment too!
		if ( maxSize <= 24 )                            // IF Max Size is 18 or Less
			textureSize = 256;                           // Set 256 Texture Size
		else if ( maxSize <= 40 )                       // ELSE IF Max Size is 40 or Less
			textureSize = 512;                           // Set 512 Texture Size
		else if ( maxSize <= 80 )                       // ELSE IF Max Size is 80 or Less
			textureSize = 1024;                          // Set 1024 Texture Size
		else                                            // ELSE IF Max Size is Larger Than 80 (and Less than FONT_SIZE_MAX)
			textureSize = 2048;                          // Set 2048 Texture Size

		// create an empty bitmap (alpha only)
		bitmap = Media.CONTEXT.createAlphaBitmap( textureSize, textureSize );  // Create Bitmap
		bitmap.eraseColor( 0x00000000 );                // Set Transparent Background (ARGB)
		canvas.setBitmap(bitmap);
		
		// calculate rows/columns
		// NOTE: while not required for anything, these may be useful to have :)
		colCnt = textureSize / cellWidth;               // Calculate Number of Columns
		rowCnt = (int)Math.ceil( (float)CHAR_CNT / (float)colCnt );  // Calculate Number of Rows

		// render each of the characters to the canvas (ie. build the font map)
		float x = fontPadX;                             // Set Start Position (X)
		float y = ( cellHeight - 1 ) - fontDescent - fontPadY;  // Set Start Position (Y)
		for ( char c = CHAR_START; c <= CHAR_END; c++ )  {  // FOR Each Character
			s[0] = c;                                    // Set Character to Draw
			canvas.drawText( s, 0, 1, x, y, paint );     // Draw Character
			x += cellWidth;                              // Move to Next Character
			if ( ( x + cellWidth - fontPadX ) > textureSize )  {  // IF End of Line Reached
				x = fontPadX;                             // Set X for New Row
				y += cellHeight + fontPadY;                          // Move Down a Row
			}
		}
		s[0] = CHAR_NONE;                               // Set Character to Use for NONE
		canvas.drawText( s, 0, 1, x, y, paint );        // Draw Character

		// setup the array of character texture regions
		x = 0;                                          // Initialize X
		y = 0;                                          // Initialize Y
		for ( int c = 0; c < CHAR_CNT; c++ )  {         // FOR Each Character (On Texture)
			charRgn[c] = new TextureRegion( textureSize, textureSize, x, y, cellWidth-1, cellHeight-1 );  // Create Region for Character
			x += cellWidth;                              // Move to Next Char (Cell)
			if ( x + cellWidth > textureSize )  {
				x = 0;                                    // Reset X Position to Start
				y += cellHeight + fontPadY;                          // Move to Next Row (Cell)
			}
		}

		// create full texture region
		textureRgn = new TextureRegion( textureSize, textureSize, 0, 0, textureSize, textureSize );  // Create Full Texture Region

		// return success
		return true;                                    // Return Success
	}
	
	@Override
	public void toTexture() {
		batch = new SpriteBatch(CHAR_BATCH_SIZE, mProgram );  // Create Sprite Batch (with Defined Size)
		textureId = bitmap.toTexture();
		bitmap.recycle();
		bitmap = null;
	}

	//--Begin/End Text Drawing--//
	// D: call these methods before/after (respectively all draw() calls using a text instance
	//    NOTE: color is set on a per-batch basis, and fonts should be 8-bit alpha only!!!
	// A: red, green, blue - RGB values for font (default = 1.0)
	//    alpha - optional alpha value for font (default = 1.0)
	// 	  vpMatrix - View and projection matrix to use
	// R: [none]
	/* (non-Javadoc)
	 * @see com.tyrlib2.graphics.text.TextRenderer#begin(float[])
	 */
	@Override
	public void begin(float[] vpMatrix)  {
		begin( 1.0f, 1.0f, 1.0f, 1.0f, vpMatrix );                // Begin with White Opaque
	}
	/* (non-Javadoc)
	 * @see com.tyrlib2.graphics.text.TextRenderer#begin(float, float[])
	 */
	@Override
	public void begin(float alpha, float[] vpMatrix)  {
		begin( 1.0f, 1.0f, 1.0f, alpha, vpMatrix );               // Begin with White (Explicit Alpha)
	}
	/* (non-Javadoc)
	 * @see com.tyrlib2.graphics.text.TextRenderer#begin(float, float, float, float, float[])
	 */
	@Override
	public void begin(float red, float green, float blue, float alpha, float[] vpMatrix)  {
		initDraw(red, green, blue, alpha);
		batch.beginBatch(vpMatrix);                             // Begin Batch
	}
	
	void initDraw(float red, float green, float blue, float alpha) {
		TyrGL.glUseProgram(mProgram.getHandle()); // specify the program to use
		
		// set color TODO: only alpha component works, text is always black #BUG
		TyrGL.glUniform4f(mColorHandle, red,green,blue,alpha); 
		
		TyrGL.glActiveTexture(TyrGL.GL_TEXTURE0);  // Set the active texture unit to texture unit 0
		
		TyrGL.glBindTexture(TyrGL.GL_TEXTURE_2D, textureId); // Bind the texture to this unit
		
		// Tell the texture uniform sampler to use this texture in the shader by binding to texture unit 0
		TyrGL.glUniform1i(mTextureUniformHandle, 0); 
	}
	
	/* (non-Javadoc)
	 * @see com.tyrlib2.graphics.text.TextRenderer#end()
	 */
	@Override
	public void end()  {
		batch.endBatch();                               // End Batch
	}

	//--Draw Text--//
	// D: draw text at the specified x,y position
	// A: text - the string to draw
	//    x, y - the x,y position to draw text at (bottom left of text; including descent)
	//    angleDeg - angle to rotate the text
	// R: [none]
	/* (non-Javadoc)
	 * @see com.tyrlib2.graphics.text.TextRenderer#draw(java.lang.String, float, float, float[])
	 */
	@Override
	public void draw(String text, float x, float y, float[] rotMatrix)  {
		float chrHeight = cellHeight * scaleY;          // Calculate Scaled Character Height
		float chrWidth = cellWidth * scaleX;            // Calculate Scaled Character Width
		int len = text.length();                        // Get String Length
		x += ( chrWidth / 2.0f ) - ( fontPadX * scaleX );  // Adjust Start X
		y += ( chrHeight / 2.0f ) - ( fontPadY * scaleY );  // Adjust Start Y
		
		// create a model matrix based on x, y and angleDeg
		modelMatrix[0] = 1;
		modelMatrix[1] = 0;
		modelMatrix[2] = 0;
		modelMatrix[3] = 0;
		modelMatrix[4] = 0;
		modelMatrix[5] = 1;
		modelMatrix[6] = 0;
		modelMatrix[7] = 0;
		modelMatrix[8] = 0;
		modelMatrix[9] = 0;
		modelMatrix[10] = 1;
		modelMatrix[11] = 0;
		modelMatrix[12] = 0;
		modelMatrix[13] = 0;
		modelMatrix[14] = 0;
		modelMatrix[15] = 1;
		
		//Matrix.setIdentityM(modelMatrix, 0);
		
		if (rotMatrix != null) {
			Matrix.multiplyMM(modelMatrix, 0, rotMatrix, 0, modelMatrix, 0);
		}
		
		modelMatrix[12] = x;
		modelMatrix[13] = y;
		

		

		
		float letterX, letterY; 
		letterX = letterY = 0;
		
		for (int i = 0; i < len; i++)  {              // FOR Each Character in String
			int c = (int)text.charAt(i) - CHAR_START;  // Calculate Character Index (Offset by First Char in Font)
			if (c < 0 || c >= CHAR_CNT)                // IF Character Not In Font
				c = CHAR_UNKNOWN;                         // Set to Unknown Character Index
			//TODO: optimize - applying the same model matrix to all the characters in the string
			batch.drawSprite(letterX, letterY, chrWidth, chrHeight, charRgn[c], modelMatrix);  // Draw the Character
			letterX += (charWidths[c] + spaceX ) * scaleX;    // Advance X Position by Scaled Character Width
		}
	}
	/* (non-Javadoc)
	 * @see com.tyrlib2.graphics.text.TextRenderer#draw(java.lang.String, float, float)
	 */
	@Override
	public void draw(String text, float x, float y) {
		draw(text, x, y, null);
	}

	//--Draw Text Centered--//
	// D: draw text CENTERED at the specified x,y position
	// A: text - the string to draw
	//    x, y - the x,y position to draw text at (bottom left of text)
	//    angleDeg - angle to rotate the text
	// R: the total width of the text that was drawn
	/* (non-Javadoc)
	 * @see com.tyrlib2.graphics.text.TextRenderer#drawC(java.lang.String, float, float, float[])
	 */
	@Override
	public float drawC(String text, float x, float y, float[] rotMatrix)  {
		float len = getLength( text );                  // Get Text Length
		draw( text, x - ( len / 2.0f ), y - ( getCharHeight() / 2.0f ), rotMatrix );  // Draw Text Centered
		return len;                                     // Return Length
	}
	/* (non-Javadoc)
	 * @see com.tyrlib2.graphics.text.TextRenderer#drawC(java.lang.String, float, float)
	 */
	@Override
	public float drawC(String text, float x, float y) {
		return drawC(text, x, y, null);
		
	}
	/* (non-Javadoc)
	 * @see com.tyrlib2.graphics.text.TextRenderer#drawCX(java.lang.String, float, float)
	 */
	@Override
	public float drawCX(String text, float x, float y)  {
		float len = getLength( text );                  // Get Text Length
		draw( text, x - ( len / 2.0f ), y );            // Draw Text Centered (X-Axis Only)
		return len;                                     // Return Length
	}
	/* (non-Javadoc)
	 * @see com.tyrlib2.graphics.text.TextRenderer#drawCY(java.lang.String, float, float)
	 */
	@Override
	public void drawCY(String text, float x, float y)  {
		draw( text, x, y - ( getCharHeight() / 2.0f ) );  // Draw Text Centered (Y-Axis Only)
	}

	//--Set Scale--//
	// D: set the scaling to use for the font
	// A: scale - uniform scale for both x and y axis scaling
	//    sx, sy - separate x and y axis scaling factors
	// R: [none]
	/* (non-Javadoc)
	 * @see com.tyrlib2.graphics.text.TextRenderer#setScale(float)
	 */
	@Override
	public void setScale(float scale)  {
		scaleX = scaleY = scale;                        // Set Uniform Scale
	}
	/* (non-Javadoc)
	 * @see com.tyrlib2.graphics.text.TextRenderer#setScale(float, float)
	 */
	@Override
	public void setScale(float sx, float sy)  {
		scaleX = sx;                                    // Set X Scale
		scaleY = sy;                                    // Set Y Scale
	}

	//--Get Scale--//
	// D: get the current scaling used for the font
	// A: [none]
	// R: the x/y scale currently used for scale
	/* (non-Javadoc)
	 * @see com.tyrlib2.graphics.text.TextRenderer#getScaleX()
	 */
	@Override
	public float getScaleX()  {
		return scaleX;                                  // Return X Scale
	}
	/* (non-Javadoc)
	 * @see com.tyrlib2.graphics.text.TextRenderer#getScaleY()
	 */
	@Override
	public float getScaleY()  {
		return scaleY;                                  // Return Y Scale
	}

	//--Set Space--//
	// D: set the spacing (unscaled; ie. pixel size) to use for the font
	// A: space - space for x axis spacing
	// R: [none]
	/* (non-Javadoc)
	 * @see com.tyrlib2.graphics.text.TextRenderer#setSpace(float)
	 */
	@Override
	public void setSpace(float space)  {
		spaceX = space;                                 // Set Space
	}

	//--Get Space--//
	// D: get the current spacing used for the font
	// A: [none]
	// R: the x/y space currently used for scale
	/* (non-Javadoc)
	 * @see com.tyrlib2.graphics.text.TextRenderer#getSpace()
	 */
	@Override
	public float getSpace()  {
		return spaceX;                                  // Return X Space
	}

	//--Get Length of a String--//
	// D: return the length of the specified string if rendered using current settings
	// A: text - the string to get length for
	// R: the length of the specified string (pixels)
	/* (non-Javadoc)
	 * @see com.tyrlib2.graphics.text.TextRenderer#getLength(java.lang.String)
	 */
	@Override
	public float getLength(String text) {
		float len = 0.0f;                               // Working Length
		int strLen = text.length();                     // Get String Length (Characters)
		for ( int i = 0; i < strLen; i++ )  {           // For Each Character in String (Except Last
			int c = (int)text.charAt( i ) - CHAR_START;  // Calculate Character Index (Offset by First Char in Font)
			len += ( charWidths[c] * 1.0f );           // Add Scaled Character Width to Total Length
		}
		len += ( strLen > 1 ? ( ( strLen - 1 ) * spaceX ) * 1.0f : 0 );  // Add Space Length
		return len;                                     // Return Total Length
	}

	//--Get Width/Height of Character--//
	// D: return the scaled width/height of a character, or max character width
	//    NOTE: since all characters are the same height, no character index is required!
	//    NOTE: excludes spacing!!
	// A: chr - the character to get width for
	// R: the requested character size (scaled)
	/* (non-Javadoc)
	 * @see com.tyrlib2.graphics.text.TextRenderer#getCharWidth(char)
	 */
	@Override
	public float getCharWidth(char chr)  {
		int c = chr - CHAR_START;                       // Calculate Character Index (Offset by First Char in Font)
		return ( charWidths[c] * scaleX );              // Return Scaled Character Width
	}
	/* (non-Javadoc)
	 * @see com.tyrlib2.graphics.text.TextRenderer#getCharWidthMax()
	 */
	@Override
	public float getCharWidthMax()  {
		return ( charWidthMax * scaleX );               // Return Scaled Max Character Width
	}
	/* (non-Javadoc)
	 * @see com.tyrlib2.graphics.text.TextRenderer#getCharHeight()
	 */
	@Override
	public float getCharHeight() {
		return ( charHeight * scaleY );                 // Return Scaled Character Height
	}

	//--Get Font Metrics--//
	// D: return the specified (scaled) font metric
	// A: [none]
	// R: the requested font metric (scaled)
	/* (non-Javadoc)
	 * @see com.tyrlib2.graphics.text.TextRenderer#getAscent()
	 */
	@Override
	public float getAscent()  {
		return ( fontAscent * scaleY );                 // Return Font Ascent
	}
	/* (non-Javadoc)
	 * @see com.tyrlib2.graphics.text.TextRenderer#getDescent()
	 */
	@Override
	public float getDescent()  {
		return ( fontDescent * scaleY );                // Return Font Descent
	}
	/* (non-Javadoc)
	 * @see com.tyrlib2.graphics.text.TextRenderer#getHeight()
	 */
	@Override
	public float getHeight()  {
		return ( fontHeight * scaleY );                 // Return Font Height (Actual)
	}

	//--Draw Font Texture--//
	// D: draw the entire font texture (NOTE: for testing purposes only)
	// A: width, height - the width and height of the area to draw to. this is used
	//    to draw the texture to the top-left corner.
	//    vpMatrix - View and projection matrix to use
	/* (non-Javadoc)
	 * @see com.tyrlib2.graphics.text.TextRenderer#drawTexture(float, float, float[])
	 */
	@Override
	public void drawTexture(float width, float height, float[] vpMatrix)  {
		initDraw(1.0f, 1.0f, 1.0f, 1.0f);

		batch.beginBatch(vpMatrix);                  // Begin Batch (Bind Texture)
		float[] idMatrix = new float[16];
		Matrix.setIdentityM(idMatrix, 0);
		batch.drawSprite(width - (textureSize / 2), height - ( textureSize / 2 ), 
				textureSize, textureSize, textureRgn, idMatrix);  // Draw
		batch.endBatch();                               // End Batch
	}
	
	/* (non-Javadoc)
	 * @see com.tyrlib2.graphics.text.TextRenderer#getSize()
	 */
	@Override
	public int getSize() {
		return size;
	}
}
