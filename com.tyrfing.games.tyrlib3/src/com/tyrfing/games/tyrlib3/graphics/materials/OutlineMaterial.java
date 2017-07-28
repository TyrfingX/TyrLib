package com.tyrfing.games.tyrlib3.graphics.materials;

import java.nio.FloatBuffer;

import com.tyrfing.games.tyrlib3.graphics.renderer.Material;
import com.tyrfing.games.tyrlib3.graphics.renderer.Mesh;
import com.tyrfing.games.tyrlib3.graphics.renderer.OpenGLRenderer;
import com.tyrfing.games.tyrlib3.graphics.renderer.Program;
import com.tyrfing.games.tyrlib3.graphics.renderer.ProgramManager;
import com.tyrfing.games.tyrlib3.graphics.renderer.TyrGL;
import com.tyrfing.games.tyrlib3.graphics.renderer.VertexLayout;
import com.tyrfing.games.tyrlib3.util.Color;

/**
 * Material for 3D highlight of friends and enemies
 * @author Sascha
 *
 */

public class OutlineMaterial extends Material {
	
	/** Coloring **/
	private int colorHandle;
	private Color color;
	
	private int normalHandle;
	
	public static final int posOffset = DefaultMaterial3.posOffset;
	
	public static final String OUTLINE_PROGRAM = "OUTLINE";
	
	public OutlineMaterial(Color color) {
		this(color, false);
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
		
		init(posOffset,3, "u_MVPMatrix", "a_Position");
		
		normalHandle = TyrGL.glGetAttribLocation(program.handle, "a_Normal");
	    colorHandle = TyrGL.glGetUniformLocation(program.handle, "u_Color");
	}
	
	public void render(Mesh mesh, float[] modelMatrix) {
	    super.render(mesh, modelMatrix);
		
		if (program.meshChange) {
			passMesh(mesh);
		}
		
		Program.blendEnable(TyrGL.GL_SRC_ALPHA, TyrGL.GL_SRC_ALPHA);
	}
	
	private void passMesh(Mesh mesh)
	{
		FloatBuffer vertexBuffer = mesh.getVertexBuffer();
		// Pass in the color information
		TyrGL.glUniform4f(colorHandle, color.r, color.g, color.b, color.a);
	    
		if (mesh.isUsingVBO()) {
	        TyrGL.glVertexAttribPointer(normalHandle, getInfoSize(VertexLayout.NORMAL), TyrGL.GL_FLOAT, false, 
	        							getByteStride() * OpenGLRenderer.BYTES_PER_FLOAT, getInfoOffset(VertexLayout.NORMAL) * OpenGLRenderer.BYTES_PER_FLOAT);
		} else {
		    // Pass in the normal information
		    vertexBuffer.position(getInfoOffset(VertexLayout.NORMAL));
		    TyrGL.glVertexAttribPointer(normalHandle, getInfoSize(VertexLayout.NORMAL), TyrGL.GL_FLOAT, false,
		    		getByteStride() * OpenGLRenderer.BYTES_PER_FLOAT, vertexBuffer);
		 
		   
		}
		
		 TyrGL.glEnableVertexAttribArray(normalHandle);
		

	}
	
	public Color getColors() {
		return color;
	}
	
	public int getNormalOffset() {
		return getInfoOffset(VertexLayout.NORMAL);
	}
	
	public Material copy() {
		OutlineMaterial material = new OutlineMaterial(color);
		return material;
	}
	
}
