package com.tyrlib2.graphics.terrain;

import java.nio.ByteBuffer;

import android.opengl.GLES20;
import android.util.FloatMath;

import com.tyrlib2.graphics.materials.DefaultMaterial3;
import com.tyrlib2.graphics.materials.TerrainMaterial;
import com.tyrlib2.graphics.renderer.Mesh;
import com.tyrlib2.graphics.renderer.Renderable;
import com.tyrlib2.graphics.renderer.Texture;
import com.tyrlib2.graphics.renderer.TextureManager;
import com.tyrlib2.graphics.renderer.TyrGL;
import com.tyrlib2.graphics.renderer.Viewport;
import com.tyrlib2.graphics.scene.SceneManager;
import com.tyrlib2.math.Vector2;
import com.tyrlib2.math.Vector3;

/**
 * Takes care of rendering terrain
 * @author Sascha
 *
 */

public class Terrain extends Renderable {

	private Vector2 size;
	private Vector2 step;
	private int width;
	private int height;
	private float maxHeight;
	
	public Terrain() {
		
	}
	
	public Terrain(TerrainMaterial material, Mesh mesh) {
		this.material = material;
		this.mesh = mesh;
	}
	
	public float getHeightAt(Vector2 point) {
		
		// Get the x,y vertex coordinates
		int x = (int) (point.x / step.x);
		int y = (int) (point.y / step.y);
		
		// Get a vector from the vertex to the desired point
		Vector2 gridPoint = new Vector2(x*step.x,y*step.y);
		Vector2 diff = point.sub(gridPoint);
	
		// Get the gradient
		Vector2 gradient = getGradient(x, y);
		
		int arrPos = x + width*y;
		int vertexPos = arrPos * material.getByteStride();
		float[] vertexData = mesh.getVertexData();
		
		float height = diff.dot(gradient) + vertexData[vertexPos + 2];
		
		return height;
	}
	
	public Vector2 getGradient(Vector2 point) {
		int x = (int) (point.x / step.x);
		int y = (int) (point.y / step.y);
		
		return getGradient(x, y);
	}
	
	public Vector2 getGradient(int x, int y) {
		int arrPos = x + width*y;
		int vertexPos = arrPos * material.getByteStride();
		float[] vertexData = mesh.getVertexData();
		
		float z1 = vertexData[vertexPos + width * material.getByteStride() + 2] - vertexData[vertexPos + 2];
		z1 /= step.x;
		
		float z2 = vertexData[vertexPos + material.getByteStride() + 2] - vertexData[vertexPos + 2];
		z2 /= step.y;
		
		return new Vector2(z1, z2);
	}
	
