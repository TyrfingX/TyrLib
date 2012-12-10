package com.tyrlib2.renderer;

import android.opengl.GLES20;
import android.opengl.Matrix;

import com.tyrlib2.math.Vector3;
import com.tyrlib2.scene.SceneNode;
import com.tyrlib2.scene.SceneObject;



/**
 * Basic class for rendering purposes.
 * @author Sascha
 *
 */

public class Renderable extends SceneObject implements IRenderable {
	
	/** The Mesh of this renderable **/
	protected Mesh mesh;
	
	/** The material of this renderable **/
	protected Material material;
	
	/** Transforms model space to world space, taken from the parent scene node **/
	protected float[] modelMatrix;
	
	/** Allocate storage for the final combined matrix. This will be passed into the shader program. */
	private float[] mvpMatrix = new float[16];
	
	public Renderable(Mesh mesh, Material material) {
		this();
		this.mesh = mesh;
		this.material = material;
	}
	
	public Renderable() {
	}
	
	public Renderable(Material material, Vector3[] points, short[] drawOrder) {
		init(material, points, drawOrder);
	}
	
	public void init(Material material, Vector3[] points, short[] drawOrder) {
		this.material = material;
		float[] vertexData = material.createVertexData(points);
		mesh = new Mesh(vertexData, drawOrder);
	}
	
	public void setMesh(Mesh mesh) {
		this.mesh = mesh;
	}
	
	public void setMaterial(Material material) {
		this.material = material;
	}
	
	public void attachTo(SceneNode node)  {
		super.attachTo(node);
		modelMatrix = node.getModelMatrix();
	}
	
	/**
	 * Render this object
	 */
	public void render(float[] vpMatrix) {

		if (modelMatrix != null) {
	        
			material.program.use();
	
			mesh.vertexBuffer.position(material.positionOffest);
	
	        // Enable a handle to the triangle vertices
	        GLES20.glEnableVertexAttribArray(material.positionHandle);
	
	        // Prepare the coordinate data
	        GLES20.glVertexAttribPointer(material.positionHandle, material.positionDataSize,
	                                     GLES20.GL_FLOAT, false,
	                                     material.strideBytes * OpenGLRenderer.BYTES_PER_FLOAT, 
	                                     mesh.vertexBuffer);
	
			material.render(mesh.vertexBuffer);

	        // Apply the projection and view transformation
			Matrix.multiplyMM(mvpMatrix, 0, vpMatrix, 0, modelMatrix, 0);
			
	        // Combine the rotation matrix with the projection and camera view
	        GLES20.glUniformMatrix4fv(material.mvpMatrixHandle, 1, false, mvpMatrix, 0);
	
	        // Draw the triangle
	        //GLES20.glDrawArrays(GLES20.GL_TRIANGLES, 0, mesh.vertexData.length / material.strideBytes);
	        GLES20.glDrawElements(GLES20.GL_TRIANGLES, mesh.drawOrder.length, GLES20.GL_UNSIGNED_SHORT, mesh.drawListBuffer);
	        // Disable vertex array
	        GLES20.glDisableVertexAttribArray(material.positionHandle);
		}

	}
	
	public Material getMaterial() {
		return material;
	}
}
