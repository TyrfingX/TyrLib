package com.tyrlib2.graphics.renderables;

import android.opengl.GLES20;

import com.tyrlib2.graphics.renderer.IRenderable;
import com.tyrlib2.graphics.renderer.Program;
import com.tyrlib2.graphics.scene.SceneObject;
import com.tyrlib2.graphics.text.Font;
import com.tyrlib2.graphics.text.GLText;
import com.tyrlib2.math.Vector3;
import com.tyrlib2.util.Color;

public class Text2  extends SceneObject implements IRenderable {
	
	private Color color;
	private String text;
	private int rotation;
	private Font font;
	
	public Text2(String text, int rotation, Color color, Font font) {
		this.color = color;
		this.font = font;
		this.text = text;
		this.rotation = rotation;
	}
	
	@Override
	public void render(float[] vpMatrix) {
		GLES20.glEnable(GLES20.GL_BLEND);
		GLES20.glBlendFunc(GLES20.GL_ONE, GLES20.GL_ONE_MINUS_SRC_ALPHA);
		GLText glText = font.glText;
		glText.begin( color.r, color.g, color.b, color.a, vpMatrix );         // Begin Text Rendering (Set Color WHITE)
		Vector3 pos = parent.getCachedAbsolutePos();
		glText.draw( text, pos.x, pos.y, rotation);              // Draw Test String
		glText.end();
		GLES20.glDisable(GLES20.GL_BLEND);
		
		Program.resetCache();
	}

	public Color getColor() {
		return color;
	}

	public void setColor(Color color) {
		this.color = color;
	}

	public String getText() {
		return text;
	}

	public void setText(String text) {
		this.text = text;
	}

	public int getRotation() {
		return rotation;
	}

	public void setRotation(int rotation) {
		this.rotation = rotation;
	}
	
	

}
