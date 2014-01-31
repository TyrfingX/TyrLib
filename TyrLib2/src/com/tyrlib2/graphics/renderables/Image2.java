package com.tyrlib2.graphics.renderables;

import com.tyrlib2.graphics.materials.TexturedMaterial;
import com.tyrlib2.graphics.renderer.Mesh;
import com.tyrlib2.graphics.renderer.Renderable2;
import com.tyrlib2.graphics.renderer.Texture;
import com.tyrlib2.graphics.renderer.TextureRegion;
import com.tyrlib2.graphics.renderer.TyrGL;
import com.tyrlib2.math.Vector2;

/**
 * Class for rendering a 2D image
 * @author Sascha
 *
 */

public class Image2 extends Renderable2 {
	private String textureName;
	private Vector2 size;
	private TextureRegion textureRegion;
	
	public static final short[] DRAW_ORDER_IMAGE = { 1, 2, 3, 0, 2, 1 };
	
	public Image2(Vector2 size, Texture texture) {
		this.size = size;
		this.material = new TexturedMaterial(texture);
		this.textureRegion = new TextureRegion();
		createMesh();
	}
	
	public Image2(Vector2 size, Texture texture, TextureRegion textureRegion) {
		this.size = size;
		this.textureRegion = textureRegion;
		this.material = new TexturedMaterial(texture);
		createMesh();
	}
	
	public Image2(Vector2 size, Texture texture, TextureRegion textureRegion, Vector2 uvSize) {
		this.size = size;
		this.textureRegion = textureRegion;
		this.material = new TexturedMaterial(texture);
		createMesh();
	}
	
	public Image2(Vector2 size, String textureName) {
		this.size = size;
		this.textureName = textureName;
		this.material = new TexturedMaterial(textureName);
		this.textureRegion = new TextureRegion();
		
		createMesh();
	}
	
	public Vector2 getSize() {
		return size;
	}
	
	public String getTextureName() {
		return textureName;
	}
	
	@Override
	public void render(float[] vpMatrix) {
		renderMode = TyrGL.GL_TRIANGLES;
		drawOrderLength = mesh.getDrawOrder().length;
		drawOrderBuffer = mesh.getDrawOrderBuffer();
		super.render(vpMatrix);
	}
	
	public void setAlpha(float alpha) {
		TexturedMaterial mat = (TexturedMaterial) material;
		mat.setAlpha(alpha);
	}
	
	public float getAlpha() {
		TexturedMaterial mat = (TexturedMaterial) material;
		return mat.getAlpha();
	}
	
	public void setTexture(Texture texture) {
		TexturedMaterial mat = (TexturedMaterial) material;
		mat.setTexture(texture);
	}
	
	public void setTextureRegion(TextureRegion textureRegion) {
		if (textureRegion != this.textureRegion) {
			this.textureRegion = textureRegion;
			createMesh();
		}
	}
	
	public void setSize(Vector2 size) {
		this.size = size;
		createMesh();
	}
	
	private void createMesh() {
		float[] vertexData = { 0, 0, 0, textureRegion.u1, textureRegion.v1,
							  size.x, 0, 0, textureRegion.u2, textureRegion.v1,
							  0, -size.y, 0, textureRegion.u1, textureRegion.v2,
							  size.x, -size.y, 0, textureRegion.u2, textureRegion.v2
		};
		this.mesh = new Mesh(vertexData, DRAW_ORDER_IMAGE, 4);
	}

}
