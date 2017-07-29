package com.tyrfing.games.tyrlib3.view.graphics.renderables;

import com.tyrfing.games.tyrlib3.model.game.Color;
import com.tyrfing.games.tyrlib3.model.math.Vector2F;
import com.tyrfing.games.tyrlib3.model.math.Vector3F;
import com.tyrfing.games.tyrlib3.view.graphics.Mesh;
import com.tyrfing.games.tyrlib3.view.graphics.TyrGL;
import com.tyrfing.games.tyrlib3.view.graphics.materials.ColoredMaterial;
import com.tyrfing.games.tyrlib3.view.graphics.materials.Material;

/**
 * A 2D rectangle
 * @author Sascha
 *
 */

public class Rectangle2 extends Renderable2 {
	private Vector2F size;

	private Color color;
	private boolean filled;
	
	private int borderWidth;
	private boolean hasBorder;
	
	public static final Color 	DEFAULT_COLOR = Color.BLACK;
	public static final int		DEFAULT_BORDER_WIDTH = 1;
	
	public static final short[] DRAW_ORDER_QUAD = { 2, 1, 0, 2, 3, 1};
	public static final short[] DRAW_ORDER_BORDER = { 0, 1, 3, 2};
	
	private Mesh borderMesh;
	
	public Rectangle2(Vector2F size, Color color) {
		this.size = size;
		this.color = color;
		this.filled = true;
		this.hasBorder = false;
		createMesh();
	}
	
	private void createMesh() {
		Vector3F[] points = { 	new Vector3F(0, 0, 0),
								new Vector3F(size.x, 0, 0),
								new Vector3F(0, -size.y, 0),
								new Vector3F(size.x, -size.y, 0) };

		Material material = new ColoredMaterial(new Color[] { Color.WHITE.copy() });
		
		init(material, points, DRAW_ORDER_QUAD);
		
		setColor(color);
	}
	
	public Rectangle2(Vector2F size) {
		this(size, DEFAULT_COLOR);
	}

	public Vector2F getSize() {
		return size;
	}
	
	public void setSize(Vector2F size) {
		this.size = size;
		
		mesh.setVertexInfo(1 * material.getByteStride() + material.getPositionOffset() + 0, size.x);
		mesh.setVertexInfo(2 * material.getByteStride() + material.getPositionOffset() + 1, -size.y);
		mesh.setVertexInfo(3 * material.getByteStride() + material.getPositionOffset() + 0, size.x);
		mesh.setVertexInfo(3 * material.getByteStride() + material.getPositionOffset() + 1, -size.y);

		if (borderMesh != null) {
			borderMesh.setVertexInfo(1 * material.getByteStride() + material.getPositionOffset() + 0, size.x);
			borderMesh.setVertexInfo(2 * material.getByteStride() + material.getPositionOffset() + 1, -size.y);
			borderMesh.setVertexInfo(3 * material.getByteStride() + material.getPositionOffset() + 0, size.x);
			borderMesh.setVertexInfo(3 * material.getByteStride() + material.getPositionOffset() + 1, -size.y);
		}
	}
	
	public void setFilled(boolean filled) {
		this.filled = filled;
	}
	
	public void setBorder(int borderWidth) {
		this.borderWidth = borderWidth;
		this.hasBorder = true;
		
		Vector3F[] points = { 	new Vector3F(0, 0, 0),
								new Vector3F(size.x, 0, 0),
								new Vector3F(0, -size.y, 0),
								new Vector3F(size.x, -size.y, 0) };
		
		float[] vertexData = material.createVertexData(points, DRAW_ORDER_BORDER);
		borderMesh = new Mesh(vertexData, DRAW_ORDER_BORDER, vertexData.length / material.getByteStride());
	}
	
	@Override
	public void render(float[] vpMatrix) {
		if (filled) {
			renderMode = TyrGL.GL_TRIANGLES;
			drawOrderLength = mesh.getDrawOrder().length;
			drawOrderBuffer = mesh.getDrawOrderBuffer();
			super.render(vpMatrix);
		}
		
		if (hasBorder) {
			Mesh tmp = mesh;
			mesh = borderMesh;
			renderMode = TyrGL.GL_LINE_LOOP;
			TyrGL.glLineWidth(borderWidth);
			drawOrderLength = mesh.getDrawOrder().length;
			drawOrderBuffer = mesh.getDrawOrderBuffer();
			super.render(vpMatrix);
			
			//renderMode = TyrGL.GL_POINTS;
			//super.render(vpMatrix);
			mesh = tmp;
		}
	}
	
	public Color getColor() {
		return new Color(color.r, color.g, color.b, color.a);
	}
	
	public void setColor(Color color) {
		this.color = color;
		((ColoredMaterial) material).setColor(color);
	}
}