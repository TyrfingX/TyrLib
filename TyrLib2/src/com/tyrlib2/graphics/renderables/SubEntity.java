package com.tyrlib2.graphics.renderables;

import java.nio.FloatBuffer;

import android.opengl.GLES20;

import com.tyrlib2.graphics.renderer.Material;
import com.tyrlib2.graphics.renderer.Mesh;
import com.tyrlib2.graphics.renderer.OpenGLRenderer;
import com.tyrlib2.graphics.renderer.Program;
import com.tyrlib2.graphics.renderer.Renderable;
import com.tyrlib2.graphics.renderer.TyrGL;

/**
 * This class represents a SubEntitiy, a part of an Entity. A SubEntity is bascially a named
 * renderable with extended functionality.
 * @author Sascha
 *
 */

public class SubEntity extends Renderable {

	protected String name;
	
	public SubEntity(String name, Mesh mesh, Material material) {
		this.name = name;
		this.mesh = mesh;
		this.material = material;
	}
	
	public void render(float[] vpMatrix, float[] skeletonBuffer, int bones) {
		
		if (skeletonBuffer != null && skeletonBuffer.length > 0) {
		
			Program program = material.getProgram();
			program.use();
			
			int boneHandle = TyrGL.glGetUniformLocation(program.handle, material.getBoneParam());
			int boneIndexHandle = TyrGL.glGetAttribLocation(program.handle, material.getBoneIndexParam());
			int boneWeightHandle = TyrGL.glGetAttribLocation(program.handle, material.getBoneWeightParam());
			
	        // Prepare the skeleton data
			TyrGL.glUniformMatrix4fv(boneHandle, bones, false, skeletonBuffer, 0);
	        
	        FloatBuffer boneBuffer = mesh.getBoneBuffer();
	        TyrGL.glEnableVertexAttribArray(boneIndexHandle);
	        boneBuffer.position(Mesh.BONE_INDEX_OFFSET);
	        TyrGL.glVertexAttribPointer(boneIndexHandle, Mesh.MAX_BONES_PER_VERTEX,
	        		TyrGL.GL_FLOAT, false,
						                Mesh.BONE_BYTE_STRIDE * OpenGLRenderer.BYTES_PER_FLOAT, 
						                mesh.getBoneBuffer());
	        
	        
	        boneBuffer.position(Mesh.BONE_WEIGHT_OFFSET);
	        TyrGL.glEnableVertexAttribArray(boneWeightHandle);
	        TyrGL.glVertexAttribPointer(boneWeightHandle, Mesh.MAX_BONES_PER_VERTEX,
	        		TyrGL.GL_FLOAT, false,
						                Mesh.BONE_BYTE_STRIDE * OpenGLRenderer.BYTES_PER_FLOAT, 
						                mesh.getBoneBuffer());
	        
		}
		super.render(vpMatrix);
	}

}
