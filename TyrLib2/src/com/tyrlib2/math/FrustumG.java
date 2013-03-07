package com.tyrlib2.math;

public class FrustumG {
	
	public static final int NEAR_CLIP_PLANE = 0;
	public static final int FAR_CLIP_PLANE = 1;
	public static final int BOTTOM_CLIP_PLANE = 2;
	public static final int LEFT_CLIP_PLANE = 3;
	public static final int RIGHT_CLIP_PLANE = 4;
	public static final int TOP_CLIP_PLANE = 5;
	
	public Plane[] planes = new Plane[6];
	private Vector3 p = new Vector3();
	
	public FrustumG(Vector3 camPos, Vector3 lookDirection, Vector3 up, int nearClip, int farClip, float nearWidth, float nearHeight) {
		
		lookDirection.normalize();
		up.normalize();
		
		Vector3 nearClipPoint = camPos.add(lookDirection.multiply(nearClip));
		Vector3 farClipPoint = camPos.add(lookDirection.multiply(farClip));
		
		planes[NEAR_CLIP_PLANE] = new Plane(lookDirection, nearClipPoint);
		planes[FAR_CLIP_PLANE] = new Plane(lookDirection.multiply(-1), farClipPoint);
		
		Vector3 right = lookDirection.cross(up);
		
		Vector3 nct = nearClipPoint.add(up.multiply(nearHeight/2));
		Vector3 ncb = nearClipPoint.add(up.multiply(-nearHeight/2));
		Vector3 ncr = nearClipPoint.add(right.multiply(nearWidth/2));
		Vector3 ncl = nearClipPoint.add(right.multiply(-nearWidth/2));
		
		Vector3 bottomNormal = right.cross(ncb.sub(camPos));
		Vector3 topNormal = nct.sub(camPos).cross(right);
		Vector3 leftNormal = ncl.sub(camPos).cross(up);
		Vector3 rightNormal = up.cross(ncr.sub(camPos));
		
		bottomNormal.normalize();
		topNormal.normalize();
		leftNormal.normalize();
		rightNormal.normalize();
		
		planes[BOTTOM_CLIP_PLANE] = new Plane(bottomNormal, ncb);
		planes[TOP_CLIP_PLANE] = new Plane(topNormal, nct);
		planes[LEFT_CLIP_PLANE] = new Plane(leftNormal, ncl);
		planes[RIGHT_CLIP_PLANE] = new Plane(rightNormal, ncr);
		
	}
	
	public boolean pointInFrustum(Vector3 point) {
		
		for (int i = 0; i < planes.length; ++i) {
			if (planes[i].distance(point) < 0) {
				return false;
			}
		}
		
		return true;
	}
	
	public boolean aabbInFrustum(AABB aabb) {
		
		for (int i = 0; i < planes.length; ++i) {
			
			if (planes[i].normal.x >= 0) {
				p.x = aabb.max.x;
			} else {
				p.x = aabb.min.x;
			}
			
			if (planes[i].normal.y >= 0) {
				p.y = aabb.max.y;
			} else {
				p.y = aabb.min.y;
			}
			
			if (planes[i].normal.z >= 0) {
				p.z = aabb.max.z;
			} else {
				p.z = aabb.min.z;
			}
			
			
			// is the positive vertex outside?
			if (planes[i].distance(p) < 0)
				return false;
		}
		
		return true;
	}
}
