package com.tyrfing.games.tyrlib3.graphics.materials;


import com.tyrfing.games.tyrlib3.graphics.renderer.IBlendable;
import com.tyrfing.games.tyrlib3.graphics.renderer.Material;
import com.tyrfing.games.tyrlib3.graphics.renderer.Mesh;
import com.tyrfing.games.tyrlib3.graphics.renderer.OpenGLRenderer;
import com.tyrfing.games.tyrlib3.graphics.renderer.Program;
import com.tyrfing.games.tyrlib3.graphics.renderer.ProgramManager;
import com.tyrfing.games.tyrlib3.graphics.renderer.TyrGL;
import com.tyrfing.games.tyrlib3.graphics.renderer.VertexLayout;
import com.tyrfing.games.tyrlib3.math.Vector3F;
import com.tyrfing.games.tyrlib3.util.Color;

/**
 * A material for simple per vertex colouring
 * @author Sascha
 *
 */

public class ColoredMaterial extends Material implements IBlendable {

	public static final int DEFAULT_COLOR_OFFSET = 3;
	public static final int DEFAULT_COLOR_SIZE = 4;
	
	private int colorHandle;
	private int alphaHandle;
	private Color[] colors;
	
	private int colorOffset;
	private int colorSize;

	private Color color = Color.WHITE.copy();
	
	public ColoredMaterial(Color[] colors) {
		
		this.colors = colors;
		
		program = ProgramManager.getInstance().getProgram("BASIC");
		colorHandle = TyrGL.glGetAttribLocation(program.handle, "a_Color");
		alphaHandle = TyrGL.glGetUniformLocation(program.handle, "u_Color");
		init(0,3, "u_MVPMatrix", "a_Position");
		
		addVertexInfo(VertexLayout.COLOR, DEFAULT_COLOR_OFFSET, DEFAULT_COLOR_SIZE);
		
		updateInfos();
	}
	
	public ColoredMaterial(Color[] colors, Color color) {
		
		this.colors = colors;
		this.color = color;
		
		program = ProgramManager.getInstance().getProgram("BASIC");
		colorHandle = TyrGL.glGetAttribLocation(program.handle, "a_Color");
		alphaHandle = TyrGL.glGetUniformLocation(program.handle, "u_Color");
		init(0,3, "u_MVPMatrix", "a_Position");
		
		addVertexInfo(VertexLayout.COLOR, DEFAULT_COLOR_OFFSET, DEFAULT_COLOR_SIZE);
	
		updateInfos();
	}
	
	private void updateInfos() {
		colorSize = getInfoSize(VertexLayout.COLOR);
		colorOffset = getInfoOffset(VertexLayout.COLOR);
	}
	
	public void render(Mesh mesh, float[] modelMatrix) {
		super.render(mesh, modelMatrix);
		
		if (mesh.isUsingVBO()) {		
		    // Pass in the color information
		    TyrGL.glVertexAttribPointer(colorHandle, colorSize, TyrGL.GL_FLOAT, false,
		    							 getByteStride() * OpenGLRenderer.BYTES_PER_FLOAT, colorOffset * OpenGLRenderer.BYTES_PER_FLOAT);
		 
		    TyrGL.glEnableVertexAttribArray(colorHandle);
		} else {
		    // Pass in the color information
		    mesh.getVertexBuffer().position(colorOffset);
		    TyrGL.glVertexAttribPointer(colorHandle,colorSize, TyrGL.GL_FLOAT, false,
		    							 getByteStride() * OpenGLRenderer.BYTES_PER_FLOAT, mesh.getVertexBuffer());
		 
		    TyrGL.glEnableVertexAttribArray(colorHandle);
		}
	    
	    TyrGL.glUniform4f(alphaHandle, color.r, color.g, color.b, color.a);
	    
	    Program.blendEnable(TyrGL.GL_SRC_ALPHA, TyrGL.GL_ONE_MINUS_SRC_ALPHA);
	}
	
	public Color[] getColors() {
		return colors;
	}
	
	public void setAlpha(float alpha) {
		color.a = alpha;
	}
	
	public float getAlpha() {
		return color.a;
	}
	
	public void setColor(Color color) {
		this.color = color;
	}
	
	public Color getColor() {
		return color;
	}
	
	public int getColorOffset() {
		return colorOffset;
	}
	
	/**
	 * Adds the colors to the vertex data.
	 * Repeats the colors if there are more vertices than colors
	 */
	
	public float[] createVertexData(Vector3F[] points, short[] drawOrder) {
		float[] vertexData = super.createVertexData(points, drawOrder);
		
		int colorOffset = getInfoOffset(VertexLayout.COLOR);
		
		int vertexCount = points.length;
		for (int i = 0; i < vertexCount; i++) {
			int pos = i * getByteStride();
			int color = i % colors.length;
			vertexData[pos + colorOffset + 0] = colors[color].r;
			vertexData[pos + colorOffset + 1] = colors[color].g;
			vertexData[pos + colorOffset + 2] = colors[color].b;
			vertexData[pos + colorOffset + 3] = colors[color].a;
		}
		
		return vertexData;
	}
	
	public float[] createVertexData(float[] vertexData, int startPos, float[] points) {
		int colorOffset = getInfoOffset(VertexLayout.COLOR);
		int byteStride = getByteStride();
		
		int countPoints = points.length/3;
		
		// Populate the vertex data
		for (int i = 0; i < countPoints; ++i) {
			int pos = startPos + byteStride * i;
			
			vertexData[pos + 0] = points[i*3+0];
			vertexData[pos + 1] = points[i*3+1];
			vertexData[pos + 2] = points[i*3+2];
			
			int color = i % colors.length;
			vertexData[pos + colorOffset + 0] = colors[color].r;
			vertexData[pos + colorOffset + 1] = colors[color].g;
			vertexData[pos + colorOffset + 2] = colors[color].b;
			vertexData[pos + colorOffset + 3] = colors[color].a;
		}
		
		
		return vertexData;
	}
}
