package com.tyrlib2.graphics.renderer;

import java.nio.ShortBuffer;

import android.opengl.GLES20;

import com.tyrlib2.math.AABB;
import com.tyrlib2.math.Matrix;

/**
 * A basic renderable 2D object
 * @author Sascha
 *
 */

public abstract class Renderable2 extends Renderable {
	
	protected int drawOrderLength;
	protected ShortBuffer drawOrderBuffer;
	
	@Override
	public void render(float[] vpMatrix) {

		if (modelMatrix != null) {
	        
			material.program.use();

	        // Apply the projection and view transformation
			Matrix.multiplyMM(mvpMatrix, 0, vpMatrix, 0, modelMatrix, 0);
			
	        // Combine the rotation matrix with the projection and camera view
	        GLES20.glUniformMatrix4fv(material.mvpMatrixHandle, 1, false, mvpMatrix, 0);
	        
	        if (material.program.mesh != mesh) {        
			
		        // Enable a handle to the triangle vertices
		        GLES20.glEnableVertexAttribArray(material.positionHandle);
	        	
	        	mesh.vertexBuffer.position(material.positionOffest);
		
		        // Prepare the coordinate data
		        GLES20.glVertexAttribPointer(material.positionHandle, material.positionDataSize,
		                                     GLES20.GL_FLOAT, false,
		                                     material.strideBytes * OpenGLRenderer.BYTES_PER_FLOAT, 
		                                     mesh.vertexBuffer);
		        
		        material.program.meshChange = true;
	        }
	        
	        material.render(mesh.vertexBuffer, modelMatrix);

        	// Draw the triangle
	        GLES20.glDrawElements(renderMode, drawOrderLength, GLES20.GL_UNSIGNED_SHORT, drawOrderBuffer);	

	        material.program.mesh = mesh;
	        material.program.meshChange = false;
	        
	        // Disable vertex array
	        //GLES20.glDisableVertexAttribArray(material.positionHandle);
		}

	}
	
	public void setAlpha(float alpha) {
		if (material instanceof IBlendable) {
			IBlendable mat = (IBlendable) material;
			mat.setAlpha(alpha);
		}
	}
	
	public float getAlpha() {
		if (material instanceof IBlendable) {
			IBlendable mat = (IBlendable) material;
			return mat.getAlpha();
		}
		
		return 1;
	}
	
	@Override
	public AABB getBoundingBox() {
		return null;
	}
}
