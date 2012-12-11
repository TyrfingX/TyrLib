package com.tyrlib2.lighting;

import android.opengl.GLES20;
import android.opengl.Matrix;

import com.tyrlib2.materials.PointLightMaterial;
import com.tyrlib2.renderer.IRenderable;
import com.tyrlib2.renderer.Material;
import com.tyrlib2.scene.SceneNode;

/**
 * Represents a point light. Emits light from the assigned position.
 * @author Sascha
 *
 */

public class PointLight extends Light implements IRenderable {
	
	private Material material;
	private float[] mvpMatrix = new float[16];
	private float[] modelMatrix;
	
	public PointLight() {
		super(Type.POINT_LIGHT);
		
		if (material == null) {
			material = new PointLightMaterial();
		}
	}

	@Override
	public void render(float[] vpMatrix) {
        
		material.getProgram().use();
		
		// Pass in the position.
		GLES20.glVertexAttrib3f(material.getPositionHandle(), modelSpaceVector[0], modelSpaceVector[1], modelSpaceVector[2]);

		// Since we are not using a buffer object, disable vertex arrays for this attribute.
        GLES20.glDisableVertexAttribArray(material.getPositionHandle());  
		
        // Apply the projection and view transformation
		Matrix.multiplyMM(mvpMatrix, 0, vpMatrix, 0, modelMatrix, 0);
        
		// Pass in the transformation matrix.
		GLES20.glUniformMatrix4fv(material.getMVPMatrixHandle(), 1, false, mvpMatrix, 0);
		
		// Draw the point.
		GLES20.glDrawArrays(GLES20.GL_POINTS, 0, 1);
	}

	public void attachTo(SceneNode node)  {
		super.attachTo(node);
		modelMatrix = node.getModelMatrix();
	}
	
}
