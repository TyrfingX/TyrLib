package com.tyrfing.games.tyrlib3.math;

public class FrustumG {
	
	public static final int NEAR_CLIP_PLANE = 0;
	public static final int FAR_CLIP_PLANE = 1;
	public static final int BOTTOM_CLIP_PLANE = 2;
	public static final int LEFT_CLIP_PLANE = 3;
	public static final int RIGHT_CLIP_PLANE = 4;
	public static final int TOP_CLIP_PLANE = 5;
	
	public static final int COUNT_PLANES = 6;
	
	public Plane[] planes = new Plane[COUNT_PLANES];
	private static float PX, PY, PZ;
	private static final float[] points = new float[24];
	private static final AABB aabb = new AABB();
	
	public final Vector3F fctl = new Vector3F();
	public final Vector3F fctr = new Vector3F();
	public final Vector3F fcbl = new Vector3F();
	public final Vector3F fcbr = new Vector3F();
	
	public final Vector3F fctlD = new Vector3F();
	public final Vector3F fctrD = new Vector3F();
	public final Vector3F fcblD = new Vector3F();
	public final Vector3F fcbrD = new Vector3F();
	
	public final Vector3F nct = new Vector3F();
	public final Vector3F ncb = new Vector3F();
	public final Vector3F ncr = new Vector3F();
	public final Vector3F ncl = new Vector3F();
	
	public final Vector3F nctCamPos = new Vector3F();
	public final Vector3F ncbCamPos = new Vector3F();
	public final Vector3F ncrCamPos = new Vector3F();
	public final Vector3F nclCamPos = new Vector3F();
	
	public final Vector3F nctl = new Vector3F();
	public final Vector3F nctr = new Vector3F();
	public final Vector3F ncbl = new Vector3F();
	public final Vector3F ncbr = new Vector3F();
	
	public final Vector3F rightNearWidthHalf = new Vector3F();
	
	private Vector3F nearClipPoint = new Vector3F();
	private Vector3F farClipPoint = new Vector3F();
	
	public FrustumG() {
		for (int i = 0; i < COUNT_PLANES; ++i) {
			planes[i] = new Plane(new Vector3F(), 0, 0, 0);
		}
	}
	
	public FrustumG(Vector3F camPos, Vector3F lookDirection, Vector3F up, int nearClip, int farClip, float nearWidth, float nearHeight) {
		this();
		update(camPos, lookDirection, up, nearClip, farClip, nearWidth, nearHeight);
	}
	
