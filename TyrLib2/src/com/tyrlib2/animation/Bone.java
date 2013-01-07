package com.tyrlib2.animation;

import android.opengl.Matrix;

import com.tyrlib2.math.Quaternion;
import com.tyrlib2.math.Vector3;
import com.tyrlib2.scene.SceneNode;

/**
 * Represents a single joint of a skeleton. Further bones, SceneNode or vertices may be bound to
 * this bone.
 * @author Sascha
 *
 */

public class Bone extends SceneNode {
	protected String name;
	protected Quaternion initRot;
	protected Vector3 initPos;
	protected float[] bindPos = new float[16];
	protected float[] localBindPos = new float[16];
	protected float[] inverseBindPos = new float[16];

	
	public Bone(String name) {
		this.name = name;
		Matrix.setIdentityM(bindPos, 0);
		Matrix.setIdentityM(localBindPos, 0);
	}
	
	public String getName() {
		return name;
	}
	
	public void setPose(Vector3 pos, Quaternion rot) {
		this.initPos = pos;
		this.initRot = rot;
		
		float[] rotation = initRot.toMatrix();
		float[] translation = new float[16];
		Matrix.setIdentityM(translation, 0);
		
		Matrix.multiplyMM(localBindPos, 0, rotation, 0, bindPos, 0);
		Matrix.translateM(translation, 0, initPos.x , initPos.y , initPos.z);
		Matrix.multiplyMM(localBindPos, 0, translation, 0, localBindPos, 0);
		Matrix.multiplyMM(bindPos, 0, localBindPos, 0, bindPos, 0);
		
		if (parent instanceof Bone) {
			Bone parentBone = (Bone) parent;	
			Matrix.multiplyMM(bindPos, 0, parentBone.bindPos, 0, localBindPos, 0);
		} 
		
		Matrix.invertM(inverseBindPos, 0,bindPos, 0);
		
	}
	
	@Override
	public void updateAll(Vector3 parentPos, Quaternion parentRot, Vector3 parentScale, float[] parentTransform) {
		
		Matrix.setIdentityM(modelMatrix, 0);
		
		
		float[] rotation = initRot.multiply(rot).toMatrix();
		float[] translation = new float[16];
		Matrix.setIdentityM(translation, 0);
		
		Matrix.setIdentityM(modelMatrix, 0);
		Matrix.multiplyMM(modelMatrix, 0, rotation, 0, modelMatrix, 0);
		Matrix.translateM(translation, 0, initPos.x + pos.x, initPos.y + pos.y, initPos.z + pos.z);
		Matrix.multiplyMM(modelMatrix, 0, translation, 0, modelMatrix, 0);

		Matrix.multiplyMM(modelMatrix, 0, parentTransform, 0, modelMatrix, 0);
		
		
		for (int i = 0; i < children.size(); ++i) {	
			children.get(i).updateAll(absolutePos, absoluteRot, absoluteScale, modelMatrix);
		}
		
		float[] animMatrix = new float[16];
		Matrix.setIdentityM(animMatrix, 0);
		Matrix.multiplyMM(animMatrix, 0,  inverseBindPos, 0, animMatrix, 0);
		Matrix.multiplyMM(animMatrix, 0,  modelMatrix, 0, animMatrix, 0);
		modelMatrix = animMatrix;
		
	}
}
