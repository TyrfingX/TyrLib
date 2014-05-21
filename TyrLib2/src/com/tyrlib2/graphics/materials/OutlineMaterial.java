package com.tyrlib2.graphics.materials;

import java.nio.FloatBuffer;

import com.tyrlib2.graphics.animation.Skeleton;
import com.tyrlib2.graphics.renderables.Entity;
import com.tyrlib2.graphics.renderer.Material;
import com.tyrlib2.graphics.renderer.OpenGLRenderer;
import com.tyrlib2.graphics.renderer.Program;
import com.tyrlib2.graphics.renderer.ProgramManager;
import com.tyrlib2.graphics.renderer.TyrGL;
import com.tyrlib2.util.Color;

/**
 * Material for 3D highlight of friends and enemies
 * @author Sascha
 *
 */

public class OutlineMaterial extends Material {
	
	/** Coloring **/
	private int colorHandle;
	private Color color;
	
	/** Per vertex normals of this object **/
	public static final int normalOffset = DefaultMaterial3.normalOffset;
	public static final int normalDataSize = DefaultMaterial3.normalDataSize;
	private int normalHandle;
	
	public static final int dataSize = DefaultMaterial3.dataSize;
	public static final int posOffset = DefaultMaterial3.posOffset;
	
	public static final String OUTLINE_PROGRAM = "OUTLINE";
	
	public OutlineMaterial(Color color) {
		this(color, false);
		setup(color);
	}
	
	public OutlineMaterial(Color color, boolean animated) {
		if (animated) {
			
			this.boneParam = "u_Bone";
			this.boneIndexParam = "a_BoneIndex";
			this.boneWeightParam = "a_BoneWeight";
			
			program = ProgramManager.getInstance().getProgram("ANIMATED_" + OUTLINE_PROGRAM);
		} else {
			program = ProgramManager.getInstance().getProgram(OUTLINE_PROGRAM);
		}
		
		
		setup(color);
	}
	
	
	protected void setup(Color color) {
		this.color = color;
		
		init(dataSize,posOffset,3, "u_MVPMatrix", "a_Position");
		
		normalHandle = TyrGL.glGetAttribLocation(program.handle, "a_Normal");
	    colorHandle = TyrGL.glGetUniformLocation(program.handle, "u_Color");
	}
	
	public void render(FloatBuffer vertexBuffer, float[] modelMatrix) {
	    super.render(vertexBuffer, modelMatrix);
		
		if (program.meshChange) {
			passMesh(vertexBuffer);
		}
		
		Program.blendEnable(TyrGL.GL_SRC_ALPHA, TyrGL.GL_SRC_ALPHA);
	}
	
	private void passMesh(FloatBuffer vertexBuffer)
	{
		
		// Pass in the color information
		TyrGL.glUniform4f(colorHandle, color.r, color.g, color.b, color.a);
	    
		if (TyrGL.GL_USE_VBO  == 1) {
	        TyrGL.glVertexAttribPointer(normalHandle, normalDataSize, TyrGL.GL_FLOAT, false, 
	        							strideBytes * OpenGLRenderer.BYTES_PER_FLOAT, normalOffset * OpenGLRenderer.BYTES_PER_FLOAT);
		} else {
		    // Pass in the normal information
		    vertexBuffer.position(normalOffset);
		    TyrGL.glVertexAttribPointer(normalHandle, normalDataSize, TyrGL.GL_FLOAT, false,
		    							 strideBytes * OpenGLRenderer.BYTES_PER_FLOAT, vertexBuffer);
		 
		   
		}
		
		 TyrGL.glEnableVertexAttribArray(normalHandle);
		

	}
	
	public Color getColors() {
		return color;
	}
	
	public int getNormalOffset() {
		return normalOffset;
	}
	
	public Material copy() {
		OutlineMaterial material = new OutlineMaterial(color);
		return material;
	}
	
}
