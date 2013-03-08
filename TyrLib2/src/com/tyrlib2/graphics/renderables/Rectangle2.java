package com.tyrlib2.graphics.renderables;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.ShortBuffer;

import android.opengl.GLES20;

import com.tyrlib2.graphics.materials.ColoredMaterial;
import com.tyrlib2.graphics.renderer.Material;
import com.tyrlib2.graphics.renderer.Renderable2;
import com.tyrlib2.math.Vector2;
import com.tyrlib2.math.Vector3;
import com.tyrlib2.util.Color;

/**
 * A 2D rectangle
 * @author Sascha
 *
 */

public class Rectangle2 extends Renderable2 {
	private Vector2 size;

	private Color color;
	private boolean filled;
	
	private int borderWidth;
	private boolean hasBorder;
	
	public static final Color 	DEFAULT_COLOR = Color.BLACK;
	public static final int		DEFAULT_BORDER_WIDTH = 1;
	
	public static final short[] DRAW_ORDER_QUAD = { 2, 1, 0, 2, 3, 1};
	public static final short[] DRAW_ORDER_BORDER = { 0, 1, 3, 2};
	private static ShortBuffer borderDrawOrderBuffer;
	
	
	public Rectangle2(Vector2 size, Color color) {
		this.size = size;
		this.color = color;
		this.filled = true;
		this.hasBorder = false;
		
		Vector3[] points = { 	new Vector3(0, 0, 0),
								new Vector3(size.x, 0, 0),
								new Vector3(0, -size.y, 0),
								new Vector3(size.x, -size.y, 0) };
		
		Material material = new ColoredMaterial(new Color[] { color });
		
		init(material, points, DRAW_ORDER_QUAD);
	
		if (borderDrawOrderBuffer == null) {
	        // initialize byte buffer for the draw list
	        ByteBuffer dlb = ByteBuffer.allocateDirect(DRAW_ORDER_BORDER.length * 2);
	        dlb.order(ByteOrder.nativeOrder());
	        borderDrawOrderBuffer = dlb.asShortBuffer();
	        borderDrawOrderBuffer.put(DRAW_ORDER_BORDER);
	        borderDrawOrderBuffer.position(0);
		}
		
	}
	
	public Rectangle2(Vector2 size) {
		this(size, DEFAULT_COLOR);
	}
	
	public Vector2 getSize() {
		return size;
	}
	
	public void setFilled(boolean filled) {
		this.filled = filled;
		if (filled == false) {
			this.setBorder(DEFAULT_BORDER_WIDTH);
		}
	}
	
	public void setBorder(int borderWidth) {
		this.borderWidth = borderWidth;
		this.hasBorder = true;
	}
	
	@Override
	public void render(float[] vpMatrix) {
		if (filled) {
			renderMode = GLES20.GL_TRIANGLES;
			drawOrderLength = mesh.getDrawOrder().length;
			drawOrderBuffer = mesh.getDrawOrderBuffer();
			super.render(vpMatrix);
		}
		
		if (hasBorder) {
			renderMode = GLES20.GL_LINE_LOOP;
			GLES20.glLineWidth(borderWidth);
			drawOrderLength = DRAW_ORDER_BORDER.length;
			drawOrderBuffer = borderDrawOrderBuffer;
			super.render(vpMatrix);
			
			renderMode = GLES20.GL_POINTS;
			super.render(vpMatrix);
		}
	}
	
	public Color getColor() {
		return new Color(color.r, color.g, color.b, color.a);
	}
	
	public void setColor(Color color) {
		this.color = color;
	}
}