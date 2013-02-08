package com.tyrlib2.graphics.renderables;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.opengl.GLES20;
import android.opengl.GLUtils;

import com.tyrlib2.graphics.renderer.IBlendable;
import com.tyrlib2.graphics.renderer.IRenderable;
import com.tyrlib2.graphics.renderer.Texture;
import com.tyrlib2.graphics.renderer.TextureManager;
import com.tyrlib2.graphics.scene.SceneNode;
import com.tyrlib2.graphics.scene.SceneObject;
import com.tyrlib2.math.AABB;
import com.tyrlib2.math.Vector2;
import com.tyrlib2.util.Color;

/**
 * This class renders 2D text
 * @author Sascha
 *
 */

public class Text2 extends SceneObject implements IRenderable, IBlendable {
	
	
	private String text;
	private Color color;
	private int textSize = 128;
	
	private Texture texture;
	private Image2 image;
	
	private float textLengthRatio;
	
	public Text2(String text, Color color) {
		this.text = text;
		this.color = color;
		createTexture();
		
		image = new Image2(new Vector2(0.2f, 0.3f / textLengthRatio), texture);
	}
	
	public void setText(String text) {
		this.text = text;
		createTexture();
	}
	
	@Override
	public void render(float[] vpMatrix) {
		if (texture.getHandle() == -1) {
			createTexture();
		}
		image.render(vpMatrix);
	}
	
	private void createTexture() {

		//Draw the text
		Paint textPaint = new Paint();
		textPaint.setTextSize(textSize);
		textPaint.setAntiAlias(true);
		textPaint.setARGB((int)(255*color.a), (int)(255*color.r), (int)(255*color.g), (int)(255*color.b));
		Rect bounds = new Rect();
		textPaint.getTextBounds(text, 0, text.length(), bounds);

		int textWidth = bounds.width()+textSize;
		int textHeight = bounds.height();
		
		int width = (int) Math.pow(2, Math.ceil(Math.log(textWidth)/Math.log(2)));
		int height = (int) Math.pow(2, Math.ceil(Math.log(textHeight)/Math.log(2)));
		
		textLengthRatio = (float) (width/ height);
		
		//Create an empty, mutable bitmap
		Bitmap bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_4444);
		//get a canvas to paint over the bitmap
		Canvas canvas = new Canvas(bitmap);
		bitmap.eraseColor(0);
		
		//draw the text centered
		canvas.drawText(text, textSize / 2, textHeight, textPaint);
		
		final int[] textureHandle = new int[1];
		
		//Generate one texture pointer...
		GLES20.glGenTextures(1, textureHandle, 0);
		//...and bind it to our array
		GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, textureHandle[0]);

		//Create Nearest Filtered Texture
		GLES20.glTexParameterf(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_MIN_FILTER, GLES20.GL_NEAREST);
		GLES20.glTexParameterf(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_MAG_FILTER, GLES20.GL_LINEAR);

		//Different possible texture parameters, e.g. GL10.GL_CLAMP_TO_EDGE
		GLES20.glTexParameterf(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_WRAP_S, GLES20.GL_CLAMP_TO_EDGE);
		GLES20.glTexParameterf(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_WRAP_T, GLES20.GL_CLAMP_TO_EDGE);

		//Use the Android GLUtils to specify a two-dimensional texture image from our bitmap
		GLUtils.texImage2D(GLES20.GL_TEXTURE_2D, 0, bitmap, 0);

		//Clean up
		bitmap.recycle();
		
		if (texture == null) {
			texture = new Texture(textureHandle[0]);
			TextureManager.getInstance().addUnnamedTexture(texture);
		} else {
			TextureManager.getInstance().destroyUnnamedTexture(texture);
			texture.setHandle(textureHandle[0]);
		}
	}
	
	@Override
	public void attachTo(SceneNode node)  {
		image.attachTo(node);
		super.attachTo(node);
	}
	

	@Override
	public SceneNode detach() {
		image.detach();
		return super.detach();
	}

	@Override
	public float getAlpha() {
		return image.getAlpha();
	}

	@Override
	public void setAlpha(float alpha) {
		image.setAlpha(alpha);
	}

	@Override
	public AABB getBoundingBox() {
		return image.getBoundingBox();
	}

	@Override
	public void setBoundingBoxVisible(boolean visible) {
		image.setBoundingBoxVisible(visible);
	}
}


