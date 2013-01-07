package com.tyrlib2.animation;

import com.tyrlib2.math.Quaternion;
import com.tyrlib2.math.Vector3;

/** This class represents a single animation frame
 * 
 * @author Sascha
 *
 */

public class AnimationFrame {
	public float time;
	public Vector3[] bonePos;
	public Quaternion[] boneRot;
	
	public AnimationFrame(int bones) {
		bonePos = new Vector3[bones];
		boneRot = new Quaternion[bones];
	}
}