	public static Terrain fromHeightmap(String textureName, DefaultMaterial3 tileMaterial, Vector2 size, float maxHeight) {
		
		// First we get the heightmap texture
		Texture texture = TextureManager.getInstance().getTexture(textureName);
		
		Terrain terrain = new Terrain();
		terrain.size = size;
		terrain.maxHeight = maxHeight;
		
		short width = (short) texture.getSize().x;
		short height = (short) texture.getSize().y;
		
		float stepX = size.x / width;
		float stepY = size.y / height;
		
		terrain.step = new Vector2(stepX, stepY);
		terrain.width = width;
		terrain.height = height;
		
		ByteBuffer pixelBuffer = ByteBuffer.allocate(4*width*height);
		pixelBuffer.position(0);
		
		//Generate a new FBO. It will contain your texture.
		int[] fb = new int[1];
		TyrGL.glGenFramebuffers(1, fb, 0);
		TyrGL.glBindFramebuffer(TyrGL.GL_FRAMEBUFFER, fb[0]);
		
		//Create the texture 
		TyrGL.glBindTexture(TyrGL.GL_TEXTURE_2D, texture.getHandle());

		//Bind the texture to your FBO
		TyrGL.glFramebufferTexture2D(TyrGL.GL_FRAMEBUFFER, TyrGL.GL_COLOR_ATTACHMENT0, TyrGL.GL_TEXTURE_2D, texture.getHandle(), 0);
		
		// set the viewport as the FBO won't be the same dimension as the screen
		TyrGL.glViewport(0, 0, width, height);
		
		TyrGL.glReadPixels(0, 0, width, height, TyrGL.GL_RGBA, TyrGL.GL_UNSIGNED_BYTE, pixelBuffer);
		
		//Bind your main FBO again
		TyrGL.glBindFramebuffer(TyrGL.GL_FRAMEBUFFER, 0);
		// set the viewport as the FBO won't be the same dimension as the screen
		Viewport viewport = SceneManager.getInstance().getViewport();
		TyrGL.glViewport(0, 0, viewport.getWidth(), viewport.getHeight());
		
		
		float[] vertexData = new float[tileMaterial.getByteStride()*width*height];
		Vector3[] points = new Vector3[width*height];
		
		Vector2 textureRepeat = tileMaterial.getRepeat();
		
		for (int y = 0; y < height; ++y) {
			for (int x = 0; x < width; ++x) {
				int arrPos = x + width*y;
				int vertexPos = arrPos * tileMaterial.getByteStride();
				
				float pointHeight = (pixelBuffer.get(4*arrPos) & 0xff) * maxHeight / 255;
				
				points[arrPos] = new Vector3(x*stepX, y * stepY, pointHeight);
				
				vertexData[vertexPos + tileMaterial.getPositionOffset() + 0] = points[arrPos].x;
				vertexData[vertexPos + tileMaterial.getPositionOffset() + 1] = points[arrPos].y;
				vertexData[vertexPos + tileMaterial.getPositionOffset() + 2] = points[arrPos].z;
				
				int uvX = x;
				int uvY = y;
				
				vertexData[vertexPos + tileMaterial.getUVOffset() + 0] = uvX * textureRepeat.x;
				vertexData[vertexPos + tileMaterial.getUVOffset() + 1] = uvY * textureRepeat.y;
				
			}
		}
		
		// Now build the index data
		final int numTriangles = (width-1)*(height-1)*2;
		
		short[] drawOrder = new short[numTriangles * 3];

		int offset = 0;
		
		for (short y = 0; y < height - 1; y++) {
			for (short x = 0; x < width - 1; x++) {
				
				int arrPos = y * width + x;
				int vertexPos = arrPos * tileMaterial.getByteStride();
				
				Vector3 u1 = points[arrPos + width].sub(points[arrPos]);
				Vector3 u2 = points[arrPos + width].sub(points[arrPos + 1]);
				Vector3 normal = u1.cross(u2);
				normal.normalize();
				
				vertexData[vertexPos + tileMaterial.getNormalOffset() + 0] += normal.x/3;
				vertexData[vertexPos + tileMaterial.getNormalOffset() + 1] += normal.y/3;
				vertexData[vertexPos + tileMaterial.getNormalOffset() + 2] += Math.abs(normal.z/3);
				
				vertexPos = (arrPos + width) * tileMaterial.getByteStride();
				vertexData[vertexPos + tileMaterial.getNormalOffset() + 0] += normal.x/3;
				vertexData[vertexPos + tileMaterial.getNormalOffset() + 1] += normal.y/3;
				vertexData[vertexPos + tileMaterial.getNormalOffset() + 2] += Math.abs(normal.z/3);
				
				vertexPos = (arrPos + 1) * tileMaterial.getByteStride();
				vertexData[vertexPos + tileMaterial.getNormalOffset() + 0] += normal.x/3;
				vertexData[vertexPos + tileMaterial.getNormalOffset() + 1] += normal.y/3;
				vertexData[vertexPos + tileMaterial.getNormalOffset() + 2] += Math.abs(normal.z/3);
				
				drawOrder[offset++] = (short) arrPos;
				drawOrder[offset++] = (short) (arrPos + width);
				drawOrder[offset++] = (short) (arrPos + 1);
				
				drawOrder[offset++] = (short) (arrPos + width);
				drawOrder[offset++] = (short) (arrPos + width + 1);
				drawOrder[offset++] = (short) (arrPos + 1);
			}
		}
		
		terrain.material = tileMaterial;
		terrain.mesh = new Mesh(vertexData, drawOrder, width*height);
		
		return terrain;
		
	}
	
