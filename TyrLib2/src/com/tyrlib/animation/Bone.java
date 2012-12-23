package com.tyrlib.animation;

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
	protected float[] transform = new float[16];
	protected float[] inverseTransform = new float[16];
	
	public Bone(String name) {
		Matrix.setIdentityM(transform, 0);
		Matrix.setIdentityM(inverseTransform, 0);
		this.name = name;
	}
	
	public String getName() {
		return name;
	}
	
	public void setPose(Vector3 pos, Quaternion rot) {
		this.initPos = pos;
		this.initRot = rot;
		Matrix.translateM(transform, 0, initPos.x, initPos.y, initPos.z);
		Matrix.rotateM(transform, 0, initRot.angle, initRot.rotX, initRot.rotY, initRot.rotZ);
		
		
		if (parent instanceof Bone) {
			Matrix.setIdentityM(modelMatrix, 0);
			Bone parentBone = (Bone) parent;
			Matrix.multiplyMM(transform, 0, parentBone.transform, 0, transform, 0);
			Matrix.invertM(inverseTransform, 0, transform, 0);
		} 
		
		Matrix.invertM(inverseTransform, 0, transform, 0);
		

	}
	
	@Override
	public void updateAll(Vector3 parentPos, Quaternion parentRot, Vector3 parentScale, float[] parentTransform) {
		Matrix.setIdentityM(modelMatrix, 0);

		Matrix.multiplyMM(modelMatrix, 0, transform, 0, modelMatrix, 0);
		Matrix.translateM(modelMatrix, 0, pos.x, pos.y, pos.z);
		Matrix.rotateM(modelMatrix, 0, rot.angle, rot.rotX, rot.rotY, rot.rotZ);
		Matrix.multiplyMM(modelMatrix, 0, inverseTransform, 0, modelMatrix, 0);
		Matrix.multiplyMM(modelMatrix, 0, parentTransform, 0, modelMatrix, 0);

		for (int i = 0; i < children.size(); ++i) {	
			children.get(i).updateAll(absolutePos, absoluteRot, absoluteScale, modelMatrix);
		}
		

		
	}
}
