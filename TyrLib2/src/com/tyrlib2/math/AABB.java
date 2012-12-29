package com.tyrlib2.math;

/**
 * An axis aligned bounding box
 * @author Sascha
 *
 */

public class AABB {
	public Vector3 min;
	public Vector3 max;
	
	public AABB() {
		min = new Vector3();
		max = new Vector3();
	}
	
	public Vector3[] getPoints() {
		Vector3 points[] = {	
				new Vector3(min),
				new Vector3(max.x, min.y, min.z),
				new Vector3(min.x, max.y, min.z),
				new Vector3(max.x, max.y, min.z),
				new Vector3(min.x, min.y, max.z),
				new Vector3(max.x, min.y, max.z),
				new Vector3(min.x, max.y, max.z),
				new Vector3(max),

		};
		
		return points;
	}
	
	/**
	 * Create a bounding box containing all the passed points.
	 * Assumes that the position data is stored in position 0
	 * @param points	An array of the points to be contained by this box
	 * @param stride	Float length of one vertex data
	 * @return			An AABB containing all vertices
	 */
	
	public static AABB createFromPoints(float[] points, int stride) {
		AABB box = new AABB();
		
		if (points.length > 0) {
			box.min = new Vector3(points[0], points[1], points[2]);
			box.max = new Vector3(points[0], points[1], points[2]);
		}
		
		for (int i = stride; i < points.length; i += stride) {
			if (points[i + 0] > box.max.x) box.max.x = points[i + 0];
			if (points[i + 1] > box.max.y) box.max.y = points[i + 1];
			if (points[i + 2] > box.max.z) box.max.z = points[i + 2];
			
			if (points[i + 0] < box.min.x) box.min.x = points[i + 0];
			if (points[i + 1] < box.min.y) box.min.y = points[i + 1];
			if (points[i + 2] < box.min.z) box.min.z = points[i + 2];
		}
		
		return box;
		
	}
}
