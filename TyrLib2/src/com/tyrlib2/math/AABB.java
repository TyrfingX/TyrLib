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
	
	/**
	 * Calculates intersection with the given ray between a certain distance
	 * interval.
	 * 
	 * Ray-box intersection is using IEEE numerical properties to ensure the
	 * test is both robust and efficient, as described in:
	 * 
	 * Amy Williams, Steve Barrus, R. Keith Morley, and Peter Shirley: "An
	 * Efficient and Robust Ray-Box Intersection Algorithm" Journal of graphics
	 * tools, 10(1):49-54, 2005
	 * 
	 * @param ray
	 *            incident ray
	 * @param minDir
	 * @param maxDir
	 * @return intersection point on the bounding box (only the first is
	 *         returned) or null if no intersection
	 */
	public Vector3 intersectsRay(Ray ray, float minDir, float maxDir) {
		Vector3 invDir = new Vector3(1f / ray.direction.x, 1f / ray.direction.y, 1f / ray.direction.z);
		
		boolean signDirX = invDir.x < 0;
		boolean signDirY = invDir.y < 0;
		boolean signDirZ = invDir.z < 0;
		
		Vector3 bbox = signDirX ? max : min;
		
		float tmin = (bbox.x - ray.origin.x) * invDir.x;
		bbox = signDirX ? min : max;
		float tmax = (bbox.x - ray.origin.x) * invDir.x;
		bbox = signDirY ? max : min;
		float tymin = (bbox.y - ray.origin.y) * invDir.y;
		bbox = signDirY ? min : max;
		float tymax = (bbox.y - ray.origin.y) * invDir.y;

		if ((tmin > tymax) || (tymin > tmax))
			return null;
		if (tymin > tmin)
			tmin = tymin;
		if (tymax < tmax)
			tmax = tymax;

		bbox = signDirZ ? max : min;
		float tzmin = (bbox.z - ray.origin.z) * invDir.z;
		bbox = signDirZ ? min : max;
		float tzmax = (bbox.z - ray.origin.z) * invDir.z;

		if ((tmin > tzmax) || (tzmin > tmax))
			return null;
		if (tzmin > tmin)
			tmin = tzmin;
		if (tzmax < tmax)
			tmax = tzmax;
		if ((tmin < maxDir) && (tmax > minDir)) {
			return ray.getPointAtDistance(tmin);
		}
		return null;
	}
}
