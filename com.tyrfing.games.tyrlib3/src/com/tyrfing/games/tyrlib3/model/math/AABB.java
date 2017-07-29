package com.tyrfing.games.tyrlib3.model.math;



/**
 * An axis aligned bounding box
 * @author Sascha
 *
 */

public class AABB {
	public Vector3F min;
	public Vector3F max;
	
	private Vector3F points[];
	
	public AABB() {
		min = new Vector3F();
		max = new Vector3F();
	}
	
	public AABB(Vector3F min, Vector3F max) {
		this.min = min;
		this.max = max;
	}
	
	public Vector3F getCenter() {
		Vector3F diff = min.vectorTo(max);
		return min.add(diff.multiply(0.5f));
	}
	
	public void getCenter(Vector3F center) {
		Vector3F.vectorTo(min, max, center);
		Vector3F.multiply(0.5f, center);
		Vector3F.add(min, center, center);
	}
	
	public float getExtends() {
		float x = max.x  - min.x;
		float y = max.y  - min.y;
		float z = max.z  - min.z;
		
		return (float) Math.sqrt(x*x+y*y+z*z);
	}
	
	public Vector3F[] getPoints() {
		
		if (points == null) {
			points = new Vector3F[] {	
					new Vector3F(min),
					new Vector3F(max.x, min.y, min.z),
					new Vector3F(min.x, max.y, min.z),
					new Vector3F(max.x, max.y, min.z),
					new Vector3F(min.x, min.y, max.z),
					new Vector3F(max.x, min.y, max.z),
					new Vector3F(min.x, max.y, max.z),
					new Vector3F(max),
			};
		}
		
		return points;
	}
	
	/**
	 * Create a bounding box containing all the passed points.
	 * Assumes that the position data is stored in position 0
	 * @param points	An array of the points to be contained by this box
	 * @param stride	Float length of one vertex data
	 * @return			An AABB containing all vertices
	 */
	
	public static AABB createFromPoints(float[] points, int stride, boolean threeDim) {
		AABB box = new AABB();
		
		box.updateWithPoints(points, stride, threeDim);
		
		return box;
		
	}
	
	public static AABB createFromPoints(float[] points, int stride) {
		return createFromPoints(points, stride, true);
	}
	
	public static AABB createFromPoints(Vector3F[] points, int start, int end) {
		AABB box = new AABB();
		
		box.updateWithPoints(points, start, end);
		
		return box;
		
	}
	
	public void updateWithPoints(Vector3F[] points, int start, int end) {
		if (points.length > 0) {
			min.x = points[start].x;
			min.y = points[start].y;
			min.z = points[start].z;
			
			max.x = points[start].x;
			max.y = points[start].y;
			max.z = points[start].z;
		}
		
		for (int i = start; i <= end; ++i) {
			if (points[i].x > max.x) max.x = points[i].x;
			if (points[i].y > max.y) max.y = points[i].y;
			if (points[i].z > max.z) max.z = points[i].z;
			
			if (points[i].x < min.x) min.x = points[i].x;
			if (points[i].y < min.y) min.y = points[i].y;
			if (points[i].z < min.z) min.z = points[i].z;
		}
	}
	
	public void updateWithPoints(float[] points, int stride, boolean threeDim) {
		if (threeDim) {
			if (points.length > 0) {
				min.x = points[0];
				min.y = points[1];
				min.z = points[2];
				
				max.x = points[0];
				max.y = points[1];
				max.z = points[2];
			}
			
			for (int i = stride; i < points.length; i += stride) {
				if (points[i + 0] > max.x) max.x = points[i + 0];
				if (points[i + 1] > max.y) max.y = points[i + 1];
				if (points[i + 2] > max.z) max.z = points[i + 2];
				
				if (points[i + 0] < min.x) min.x = points[i + 0];
				if (points[i + 1] < min.y) min.y = points[i + 1];
				if (points[i + 2] < min.z) min.z = points[i + 2];
			}
		} else {
			if (points.length > 0) {
				min.x = points[0];
				min.y = points[1];
				min.z = 0;
				
				max.x = points[0];
				max.y = points[1];
				max.z = 0;
			}
			
			for (int i = stride; i < points.length; i += stride) {
				if (points[i + 0] > max.x) max.x = points[i + 0];
				if (points[i + 1] > max.y) max.y = points[i + 1];
				
				if (points[i + 0] < min.x) min.x = points[i + 0];
				if (points[i + 1] < min.y) min.y = points[i + 1];
			}
		}
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
	public Vector3F intersectsRay(Ray ray, float minDir, float maxDir) {
		float invDirX = 1f / ray.direction.x;
		float invDirY = 1f / ray.direction.y;
		float invDirZ = 1f / ray.direction.z;
		
		boolean signDirX = invDirX < 0;
		boolean signDirY = invDirY < 0;
		boolean signDirZ = invDirZ < 0;
		
		Vector3F bbox = signDirX ? max : min;
		
		float tmin = (bbox.x - ray.origin.x) * invDirX;
		bbox = signDirX ? min : max;
		float tmax = (bbox.x - ray.origin.x) * invDirX;
		bbox = signDirY ? max : min;
		float tymin = (bbox.y - ray.origin.y) * invDirY;
		bbox = signDirY ? min : max;
		float tymax = (bbox.y - ray.origin.y) * invDirY;

		if ((tmin > tymax) || (tymin > tmax))
			return null;
		if (tymin > tmin)
			tmin = tymin;
		if (tymax < tmax)
			tmax = tymax;

		bbox = signDirZ ? max : min;
		float tzmin = (bbox.z - ray.origin.z) * invDirZ;
		bbox = signDirZ ? min : max;
		float tzmax = (bbox.z - ray.origin.z) * invDirZ;

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
	
	
	public boolean intersectsAABB(AABB other) {

	    if (min.x > other.max.x) return false;
	    if (other.min.x > max.x) return false;
	    if (min.y > other.max.y) return false;
	    if (other.min.y > max.y) return false;
	    if (min.z > other.max.z) return false;
	    if (other.min.z > max.z) return false;

	    return true;
	}
	
	public boolean containsAABB(AABB other) {
		if (min.x > other.min.x) return false;
		if (min.y > other.min.y) return false;
		if (min.z > other.min.z) return false;
		
		if (max.x < other.max.x) return false;
		if (max.y < other.max.y) return false;
		if (max.z < other.max.z) return false;
		
		return true;
	}
	
	public boolean containsPoint(Vector3F point) {
		if (min.x > point.x) return false;
		if (min.y > point.y) return false;
		if (min.z > point.z) return false;
		
		if (max.x < point.x) return false;
		if (max.y < point.y) return false;
		if (max.z < point.z) return false;
		
		return true;
	}
}
