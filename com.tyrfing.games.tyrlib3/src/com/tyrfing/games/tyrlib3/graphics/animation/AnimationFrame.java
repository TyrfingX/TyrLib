package com.tyrfing.games.tyrlib3.graphics.animation;

import com.tyrfing.games.tyrlib3.math.Quaternion;
import com.tyrfing.games.tyrlib3.math.Vector3F;

/** This class represents a single animation frame
 * 
 * @author Sascha
 *
 */

public class AnimationFrame {
	public float time;
	public Vector3F[] bonePos;
	public Quaternion[] boneRot;
	
	public AnimationFrame(int bones) {
		bonePos = new Vector3F[bones];
		boneRot = new Quaternion[bones];
	}
}
