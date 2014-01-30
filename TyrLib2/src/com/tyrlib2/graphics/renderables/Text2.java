package com.tyrlib2.graphics.renderables;

import android.opengl.GLES20;
import android.opengl.Matrix;

import com.tyrlib2.graphics.renderer.IRenderable;
import com.tyrlib2.graphics.renderer.Program;
import com.tyrlib2.graphics.scene.SceneObject;
import com.tyrlib2.graphics.text.Font;
import com.tyrlib2.graphics.text.TextRenderer;
import com.tyrlib2.math.Vector3;
import com.tyrlib2.util.Color;

public class Text2  extends SceneObject implements IRenderable {
	
	private Color color;
	private String text;
	private Font font;
	private float scale;
	float [] mvp = new float[16];
	private float[] rotation = new float[16];
	private int rotationValue;
	
	private boolean noMVP = false;
	
	public Text2(String text, int rotation, Color color, Font font) {
		this.color = color;
		this.font = font;
		this.text = text;
		this.rotationValue = rotation;
		Matrix.setIdentityM(this.rotation, 0);
		Matrix.rotateM(this.rotation, 0, rotation, 0, 0, 1);
	}
	
	@Override
	public void render(float[] vpMatrix) {
		Program.blendEnable(GLES20.GL_ONE, GLES20.GL_ONE_MINUS_SRC_ALPHA);
		TextRenderer glText = font.glText;
		float tmpX = glText.getScaleX();
		float tmpY = glText.getScaleY();
		if (scale != 0) {
			glText.setScale(scale);
		}
		if (noMVP) {
			glText.begin( color.r, color.g, color.b, color.a, vpMatrix );
		} else {
			Matrix.multiplyMM(mvp, 0, vpMatrix, 0, getParent().getModelMatrix(), 0);
			glText.begin( color.r, color.g, color.b, color.a, mvp );         // Begin Text Rendering (Set Color WHITE)
		}
		Vector3 pos = new Vector3();
		glText.draw( text, pos.x, pos.y, rotation);              // Draw Test String
		glText.end();
		glText.setScale(tmpX, tmpY);
		Program.blendDisable();
		
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
		return rotationValue;
	}

	public void setRotation(int rotation) {
		this.rotationValue = rotation;
		
		Matrix.setIdentityM(this.rotation, 0);
		Matrix.rotateM(this.rotation, 0, rotation, 0, 0, 1);
	}
	
	public void setScale(float scale) {
		this.scale = scale;
	}
	
	public void noMVP() {
		noMVP = true;
	}
	
	

}
