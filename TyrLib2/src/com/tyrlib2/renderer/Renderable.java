package com.tyrlib2.renderer;

import android.opengl.GLES20;
import android.opengl.Matrix;



/**
 * Basic interface for all renderable objects.
 * @author Sascha
 *
 */

public abstract class Renderable {
	
	/** The Mesh of this renderable **/
	protected Mesh mesh;
	
	/** The material of this renderable **/
	protected Material material;
	
	/** Transforms model space to world space **/
	protected float[] modelMatrix = new float[16];
	
	/** Allocate storage for the final combined matrix. This will be passed into the shader program. */
	private float[] mvpMatrix = new float[16];
	
	public Renderable(Mesh mesh, Material material) {
		this();
		this.mesh = mesh;
		this.material = material;
	}
	
	public Renderable() {
		Matrix.setIdentityM(modelMatrix, 0);
	}
	
	public void setMesh(Mesh mesh) {
		this.mesh = mesh;
	}
	
	public void setMaterial(Material material) {
		this.material = material;
	}
	
	/**
	 * Render this object
	 */
	protected void render(float[] vpMatrix) {
		Matrix.multiplyMM(mvpMatrix, 0, vpMatrix, 0, modelMatrix, 0);
		
		material.program.use();
		
		mesh.vertexBuffer.position(material.positionOffest);

		GLES20.glVertexAttribPointer(material.positionHandle, material.positionDataSize, GLES20.GL_FLOAT, 
									false, material.strideBytes * OpenGLRenderer.BYTES_PER_FLOAT, mesh.vertexBuffer);
	    GLES20.glUniformMatrix4fv(material.mvpMatrixHandle, 1, false, mvpMatrix, 0);
		
		material.render(mesh.vertexBuffer);
		GLES20.glDrawArrays(GLES20.GL_TRIANGLES, 0, mesh.vertexData.length / material.strideBytes);
	}
}
