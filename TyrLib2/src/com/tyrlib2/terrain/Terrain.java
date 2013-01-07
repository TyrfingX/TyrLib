package com.tyrlib2.terrain;

import java.nio.ByteBuffer;

import android.opengl.GLES20;

import com.tyrlib2.materials.DefaultMaterial3;
import com.tyrlib2.math.Vector2;
import com.tyrlib2.math.Vector3;
import com.tyrlib2.renderer.Mesh;
import com.tyrlib2.renderer.Renderable;
import com.tyrlib2.renderer.Texture;
import com.tyrlib2.renderer.TextureManager;
import com.tyrlib2.renderer.Viewport;
import com.tyrlib2.scene.SceneManager;

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
		
		short width = 32;
		short height = 32;
		
		float stepX = size.x / width;
		float stepY = size.y / height;
		
		terrain.step = new Vector2(stepX, stepY);
		terrain.width = width;
		terrain.height = height;
		
		ByteBuffer pixelBuffer = ByteBuffer.allocate(4*width*height);
		pixelBuffer.position(0);
		
		//Generate a new FBO. It will contain your texture.
		int[] fb = new int[1];
		GLES20.glGenFramebuffers(1, fb, 0);
		GLES20.glBindFramebuffer(GLES20.GL_FRAMEBUFFER, fb[0]);
		
		//Create the texture 
		GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, texture.getHandle());

		//Bind the texture to your FBO
		GLES20.glFramebufferTexture2D(GLES20.GL_FRAMEBUFFER, GLES20.GL_COLOR_ATTACHMENT0, GLES20.GL_TEXTURE_2D, texture.getHandle(), 0);
		
		// set the viewport as the FBO won't be the same dimension as the screen
		GLES20.glViewport(0, 0, width, height);
		
		GLES20.glReadPixels(0, 0, width, height, GLES20.GL_RGBA, GLES20.GL_UNSIGNED_BYTE, pixelBuffer);
		
		//Bind your main FBO again
		GLES20.glBindFramebuffer(GLES20.GL_FRAMEBUFFER, 0);
		// set the viewport as the FBO won't be the same dimension as the screen
		Viewport viewport = SceneManager.getInstance().getViewport();
		GLES20.glViewport(0, 0, viewport.getWidth(), viewport.getHeight());
		
		
		float[] vertexData = new float[tileMaterial.getByteStride()*width*height];
		Vector3[] points = new Vector3[width*height];
		
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
				
				vertexData[vertexPos + tileMaterial.getUVOffset() + 0] = uvX;
				vertexData[vertexPos + tileMaterial.getUVOffset() + 1] = uvY;
				
				vertexData[vertexPos + tileMaterial.getColorOffset() + 0] = 1;
				vertexData[vertexPos + tileMaterial.getColorOffset() + 1] = 1;
				vertexData[vertexPos + tileMaterial.getColorOffset() + 2] = 1;
				vertexData[vertexPos + tileMaterial.getColorOffset() + 3] = 1;
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
				drawOrder[offset++] = (short) (arrPos + 1);
				drawOrder[offset++] = (short) (arrPos + width + 1);
			}
		}
		
		terrain.material = tileMaterial;
		terrain.mesh = new Mesh(vertexData, drawOrder, width*height);
		
		return terrain;
		
	}

}
