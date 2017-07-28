package com.tyrfing.games.tyrlib3.graphics.renderer;

import com.tyrfing.games.tyrlib3.graphics.materials.LightedMaterial;
import com.tyrfing.games.tyrlib3.graphics.scene.SceneManager;
import com.tyrfing.games.tyrlib3.graphics.scene.SceneNode;
import com.tyrfing.games.tyrlib3.math.AABB;
import com.tyrfing.games.tyrlib3.math.Matrix;
import com.tyrfing.games.tyrlib3.math.Vector3F;



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
	
	protected int renderMode = TyrGL.GL_TRIANGLES;

	private int insertionID;
	
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
	
	public Renderable(Material material, Vector3F[] points, short[] drawOrder) {
		init(material, points, drawOrder);
	}
	
	public void init(Material material, Vector3F[] points, short[] drawOrder) {
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
	
	
	/**
	 * Render this object
	 */
	public void renderShadow(float[] vpMatrix) {
		if (parent != null && modelMatrix != null && material.castShadow && material.visible) {
			Program program = SceneManager.getInstance().getRenderer().getShadowProgram(material.animated);
			program.use();
			
			int smvpHandle = TyrGL.glGetUniformLocation(program.handle, "u_SMVP");
			
	        // Apply the projection and view transformation
			Matrix.multiplyMM(mvpMatrix, 0, SceneManager.getInstance().getRenderer().getShadowVP(), 0, modelMatrix, 0);
			
	        // Combine the rotation matrix with the projection and camera view
			TyrGL.glUniformMatrix4fv(smvpHandle, 1, false, mvpMatrix, 0);
			
			if (program.mesh != mesh) {
			
				if (mesh.isUsingVBO()) {
					TyrGL.glBindBuffer(TyrGL.GL_ARRAY_BUFFER, mesh.getVBOBuffer());
				}
				
		        if (mesh.isUsingIBO()) {
		        	TyrGL.glBindBuffer(TyrGL.GL_ELEMENT_ARRAY_BUFFER, mesh.getIBOBuffer());
		        }    	
		        	
		        // Enable a handle to the triangle vertices
	        	TyrGL.glEnableVertexAttribArray(material.positionHandle);
	        	
	        	if (mesh.isUsingVBO()) {
		        	
		        	//mesh.vertexBuffer.position(material.positionOffest);
		        	
			        // Prepare the coordinate data
		        	TyrGL.glVertexAttribPointer(material.positionHandle, Material.DEFAULT_POSITION_SIZE,
		        								 TyrGL.GL_FLOAT, false,
			                                     material.getByteStride() * OpenGLRenderer.BYTES_PER_FLOAT, 
			                                     Material.DEFAULT_POSITION_OFFSET);
	        	} else {
	        		
		        	mesh.vertexBuffer.position(Material.DEFAULT_POSITION_OFFSET);
		        	
			        // Prepare the coordinate data
		        	TyrGL.glVertexAttribPointer(material.positionHandle,  Material.DEFAULT_POSITION_SIZE,
		        								TyrGL.GL_FLOAT, false,
			                                     material.getByteStride() * OpenGLRenderer.BYTES_PER_FLOAT, 
			                                     mesh.vertexBuffer);
	        	}
	        	
		        material.program.meshChange = true;
	        
			}
	        
			if (!material.backfaceCulling) {
				TyrGL.glDisable(TyrGL.GL_CULL_FACE);
			}
			
        	// Draw the triangle    	        
        	if (mesh.isUsingIBO()) {
	        	TyrGL.glDrawElements(renderMode, mesh.getIndexCount(), TyrGL.GL_UNSIGNED_SHORT, 0);	
	        } else {
	        	TyrGL.glDrawElements(renderMode, mesh.getIndexCount(), TyrGL.GL_UNSIGNED_SHORT, mesh.drawListBuffer);	
	        }
	        
        	/*
			if (mesh.isUsingVBO()) {
				TyrGL.glBindBuffer(TyrGL.GL_ARRAY_BUFFER, 0);
			}
			
	        if (mesh.isUsingIBO()) {
	        	TyrGL.glBindBuffer(TyrGL.GL_ELEMENT_ARRAY_BUFFER, 0);
	        }
	        */
	        
	        material.program.mesh = mesh;
	        material.program.meshChange = false;
	        
	        if (!material.backfaceCulling) {
	        	TyrGL.glEnable(TyrGL.GL_CULL_FACE);
	        }
		}
	}
	
	/**
	 * Render this object
	 */
	public void render(float[] vpMatrix) {
		TyrGL.glGetError();
		
		if (parent != null && modelMatrix != null && material.visible) {
	        
			Program.blendDisable();
			
			material.program.use();

	        // Apply the projection and view transformation
			Matrix.multiplyMM(mvpMatrix, 0, vpMatrix, 0, modelMatrix, 0);
			
	        // Combine the rotation matrix with the projection and camera view
			TyrGL.glUniformMatrix4fv(material.mvpMatrixHandle, 1, false, mvpMatrix, 0);
			
	        if (material.program.mesh != mesh) {
	        	
				if (mesh.isUsingVBO()) {
					TyrGL.glBindBuffer(TyrGL.GL_ARRAY_BUFFER, mesh.getVBOBuffer());
				}
				
		        if (mesh.isUsingIBO()) {
		        	TyrGL.glBindBuffer(TyrGL.GL_ELEMENT_ARRAY_BUFFER, mesh.getIBOBuffer());
		        }
	        	
		        // Enable a handle to the triangle vertices
	        	TyrGL.glEnableVertexAttribArray(material.positionHandle);
	        	
	        	if (mesh.isUsingVBO()) {
		        	
		        	TyrGL.glBindBuffer(TyrGL.GL_ARRAY_BUFFER, mesh.getVBOBuffer());
		        	
			        // Prepare the coordinate data
		        	TyrGL.glVertexAttribPointer(material.positionHandle, Material.DEFAULT_POSITION_SIZE,
		        								 TyrGL.GL_FLOAT, false,
			                                     material.getByteStride() * OpenGLRenderer.BYTES_PER_FLOAT, 
			                                     Material.DEFAULT_POSITION_OFFSET);
	        	} else {
	        		
		        	mesh.vertexBuffer.position(Material.DEFAULT_POSITION_OFFSET);
		        	
			        // Prepare the coordinate data
		        	TyrGL.glVertexAttribPointer(material.positionHandle, Material.DEFAULT_POSITION_SIZE,
		        								TyrGL.GL_FLOAT, false,
			                                     material.getByteStride() * OpenGLRenderer.BYTES_PER_FLOAT, 
			                                     mesh.vertexBuffer);
	        	}
	        	
		        material.program.meshChange = true;
	        
	        }

	        for (material.iteration = 0; material.iteration < material.repeatRender; ++material.iteration) {
	        
	        	if (material.iteration > 0 || material.nodepth) {
	        		TyrGL.glDepthMask( true );
	        	}
	        	
		        material.render(mesh, modelMatrix);   
		    	
		        if (material.lighted) {
		        	LightedMaterial lightedMaterial = (LightedMaterial) material;
		        	
		        	// First draw using depth buffer and no blending
	        		lightedMaterial.renderLight(0);
	    	        if (mesh.isUsingIBO()) {
	    	        	TyrGL.glDrawElements(renderMode, mesh.getIndexCount(), TyrGL.GL_UNSIGNED_SHORT, 0);	
	    	        } else {
	    	        	TyrGL.glDrawElements(renderMode, mesh.getIndexCount(), TyrGL.GL_UNSIGNED_SHORT, mesh.drawListBuffer);	
	    	        }
		        	
	    	        // Enable blending
		    		//TyrGL.glDisable(TyrGL.GL_DEPTH_TEST);
	    	        int lightCount = SceneManager.getInstance().getLightCount();
	        		if (lightCount > 1) {
	        			TyrGL.glEnable(TyrGL.GL_BLEND);
			    		
			        	for(int i = 1; i < lightCount; ++i) {
			        		lightedMaterial.renderLight(i);
			    	        if (mesh.isUsingIBO()) {
			    	        	TyrGL.glDrawElements(renderMode, mesh.getIndexCount(), TyrGL.GL_UNSIGNED_SHORT, 0);	
			    	        } else {
			    	        	TyrGL.glDrawElements(renderMode, mesh.getIndexCount(), TyrGL.GL_UNSIGNED_SHORT, mesh.drawListBuffer);	
			    	        }
			        	}
			    		// Enable blending
			        	TyrGL.glDisable(TyrGL.GL_BLEND);
	        		}
		    		//TyrGL.glEnable(TyrGL.GL_DEPTH_TEST);
		        } else {
		        	// Draw the triangle    
		        	if (mesh.isUsingIBO()) {
			        	TyrGL.glDrawElements(renderMode, mesh.getIndexCount(), TyrGL.GL_UNSIGNED_SHORT, 0);	
		        	} else {
			        	TyrGL.glDrawElements(renderMode, mesh.getIndexCount(), TyrGL.GL_UNSIGNED_SHORT, mesh.drawListBuffer);	
			        }
		        }
	        
	        }
	        
	        if (material.repeatRender > 1 || material.nodepth) {
	        	TyrGL.glDepthMask( true );
	        }
	
	        
	        // Disable vertex array
	        //TyrGL.glDisableVertexAttribArray(material.positionHandle);
	        /*
			if (mesh.isUsingVBO()) {
				TyrGL.glBindBuffer(TyrGL.GL_ARRAY_BUFFER, 0);
			}
			
	        if (mesh.isUsingIBO()) {
	        	TyrGL.glBindBuffer(TyrGL.GL_ELEMENT_ARRAY_BUFFER, 0);
	        }
	        */
	        
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
