package com.tyrlib2.util;

import com.tyrlib2.graphics.scene.SceneObject;
import com.tyrlib2.math.Vector3;

public class RaycastResult implements Comparable<RaycastResult> {
	public SceneObject sceneObject;
	public Vector3 intersection;
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
