package com.tyrfing.games.tyrlib3.model.graphics.scene;

import com.tyrfing.games.tyrlib3.model.math.Vector3F;

public class RaycastResult implements Comparable<RaycastResult> {
	public SceneObject sceneObject;
	public Vector3F intersection;
	public float distance;

	@Override
	public int compareTo(RaycastResult another) {
		if (another.distance < distance) {
			return 1;
		} else if (another.distance > distance) {
			return -1;
		}
		
		return 0;
	}
}
