package com.tyrlib2.graphics.renderables;

import com.tyrlib2.graphics.renderer.IRenderable;
import com.tyrlib2.graphics.renderer.Program;
import com.tyrlib2.graphics.renderer.TyrGL;
import com.tyrlib2.graphics.scene.SceneObject;
import com.tyrlib2.graphics.text.Font;
import com.tyrlib2.graphics.text.IGLText;
import com.tyrlib2.math.Matrix;
import com.tyrlib2.math.Quaternion;
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
	private int insertionID;
	
	public Text2(String text, int rotation, Color color, Font font) {
		this.color = color;
		this.font = font;
		this.text = text;
		this.rotationValue = rotation;
		Quaternion.fromAxisAngle(new Vector3(0,0,1), rotation).toMatrix(this.rotation);
	}
	
	@Override
	public void render(float[] vpMatrix) {
		Program.blendEnable(TyrGL.GL_SRC_ALPHA, TyrGL.GL_ONE_MINUS_SRC_ALPHA);
		IGLText glText = font.glText;
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
		Quaternion.fromAxisAngle(new Vector3(0,0,1), rotation).toMatrix(this.rotation);
	}
	
	public void setScale(float scale) {
		this.scale = scale;
	}
	
	public void noMVP() {
		noMVP = true;
	}

	@Override
	public void renderShadow(float[] vpMatrix) {
		// TODO Auto-generated method stub
		
	}
	
	@Override
	public void setInsertionID(int id) {
		this.insertionID = id;
	}

	@Override
	public int getInsertionID() {
		return insertionID;
	}

	@Override
	public void destroy() {
		// TODO Auto-generated method stub
		
	}
	

}
