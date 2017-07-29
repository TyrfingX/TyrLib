package com.tyrfing.games.tyrlib3.view.graphics.lighting;

import com.tyrfing.games.tyrlib3.model.graphics.scene.SceneNode;
import com.tyrfing.games.tyrlib3.model.math.Matrix;
import com.tyrfing.games.tyrlib3.view.graphics.TyrGL;
import com.tyrfing.games.tyrlib3.view.graphics.materials.Material;
import com.tyrfing.games.tyrlib3.view.graphics.materials.PointLightMaterial;
import com.tyrfing.games.tyrlib3.view.graphics.renderables.IRenderable;

/**
 * Represents a point light. Emits light from the assigned position.
 * @author Sascha
 *
 */

public class PointLight extends Light implements IRenderable {
	
	/** Holds the light position in model space **/
	protected final float[] modelSpaceVector =  { 0,0,0,1 };
	
	/** Holds the transformed position of the light in world space (model) **/
	protected final float[] worldSpaceVector = new float[4];
	
	/** Holds the transformed position of the light in eye space (model*view) **/
	protected final float[] eyeSpaceVector = new float[4];
	
	private Material material;
	private float[] mvpMatrix = new float[16];
	private float[] modelMatrix;

	private int insertionID;
	
	public PointLight() {
		super(Type.POINT_LIGHT);
		
		if (material == null) {
			material = new PointLightMaterial();
		}
	}

	@Override
	public void renderShadow(float[] vpMatrix) {
		
	}
	
	@Override
	public void render(float[] vpMatrix) {
        
		material.getProgram().use();
		
		// Pass in the position.
		TyrGL.glVertexAttrib3f(material.getPositionHandle(), modelSpaceVector[0], modelSpaceVector[1], modelSpaceVector[2]);

		// Since we are not using a buffer object, disable vertex arrays for this attribute.
		TyrGL.glDisableVertexAttribArray(material.getPositionHandle());  
		
        // Apply the projection and view transformation
		Matrix.multiplyMM(mvpMatrix, 0, vpMatrix, 0, modelMatrix, 0);
        
		// Pass in the transformation matrix.
		TyrGL.glUniformMatrix4fv(material.getMVPMatrixHandle(), 1, false, mvpMatrix, 0);
		
		// Draw the point.
		TyrGL.glDrawArrays(TyrGL.GL_POINTS, 0, 1);
	}

	public void attachTo(SceneNode node)  {
		super.attachTo(node);
		modelMatrix = node.getModelMatrix();
	}
	
	public void update(float[] viewMatrix) {
		if (parent != null) {
			Matrix.multiplyMV(worldSpaceVector, 0, parent.getModelMatrix(), 0, modelSpaceVector, 0);
			Matrix.multiplyMV(eyeSpaceVector, 0, viewMatrix, 0, worldSpaceVector, 0);	
		}
	}
	
	public float[] getLightVector() {
		return eyeSpaceVector;
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
		// TODO Auto-generated method stub
		
	}
	
}
