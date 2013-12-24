package com.tyrlib2.graphics.renderer;

import android.opengl.GLES20;
import android.opengl.Matrix;

import com.tyrlib2.graphics.materials.LightedMaterial;
import com.tyrlib2.graphics.scene.SceneManager;
import com.tyrlib2.graphics.scene.SceneNode;
import com.tyrlib2.math.AABB;
import com.tyrlib2.math.Vector3;



/**
 * Basic class for rendering purposes.
 * @author Sascha
 *
 */

public class Renderable extends BoundedRenderable {
	
	/** The Mesh of this renderable **/
	protected Mesh mesh;
	
	/** The material of this renderable **/
	protected Material material;
	
	/** Transforms model space to world space, taken from the parent scene node **/
	protected float[] modelMatrix;
	
	/** Allocate storage for the final combined matrix. This will be passed into the shader program. */
	protected static float[] mvpMatrix = new float[16];
	
	public Renderable(Mesh mesh, Material material) {
		this();
		this.mesh = mesh;
		this.material = material;
	}
	
	public Renderable() {
	}
	
	public Renderable(Material material, Mesh mesh) {
		this.material = material;
		this.mesh = mesh;
	}
	
	public Renderable(Material material, Vector3[] points, short[] drawOrder) {
		init(material, points, drawOrder);
	}
	
	public void init(Material material, Vector3[] points, short[] drawOrder) {
		this.material = material;
		float[] vertexData = material.createVertexData(points, drawOrder);
		mesh = new Mesh(vertexData, drawOrder, vertexData.length / material.strideBytes);
	}
	
	public void setMesh(Mesh mesh) {
		this.mesh = mesh;
	}
	
	public Mesh getMesh() {
		return mesh;
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

		if (parent != null && modelMatrix != null) {
	        
			Program.blendDisable();
			
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
	        
	        if (material.lighted) {
	    		

	        	LightedMaterial lightedMaterial = (LightedMaterial) material;
	        	
	        	// First draw using depth buffer and no blending
        		lightedMaterial.renderLight(0);
        		
        		GLES20.glDrawElements(GLES20.GL_TRIANGLES, mesh.getIndexCount(), GLES20.GL_UNSIGNED_SHORT, mesh.drawListBuffer);	
        	
	        	
	        	// Enable blending
	    		//GLES20.glDisable(GLES20.GL_DEPTH_TEST);
        		if (SceneManager.getInstance().getLightCount() > 1) {
		    		GLES20.glEnable(GLES20.GL_BLEND);
		    		
		        	for(int i = 1; i < SceneManager.getInstance().getLightCount(); ++i) {
		        		lightedMaterial.renderLight(i);
		        		GLES20.glDrawElements(GLES20.GL_TRIANGLES, mesh.getIndexCount(), GLES20.GL_UNSIGNED_SHORT, mesh.drawListBuffer);	
		        	}
		        	
		    		// Enable blending
		    		GLES20.glDisable(GLES20.GL_BLEND);
        		}
	    		//GLES20.glEnable(GLES20.GL_DEPTH_TEST);
	        } else {
	        	// Draw the triangle
		        GLES20.glDrawElements(GLES20.GL_TRIANGLES, mesh.getIndexCount(), GLES20.GL_UNSIGNED_SHORT, mesh.drawListBuffer);	
	        }
	
	        
	        // Disable vertex array
	        //GLES20.glDisableVertexAttribArray(material.positionHandle);
	        
	        material.program.mesh = mesh;
	        material.program.meshChange = false;
		}

	}
	
	public Material getMaterial() {
		return material;
	}

	@Override
	protected AABB createUntransformedBoundingBox() {
		return mesh.getBoundingBox();
	}
	
	public Renderable createShallowCopy() {
		return new Renderable(material, mesh);
	}

}
