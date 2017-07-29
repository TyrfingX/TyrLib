package com.tyrfing.games.tyrlib3.view.graphics.materials;

import java.nio.FloatBuffer;

import com.tyrfing.games.tyrlib3.model.game.Color;
import com.tyrfing.games.tyrlib3.model.graphics.VertexLayout;
import com.tyrfing.games.tyrlib3.model.math.Vector3F;
import com.tyrfing.games.tyrlib3.view.graphics.ProgramManager;
import com.tyrfing.games.tyrlib3.view.graphics.TyrGL;
import com.tyrfing.games.tyrlib3.view.graphics.lighting.LightingType;
import com.tyrfing.games.tyrlib3.view.graphics.renderer.OpenGLRenderer;

public class ColoredLightedMaterial extends LightedMaterial {
	
	public static final int DEFAULT_NORMAL_OFFSET = 3;
	public static final int DEFAULT_NORMAL_SIZE = 3;
	
	public static final int DEFAULT_COLOR_OFFSET = 6;
	public static final int DEFAULT_COLOR_SIZE = 4;
	
	private int colorHandle;
	private Color[] colors;
	
	private int normalHandle;

	
	public static final String PER_VERTEX_PROGRAM_NAME = "BASIC_LIGHTED";
	public static final String PER_PIXEL_PROGRAM_NAME = "BASIC_PER_PIXEL_LIGHTED";
	
	public ColoredLightedMaterial(Color[] colors, LightingType type) {
		
		this.colors = colors;
		
		switch (type) {
		case PER_PIXEL:
			program = ProgramManager.getInstance().getProgram(PER_PIXEL_PROGRAM_NAME);
			break;
		case PER_VERTEX:
			program = ProgramManager.getInstance().getProgram(PER_VERTEX_PROGRAM_NAME);
			break;
		}
		
		
		init(0,3, "u_MVPMatrix", "a_Position");
		
		addVertexInfo(VertexLayout.COLOR, DEFAULT_COLOR_OFFSET, DEFAULT_COLOR_SIZE);
		addVertexInfo(VertexLayout.NORMAL, DEFAULT_NORMAL_OFFSET, DEFAULT_NORMAL_SIZE);
		
		colorHandle = TyrGL.glGetAttribLocation(program.handle, "a_Color");
		normalHandle = TyrGL.glGetAttribLocation(program.handle, "a_Normal");
		lightPosHandle = TyrGL.glGetUniformLocation(program.handle, "u_LightPos");
	}
	
	public void render(FloatBuffer vertexBuffer, float[] modelMatrix) {
	    // Pass in the color information
	    vertexBuffer.position(getInfoOffset(VertexLayout.COLOR));
	    TyrGL.glVertexAttribPointer(colorHandle, getInfoSize(VertexLayout.COLOR), TyrGL.GL_FLOAT, false,
	    							 getByteStride() * OpenGLRenderer.BYTES_PER_FLOAT, vertexBuffer);
	 
	    TyrGL.glEnableVertexAttribArray(colorHandle);
	    
	    // Pass in the normal information
	    vertexBuffer.position(getInfoOffset(VertexLayout.NORMAL));
	    TyrGL.glVertexAttribPointer(normalHandle, getInfoSize(VertexLayout.NORMAL), TyrGL.GL_FLOAT, false,
	    							getByteStride() * OpenGLRenderer.BYTES_PER_FLOAT, vertexBuffer);
	 
	    TyrGL.glEnableVertexAttribArray(normalHandle);

	}
	
	public Color[] getColors() {
		return colors;
	}
	
	/**
	 * Adds the colors to the vertex data.
	 * Repeats the colors if there are more vertices than colors
	 */
	
	public float[] createVertexData(Vector3F[] points, short[] drawOrder) {
		float[] vertexData = super.createVertexData(points, drawOrder);
		
		int vertexCount = points.length;
		
		Vector3F[] normals = new Vector3F[points.length];
		for (int i = 0; i < vertexCount; ++i) {
			normals[i] = new Vector3F();

			for (int j = 0; j < drawOrder.length; j += 3) {
				if (drawOrder[j] == i || drawOrder[j+1] == i || drawOrder[j + 2] == i) {
					Vector3F u = points[drawOrder[j]].vectorTo(points[drawOrder[j+1]]);
					Vector3F v = points[drawOrder[j+1]].vectorTo(points[drawOrder[j+2]]);
					Vector3F normal = u.cross(v);
					normal.normalize();
					
					normals[i] = normals[i].add(normal);
				}
			}
			normals[i].normalize();
		}
		
		int colorOffset = getInfoOffset(VertexLayout.COLOR);
		int normalOffset = getInfoOffset(VertexLayout.NORMAL);
		
		for (int i = 0; i < vertexCount; i++) {
			
			int pos = i * getByteStride();
			int color = i % colors.length;
			vertexData[pos + colorOffset + 0] = colors[color].r;
			vertexData[pos + colorOffset + 1] = colors[color].g;
			vertexData[pos + colorOffset + 2] = colors[color].b;
			vertexData[pos + colorOffset + 3] = colors[color].a;
			
			vertexData[pos + normalOffset + 0] = normals[i].x;
			vertexData[pos + normalOffset + 1] = normals[i].y;
			vertexData[pos + normalOffset + 2] = normals[i].z;
			
		}
		
		return vertexData;
	}

}
