package com.tyrlib2.graphics.animation;

import com.tyrlib2.graphics.scene.SceneNode;
import com.tyrlib2.math.Matrix;
import com.tyrlib2.math.Quaternion;
import com.tyrlib2.math.Vector3;

/**
 * Represents a single joint of a skeleton. Further bones, SceneNode or vertices may be bound to
 * this bone.
 * @author Sascha
 *
 */

public class Bone extends SceneNode {
	/**
	 * 
	 */
	private static final long serialVersionUID = -6784032679949238763L;
	
	protected String name;
	protected Quaternion initRot;
	protected Quaternion initRotInverse;
	protected Vector3 initPos;
	protected float[] bindPos = new float[16];
	protected float[] localBindPos = new float[16];
	protected float[] inverseBindPos = new float[16];
	protected float[] tmpMatrix = new float[16];
	
	private static final float[] translation = new float[16];
	
	private Quaternion tmp = new Quaternion();
	
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
		this.initRotInverse = rot.inverse();
		
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
	public void updateAll(Quaternion parentRot, Vector3 parentScale, float[] parentTransform) {
		
		if (initRot != null && initPos != null) {
		
			initRot.multiply(rot, tmp);
			tmp.toMatrix(tmpMatrix);
			Matrix.setIdentityM(translation, 0);
			Matrix.translateM(translation, 0, initPos.x + pos.x, initPos.y + pos.y, initPos.z + pos.z);
			Matrix.multiplyMM(tmpMatrix, 0, translation, 0, tmpMatrix, 0);
			Matrix.multiplyMM(tmpMatrix, 0, parentTransform, 0, tmpMatrix, 0);
			
			
			for (int i = 0, countChildren = children.size(); i < countChildren; ++i) {	
				children.get(i).updateAll(absoluteRot, absoluteScale, tmpMatrix);
			}

			Matrix.multiplyMM(modelMatrix, 0,  tmpMatrix, 0, inverseBindPos, 0);
		
		}
		
	}
	
	public Vector3 getInitPos() {
		return initPos;
	}
	
	public Quaternion getInitRot() {
		return initRot;
	}
}
