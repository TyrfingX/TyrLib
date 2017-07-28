package com.tyrlib2.graphics.renderer;

import java.nio.ShortBuffer;

import com.tyrlib2.graphics.scene.SceneNode;
import com.tyrlib2.graphics.scene.SceneObject;
import com.tyrlib2.math.Matrix;
import com.tyrlib2.math.Vector3;

/**
 * A basic renderable 2D object
 * @author Sascha
 *
 */

public class Renderable2 extends SceneObject implements IRenderable {
	
	protected int drawOrderLength;
	protected ShortBuffer drawOrderBuffer;
	
	/** The Mesh of this renderable **/
	protected Mesh mesh;
	
	/** The material of this renderable **/
	protected Material material;
	
	/** Transforms model space to world space, taken from the parent scene node **/
	public float[] modelMatrix;
	
	/** Allocate storage for the final combined matrix. This will be passed into the shader program. */
	protected static float[] mvpMatrix = new float[16];
	
	protected int renderMode = TyrGL.GL_TRIANGLES;
	private int insertionID;
	
	public Renderable2() {
	}
	
	public Renderable2(Material material, Mesh mesh) {
		this.material = material;
		this.mesh = mesh;
	}
	
	public Renderable2(Material material, Vector3[] points, short[] drawOrder) {
		init(material, points, drawOrder);
	}
	
	public void init(Material material, Vector3[] points, short[] drawOrder) {
		this.material = material;
		float[] vertexData = material.createVertexData(points, drawOrder);
		mesh = new Mesh(vertexData, drawOrder, vertexData.length / material.getByteStride());
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
	
	public Material getMaterial() {
		return material;
	}
	
	public Renderable2 createShallowCopy() {
		return new Renderable2(material, mesh);
	}
	
	@Override
	public void renderShadow(float[] vpMatrix) {
		
	}
	
	@Override
	public void render(float[] vpMatrix) {

		if (modelMatrix != null) {
	        
			material.program.use();

	        // Apply the projection and view transformation
			Matrix.multiplyMM(mvpMatrix, 0, vpMatrix, 0, modelMatrix, 0);
			
	        // Combine the rotation matrix with the projection and camera view
			TyrGL.glUniformMatrix4fv(material.mvpMatrixHandle, 1, false, mvpMatrix, 0);
	        
			if (mesh.isUsingVBO()) {
				TyrGL.glBindBuffer(TyrGL.GL_ARRAY_BUFFER, mesh.getVBOBuffer());
			}
			
	        if (mesh.isUsingIBO()) {
	        	TyrGL.glBindBuffer(TyrGL.GL_ELEMENT_ARRAY_BUFFER, mesh.getIBOBuffer());
	        }
			
	        if (material.program.mesh != mesh) {        
			
		        // Enable a handle to the triangle vertices
	        	TyrGL.glEnableVertexAttribArray(material.positionHandle);
	        	
	        	if (mesh.isUsingVBO()) {
	        		
			        // Prepare the coordinate data
			        TyrGL.glVertexAttribPointer(material.positionHandle,  Material.DEFAULT_POSITION_SIZE,
			        							TyrGL.GL_FLOAT, false,
			                                     material.getByteStride() * OpenGLRenderer.BYTES_PER_FLOAT, 
			                                     Material.DEFAULT_POSITION_OFFSET * OpenGLRenderer.BYTES_PER_FLOAT);
		        
	        	} else {
	        		
		        	mesh.vertexBuffer.position(Material.DEFAULT_POSITION_OFFSET);
			
			        // Prepare the coordinate data
			        TyrGL.glVertexAttribPointer(material.positionHandle,Material.DEFAULT_POSITION_SIZE,
			        		TyrGL.GL_FLOAT, false,
			                                     material.getByteStride() * OpenGLRenderer.BYTES_PER_FLOAT, 
			                                     mesh.vertexBuffer);
	        	}
		        
		        material.program.meshChange = true;
	        }
	        
	        material.render(mesh, modelMatrix);
	        
        	// Draw the triangle
	        if (mesh.isUsingIBO()) {
	        	TyrGL.glDrawElements(renderMode, drawOrderLength, TyrGL.GL_UNSIGNED_SHORT, 0);	
	        } else {
	        	TyrGL.glDrawElements(renderMode, drawOrderLength, TyrGL.GL_UNSIGNED_SHORT, drawOrderBuffer);	
	        }
	        
			if (mesh.isUsingVBO()) {
				TyrGL.glBindBuffer(TyrGL.GL_ARRAY_BUFFER, 0);
			}
			
	        if (mesh.isUsingIBO()) {
	        	TyrGL.glBindBuffer(TyrGL.GL_ELEMENT_ARRAY_BUFFER, 0);
	        }
	        
	        material.program.mesh = mesh;
	        material.program.meshChange = false;
	        
	        // Disable vertex array
	        //TyrGL.glDisableVertexAttribArray(material.positionHandle);
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
	public void setInsertionID(int id) {
		this.insertionID = id;
	}

	@Override
	public int getInsertionID() {
		return insertionID;
	}
	
	@Override
	public void destroy() {
		mesh.destroy();
		detach();
	}
}
