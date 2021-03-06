package com.tyrlib2.graphics.renderables;

import com.tyrlib2.graphics.materials.TexturedMaterial;
import com.tyrlib2.graphics.renderer.Material;
import com.tyrlib2.graphics.renderer.Mesh;
import com.tyrlib2.graphics.renderer.Renderable2;
import com.tyrlib2.graphics.renderer.Texture;
import com.tyrlib2.graphics.renderer.TextureRegion;
import com.tyrlib2.graphics.renderer.TyrGL;
import com.tyrlib2.graphics.scene.SceneManager;
import com.tyrlib2.math.Quaternion;
import com.tyrlib2.math.Vector2;
import com.tyrlib2.math.Vector3;
import com.tyrlib2.util.Color;

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
		this.textureRegion = new TextureRegion();
		this.material = new TexturedMaterial(texture, textureRegion);
		createMesh();
	}
	
	public Image2(Vector2 size, Texture texture, TextureRegion textureRegion) {
		this.size = size;
		this.textureRegion = textureRegion;
		this.material = new TexturedMaterial(texture, textureRegion);
		createMesh();
	}
	
	public Image2(Vector2 size, Texture texture, TextureRegion textureRegion, Vector2 repeat) {
		this.size = size;
		this.material = new TexturedMaterial(texture, textureRegion);
		if (repeat != null) {
			TextureRegion region = new TextureRegion();
			region.u1 = textureRegion.u1;
			region.v1 = textureRegion.v1;
			region.u2 = (textureRegion.u2-textureRegion.u1)*repeat.x;
			region.v2 = (textureRegion.u2-textureRegion.u1)*repeat.y;
			this.textureRegion = region;
		} else {
			this.textureRegion = textureRegion;
		}
		createMesh();
	}
	
	public Image2(Vector2 size, String textureName) {
		this.size = size;
		this.textureName = textureName;
		this.textureRegion = new TextureRegion();
		this.material = new TexturedMaterial(textureName, textureRegion);
		
		
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
	
	public void setColor(Color color) {
		TexturedMaterial mat = (TexturedMaterial) material;
		mat.setColor(color);
	}
	
	public void setTexture(Texture texture) {
		TexturedMaterial mat = (TexturedMaterial) material;
		mat.setTexture(texture, textureRegion);
	}
	
	public void setMaterial(Material material) {
		this.material = material;
	}
	
	public void setTextureRegion(TextureRegion textureRegion) {
		if (textureRegion != this.textureRegion) {
			this.textureRegion = textureRegion;
			TexturedMaterial mat = (TexturedMaterial) material;
			mat.setTexture(mat.getTexture(), textureRegion);
			createMesh();
		}
	}
	
	public TextureRegion getTextureRegion() {
		TexturedMaterial mat = (TexturedMaterial) material;
		return mat.getTextureRegion();
	}
	
	public void setSize(Vector2 size) {
		this.size = size;
		createMesh();
	}
	
	private void createMesh() {
		float[] vertexData = { 	0, 0, 0, textureRegion.u1, textureRegion.v1,
								size.x, 0, 0, textureRegion.u2, textureRegion.v1,
								0, -size.y, 0, textureRegion.u1, textureRegion.v2,
								size.x, -size.y, 0, textureRegion.u2, textureRegion.v2
		};
		this.mesh = new Mesh(vertexData, DRAW_ORDER_IMAGE, 4);
	}

	public void rotate(Quaternion quat) {
		
		Vector3 topLeft = quat.multiply(new Vector3(0,0,0));
		Vector3 topRight = quat.multiply(new Vector3(size.x,0,0));
		Vector3 bottomLeft = quat.multiply(new Vector3(0,-size.y,0));
		Vector3 bottomRight = quat.multiply(new Vector3(size.x,-size.y,0));
		
		float[] vertexData = { 	topLeft.x, topLeft.y/SceneManager.getInstance().getViewportRatio(), topLeft.z, textureRegion.u1, textureRegion.v1,
								bottomLeft.x, bottomLeft.y/SceneManager.getInstance().getViewportRatio(), bottomLeft.z, textureRegion.u1, textureRegion.v2,
								topRight.x, topRight.y/SceneManager.getInstance().getViewportRatio(), topRight.z, textureRegion.u2, textureRegion.v1,
								bottomRight.x, bottomRight.y/SceneManager.getInstance().getViewportRatio(), bottomRight.z, textureRegion.u2, textureRegion.v2
		};
		this.mesh = new Mesh(vertexData, DRAW_ORDER_IMAGE, 4);
	}
	
}