	public void update(Vector3F camPos, Vector3F lookDirection, Vector3F up, int nearClip, int farClip, float nearWidth, float nearHeight) {
		lookDirection.normalize();
		up.normalize();
		
		// Get near clip and farclip point
		Vector3F.addScaled(camPos, lookDirection, nearClip, nearClipPoint);
		Vector3F.addScaled(camPos, lookDirection, farClip, farClipPoint);
		
		// setup the nearclip and farclip planes
		planes[NEAR_CLIP_PLANE].normal.set(lookDirection);
		planes[NEAR_CLIP_PLANE].set(nearClipPoint);
		
		planes[FAR_CLIP_PLANE].normal.setScaled(lookDirection, -1);
		planes[FAR_CLIP_PLANE].set(farClipPoint);
		
		// get the right vector
		Vector3F right = lookDirection.cross(up);
		right.normalize();
		rightNearWidthHalf.setScaled(right, nearWidth /2);
		
		// Get the middle points of the edges of the nearclip plane
		Vector3F.addScaled(nearClipPoint, up, nearHeight/2, nct);
		Vector3F.addScaled(nearClipPoint, up, -nearHeight/2, ncb);
		Vector3F.add(nearClipPoint, rightNearWidthHalf, ncr);
		Vector3F.addScaled(nearClipPoint, rightNearWidthHalf, -1, ncl);

		// Get the vectors from the camera to the near clip points
		Vector3F.vectorTo(camPos, ncb, ncbCamPos);
		Vector3F.vectorTo(camPos, nct, nctCamPos);
		Vector3F.vectorTo(camPos, ncr, ncrCamPos);
		Vector3F.vectorTo(camPos, ncl, nclCamPos);
		
		// Get the normal vectors for the remaining planes
		Vector3F.cross(right, ncbCamPos, planes[BOTTOM_CLIP_PLANE].normal);
		Vector3F.cross(nctCamPos, right, planes[TOP_CLIP_PLANE].normal);
		Vector3F.cross(nclCamPos, up, planes[LEFT_CLIP_PLANE].normal);
		Vector3F.cross(up, ncrCamPos, planes[RIGHT_CLIP_PLANE].normal);

		planes[BOTTOM_CLIP_PLANE].normal.normalize();
		planes[TOP_CLIP_PLANE].normal.normalize();
		planes[LEFT_CLIP_PLANE].normal.normalize();
		planes[RIGHT_CLIP_PLANE].normal.normalize();
		
		// Get the origin vectors for the remaining planes
		planes[BOTTOM_CLIP_PLANE].set(ncb);
		planes[TOP_CLIP_PLANE].set(nct);
		planes[LEFT_CLIP_PLANE].set(ncl);
		planes[RIGHT_CLIP_PLANE].set(ncr);
		
		// Get the corners of the nearclip plane
		
		Vector3F.addScaled(nct, rightNearWidthHalf, -1, nctl);
		Vector3F.add(nct, rightNearWidthHalf, nctr);
		Vector3F.addScaled(ncb, rightNearWidthHalf, -1, ncbl);
		Vector3F.add(ncb, rightNearWidthHalf, ncbr);
		
		// Get the vectors to the far clip plane corners
		
		Vector3F.vectorTo(camPos, nctl, fctlD);
		Vector3F.vectorTo(camPos, nctr, fctrD);
		Vector3F.vectorTo(camPos, ncbl, fcblD);
		Vector3F.vectorTo(camPos, ncbr, fcbrD);
		
		fctlD.normalize();
		fctrD.normalize();
		fcblD.normalize();
		fcbrD.normalize();
		
		Vector3F.addScaled(nctl, fctlD, farClip, fctl);
		Vector3F.addScaled(nctr, fctrD, farClip, fctr);
		Vector3F.addScaled(ncbl, fcblD, farClip, fcbl);
		Vector3F.addScaled(ncbr, fcbrD, farClip, fcbr);
		
		points[0] = nctl.x;
		points[1] = nctl.y;
		points[2] = nctl.z;
		
		points[3] = nctr.x;
		points[4] = nctr.y;
		points[5] = nctr.z;
		
		points[6] = ncbl.x;
		points[7] = ncbl.y;
		points[8] = ncbl.z;
		
		points[9] = ncbr.x;
		points[10] = ncbr.y;
		points[11] = ncbr.z;
		
		points[12] = fctl.x;
		points[13] = fctl.y;
		points[14] = fctl.z;
		
		points[15] = fctr.x;
		points[16] = fctr.y;
		points[17] = fctr.z;
		
		points[18] = fcbl.x;
		points[19] = fcbl.y;
		points[20] = fcbl.z;
		
		points[21] = fcbr.x;
		points[22] = fcbr.y;
		points[23] = fcbr.z;
		
		aabb.updateWithPoints(points, 3, true);
	}
	
	public float getFarClipWidth() {
		return (float) Math.sqrt((points[12]-points[15]) * (points[12]-points[15]) + (points[13]-points[16]) * (points[13]-points[16]) + (points[14]-points[17]) * (points[14]-points[17]));
	}
	
	public float getFarClipHeight() {
		return (float) Math.sqrt((points[15]-points[21]) * (points[15]-points[21]) + (points[16]-points[22]) * (points[16]-points[22]) + (points[17]-points[23]) * (points[17]-points[23]));
	}
	
	public AABB getAABB() {
		return aabb;
	}
	
	public boolean pointInFrustum(Vector3F point) {
		
		for (int i = 0; i < planes.length; ++i) {
			if (planes[i].distance(point) < 0) {
				return false;
			}
		}
		
		return true;
	}
	
	public boolean aabbInFrustum(AABB aabb) {
		
		for (int i = 1; i < COUNT_PLANES; ++i) {
			
			Plane plane = planes[i];

			Vector3F normal = plane.normal;
			
			PX = normal.x >= 0 ? aabb.max.x : aabb.min.x;
			PY = normal.y >= 0 ? aabb.max.y : aabb.min.y;
			PZ = normal.z >= 0 ? aabb.max.z : aabb.min.z;
			
			// is the positive vertex outside?
			if (normal.x * (PX - plane.x) +
				normal.y * (PY - plane.y) + 
				normal.z * (PZ - plane.z) < 0) {
				return false;
			}
		}
		
		return true;
	}
}
