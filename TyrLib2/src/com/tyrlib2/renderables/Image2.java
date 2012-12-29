package com.tyrlib2.renderables;

import android.opengl.GLES20;

import com.tyrlib2.materials.TexturedMaterial;
import com.tyrlib2.math.Vector2;
import com.tyrlib2.renderer.Mesh;
import com.tyrlib2.renderer.Renderable2;
import com.tyrlib2.renderer.Texture;

/**
 * Class for rendering a 2D image
 * @author Sascha
 *
 */

public class Image2 extends Renderable2 {
	private String textureName;
	private Vector2 size;
	
	public static final short[] DRAW_ORDER_IMAGE = { 3, 2, 1, 1, 2, 0 };
	
	public Image2(Vector2 size, Texture texture) {
		this.size = size;
		
		this.material = new TexturedMaterial(texture);
		
		float[] vertexData = { 0, 0, 0, 0, 1,
							   size.x, 0, 0, 1, 1,
							   0, size.y, 0, 0, 0,
							   size.x, size.y, 0, 1, 0
							 };

		this.mesh = new Mesh(vertexData, DRAW_ORDER_IMAGE);
	}
	public Image2(Vector2 size, String textureName) {
		this.size = size;
		this.textureName = textureName;
		
		this.material = new TexturedMaterial(textureName);
		
		float[] vertexData = { 0, 0, 0, 0, 0,
							   size.x, 0, 0, 1, 0,
							   0, size.y, 0, 0, 1,
							   size.x, size.y, 0, 1, 1
							 };

		this.mesh = new Mesh(vertexData, DRAW_ORDER_IMAGE);
	}

	
	
	
	public Vector2 getSize() {
		return size;
	}
	
	public String getTextureName() {
		return textureName;
	}
	
	@Override
	public void render(float[] vpMatrix) {
		renderMode = GLES20.GL_TRIANGLES;
		drawOrderLength = mesh.getDrawOrder().length;
		drawOrderBuffer = mesh.getDrawOrderBuffer();
		super.render(vpMatrix);
	}

}