	public static Terrain fromHeightmap(String textureName, TerrainMaterial material, Vector2 size, float maxHeight) {
		
		// First we get the heightmap texture
		Texture texture = TextureManager.getInstance().getTexture(textureName);
		
		Terrain terrain = new Terrain();
		terrain.size = size;
		terrain.maxHeight = maxHeight;
		
		short width = (short) texture.getSize().x;
		short height = (short) texture.getSize().y;
		
		float stepX = size.x / width;
		float stepY = size.y / height;
		
		terrain.step = new Vector2(stepX, stepY);
		terrain.width = width;
		terrain.height = height;
		
		ByteBuffer pixelBuffer = ByteBuffer.allocate(4*width*height);
		pixelBuffer.position(0);
		
		//Generate a new FBO. It will contain your texture.
		int[] fb = new int[1];
		TyrGL.glGenFramebuffers(1, fb, 0);
		TyrGL.glBindFramebuffer(TyrGL.GL_FRAMEBUFFER, fb[0]);
		
		//Create the texture 
		TyrGL.glBindTexture(TyrGL.GL_TEXTURE_2D, texture.getHandle());

		//Bind the texture to your FBO
		TyrGL.glFramebufferTexture2D(TyrGL.GL_FRAMEBUFFER, TyrGL.GL_COLOR_ATTACHMENT0, TyrGL.GL_TEXTURE_2D, texture.getHandle(), 0);
		
		// set the viewport as the FBO won't be the same dimension as the screen
		TyrGL.glViewport(0, 0, width, height);
		
		TyrGL.glReadPixels(0, 0, width, height, TyrGL.GL_RGBA, TyrGL.GL_UNSIGNED_BYTE, pixelBuffer);
		
		//Bind your main FBO again
		TyrGL.glBindFramebuffer(TyrGL.GL_FRAMEBUFFER, 0);
		// set the viewport as the FBO won't be the same dimension as the screen
		Viewport viewport = SceneManager.getInstance().getViewport();
		TyrGL.glViewport(0, 0, viewport.getWidth(), viewport.getHeight());
		
		
		float[] vertexData = new float[material.getByteStride()*width*height];
		Vector3[] points = new Vector3[width*height];
		
		Vector2 textureRepeat = material.getRepeat();
		
		for (int y = 0; y < height; ++y) {
			for (int x = 0; x < width; ++x) {
				int arrPos = x + width*y;
				int vertexPos = arrPos * material.getByteStride();
				
				float normHeight = (pixelBuffer.get(4*arrPos) & 0xff) / 255.f;
				float pointHeight = normHeight * maxHeight ;
				
				points[arrPos] = new Vector3(x*stepX, y * stepY, pointHeight);
				
				vertexData[vertexPos + material.getPositionOffset() + 0] = points[arrPos].x;
				vertexData[vertexPos + material.getPositionOffset() + 1] = points[arrPos].y;
				vertexData[vertexPos + material.getPositionOffset() + 2] = points[arrPos].z;
				
				int uvX = x;
				int uvY = y;
				
				vertexData[vertexPos + material.getUVOffset() + 0] = uvX * textureRepeat.x;
				vertexData[vertexPos + material.getUVOffset() + 1] = uvY * textureRepeat.y;
				
				float totalWeight = 0;
				for (int i = 0; i < TerrainMaterial.TEXTURES_PER_CHUNK; ++i) {
					TerrainTexture tex = material.getTerrainTexture(i);
					if (tex != null) {
						// get the slope
						float slope = 0;
						if (x < width - 1 && y < height - 1) {
							int arrPos2 = x + 1 + width*(y+1);
							float heightOther = (pixelBuffer.get(4*arrPos2) & 0xff) / 255.f;
							float diff = Math.abs(heightOther - normHeight);
							slope = diff / (FloatMath.sqrt(stepX * stepX + stepY * stepY));
						}
						
						float weight = tex.getWeight(normHeight, slope);
						totalWeight += weight;
					}
				}
				
				for (int i = 0; i < TerrainMaterial.TEXTURES_PER_CHUNK; ++i) {
					TerrainTexture tex = material.getTerrainTexture(i);
					if (tex != null) {
						
						// get the slope
						float slope = 0;
						if (x < width - 1 && y < height - 1) {
							int arrPos2 = x + 1 + width*(y+1);
							float heightOther = (pixelBuffer.get(4*arrPos2) & 0xff) / 255.f;
							float diff = Math.abs(heightOther - normHeight);
							slope = diff / (FloatMath.sqrt(stepX * stepX + stepY * stepY));
						}
						
						float weight = tex.getWeight(normHeight, slope);
						
						vertexData[vertexPos + material.getTextureWeightOffset() + i] = weight / totalWeight;
					}
				}
			}
		}
		
		// Now build the index data
		final int numTriangles = (width-1)*(height-1)*2;
		
		short[] drawOrder = new short[numTriangles * 3];

		int offset = 0;
		
		for (short y = 0; y < height - 1; y++) {
			for (short x = 0; x < width - 1; x++) {
				
				int arrPos = y * width + x;
				int vertexPos = arrPos * material.getByteStride();
				
				Vector3 u1 = points[arrPos + width].sub(points[arrPos]);
				Vector3 u2 = points[arrPos + width].sub(points[arrPos + 1]);
				Vector3 normal = u1.cross(u2);
				normal.normalize();
				
				vertexData[vertexPos + material.getNormalOffset() + 0] += normal.x/3;
				vertexData[vertexPos + material.getNormalOffset() + 1] += normal.y/3;
				vertexData[vertexPos + material.getNormalOffset() + 2] += Math.abs(normal.z/3);
				
				vertexPos = (arrPos + width) * material.getByteStride();
				vertexData[vertexPos + material.getNormalOffset() + 0] += normal.x/3;
				vertexData[vertexPos + material.getNormalOffset() + 1] += normal.y/3;
				vertexData[vertexPos + material.getNormalOffset() + 2] += Math.abs(normal.z/3);
				
				vertexPos = (arrPos + 1) * material.getByteStride();
				vertexData[vertexPos + material.getNormalOffset() + 0] += normal.x/3;
				vertexData[vertexPos + material.getNormalOffset() + 1] += normal.y/3;
				vertexData[vertexPos + material.getNormalOffset() + 2] += Math.abs(normal.z/3);
				
				drawOrder[offset++] = (short) arrPos;
				drawOrder[offset++] = (short) (arrPos + 1);
				
				drawOrder[offset++] = (short) (arrPos + width);
				
				
				drawOrder[offset++] = (short) (arrPos + width);
				drawOrder[offset++] = (short) (arrPos + 1);
				drawOrder[offset++] = (short) (arrPos + width + 1);
			}
		}
		
		terrain.material = material;
		terrain.mesh = new Mesh(vertexData, drawOrder, width*height);
		
		return terrain;
		
	}

}
