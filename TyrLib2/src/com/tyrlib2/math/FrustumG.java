package com.tyrlib2.math;

public class FrustumG {
	
	public static final int NEAR_CLIP_PLANE = 0;
	public static final int FAR_CLIP_PLANE = 1;
	public static final int BOTTOM_CLIP_PLANE = 2;
	public static final int LEFT_CLIP_PLANE = 3;
	public static final int RIGHT_CLIP_PLANE = 4;
	public static final int TOP_CLIP_PLANE = 5;
	
	public static final int COUNT_PLANES = 6;
	
	public Plane[] planes = new Plane[COUNT_PLANES];
	private static final Vector3 p = new Vector3();
	private static final float[] points = new float[24];
	private static final AABB aabb = new AABB();
	
	public final Vector3 fctl = new Vector3();
	public final Vector3 fctr = new Vector3();
	public final Vector3 fcbl = new Vector3();
	public final Vector3 fcbr = new Vector3();
	
	public FrustumG() {
		for (int i = 0; i < COUNT_PLANES; ++i) {
			planes[i] = new Plane(new Vector3(), 0, 0, 0);
		}
	}
	
	public FrustumG(Vector3 camPos, Vector3 lookDirection, Vector3 up, int nearClip, int farClip, float nearWidth, float nearHeight) {
		this();
		update(camPos, lookDirection, up, nearClip, farClip, nearWidth, nearHeight);
	}
	
	public void update(Vector3 camPos, Vector3 lookDirection, Vector3 up, int nearClip, int farClip, float nearWidth, float nearHeight) {
		lookDirection.normalize();
		up.normalize();
		
		Vector3 nearClipPoint = camPos.add(lookDirection.multiply(nearClip));
		Vector3 farClipPoint = camPos.add(lookDirection.multiply(farClip));
		
		planes[NEAR_CLIP_PLANE].normal.x = lookDirection.x;
		planes[NEAR_CLIP_PLANE].normal.y = lookDirection.y;
		planes[NEAR_CLIP_PLANE].normal.z = lookDirection.z;
		planes[NEAR_CLIP_PLANE].x = nearClipPoint.x;
		planes[NEAR_CLIP_PLANE].y = nearClipPoint.y;
		planes[NEAR_CLIP_PLANE].z = nearClipPoint.z;
		
		planes[FAR_CLIP_PLANE].normal.x = -lookDirection.x;
		planes[FAR_CLIP_PLANE].normal.y = -lookDirection.y;
		planes[FAR_CLIP_PLANE].normal.z = -lookDirection.z;
		planes[FAR_CLIP_PLANE].x = farClipPoint.x;
		planes[FAR_CLIP_PLANE].y = farClipPoint.y;
		planes[FAR_CLIP_PLANE].z = farClipPoint.z;
		
		Vector3 right = lookDirection.cross(up);

		float rightNearWidthHalfX = right.x * nearWidth / 2;
		float rightNearWidthHalfY = right.y * nearWidth / 2;
		float rightNearWidthHalfZ = right.z * nearWidth / 2;
				
		float nctX = nearClipPoint.x + up.x * nearHeight / 2;
		float nctY = nearClipPoint.y + up.y * nearHeight / 2;
		float nctZ = nearClipPoint.z + up.z * nearHeight / 2;
		
		float ncbX = nearClipPoint.x + up.x * -nearHeight / 2;
		float ncbY = nearClipPoint.y + up.y * -nearHeight / 2;
		float ncbZ = nearClipPoint.z + up.z * -nearHeight / 2;
		
		float ncrX = nearClipPoint.x + rightNearWidthHalfX;
		float ncrY = nearClipPoint.y + rightNearWidthHalfY;
		float ncrZ = nearClipPoint.z + rightNearWidthHalfZ;
		
		float nclX = nearClipPoint.x + -rightNearWidthHalfX;
		float nclY = nearClipPoint.y + -rightNearWidthHalfY;
		float nclZ = nearClipPoint.z + -rightNearWidthHalfZ;

		/*
		Vector3 nct = nearClipPoint.add(up.multiply(nearHeight/2));
		Vector3 ncb = nearClipPoint.add(up.multiply(-nearHeight/2));
		Vector3 ncr = nearClipPoint.add(right.multiply(nearWidth/2));
		Vector3 ncl = nearClipPoint.add(right.multiply(-nearWidth/2));
*/
/*
		float bottomNormalX = right.y * (ncbZ - camPos.z) - right.z * (ncbY - camPos.y);
		float bottomNormalY = -(right.x * (ncbZ - camPos.z) - right.z * (ncbX - camPos.x));
		float bottomNormalZ = right.x * (ncbY - camPos.y) - right.y * (ncbX - camPos.x);
		
		float topNormalX = nctY * right.z - nctZ * right.y;
		float topNormalY = -(nctX * right.z - nctZ * right.x);
		float topNormalZ = nctX * right.y - nctY * right.x;
		
		float leftNormalX = nctY * right.z - nctZ * right.y;
		float leftNormalY = -(nctX * right.z - nctZ * right.x);
		float leftNormalZ = nctX * right.y - nctY * right.x;
		*/

		float ncbCamPosX = ncbX - camPos.x;
		float ncbCamPosY = ncbY - camPos.y;
		float ncbCamPosZ = ncbZ - camPos.z;
		
		float nctCamPosX = nctX - camPos.x;
		float nctCamPosY = nctY - camPos.y;
		float nctCamPosZ = nctZ - camPos.z;
		
		float nclCamPosX = nclX - camPos.x;
		float nclCamPosY = nclY - camPos.y;
		float nclCamPosZ = nclZ - camPos.z;
		
		float ncrCamPosX = ncrX - camPos.x;
		float ncrCamPosY = ncrY - camPos.y;
		float ncrCamPosZ = ncrZ - camPos.z;
		
		Vector3.cross(planes[BOTTOM_CLIP_PLANE].normal, right.x, right.y, right.z, ncbCamPosX, ncbCamPosY, ncbCamPosZ);
		Vector3.cross(planes[TOP_CLIP_PLANE].normal, nctCamPosX, nctCamPosY, nctCamPosZ, right.x, right.y, right.z);
		Vector3.cross(planes[LEFT_CLIP_PLANE].normal, nclCamPosX, nclCamPosY, nclCamPosZ, up.x, up.y, up.z);
		Vector3.cross(planes[RIGHT_CLIP_PLANE].normal, up.x, up.y, up.z, ncrCamPosX, ncrCamPosY, ncrCamPosZ);
		
		planes[BOTTOM_CLIP_PLANE].normal.normalize();
		planes[TOP_CLIP_PLANE].normal.normalize();
		planes[LEFT_CLIP_PLANE].normal.normalize();
		planes[RIGHT_CLIP_PLANE].normal.normalize();
		
		planes[BOTTOM_CLIP_PLANE].x =  ncbX;
		planes[BOTTOM_CLIP_PLANE].y =  ncbY;
		planes[BOTTOM_CLIP_PLANE].z =  ncbZ;
		
		planes[TOP_CLIP_PLANE].x =  nctX;
		planes[TOP_CLIP_PLANE].y =  nctZ;
		planes[TOP_CLIP_PLANE].z =  ncbZ;
		
		planes[LEFT_CLIP_PLANE].x =  nclX;
		planes[LEFT_CLIP_PLANE].y =  nclY;
		planes[LEFT_CLIP_PLANE].z =  nclZ;
	
		planes[RIGHT_CLIP_PLANE].x =  ncrX;
		planes[RIGHT_CLIP_PLANE].y =  ncrY;
		planes[RIGHT_CLIP_PLANE].z =  ncrZ;
		
		float nctlX = nctX - rightNearWidthHalfX;
		float nctlY = nctY - rightNearWidthHalfY;
		float nctlZ = nctZ - rightNearWidthHalfZ;
		
		float nctrX = nctX + rightNearWidthHalfX;
		float nctrY = nctY + rightNearWidthHalfY;
		float nctrZ = nctZ + rightNearWidthHalfZ;
		
		float ncblX = ncbX - rightNearWidthHalfX;
		float ncblY = ncbY - rightNearWidthHalfY;
		float ncblZ = ncbZ - rightNearWidthHalfZ;
		
		float ncbrX = ncbX + rightNearWidthHalfX;
		float ncbrY = ncbY + rightNearWidthHalfY;
		float ncbrZ = ncbZ + rightNearWidthHalfZ;
		
		/*
		Vector3 nctl = nct.add(right.multiply(-nearWidth/2));
		Vector3 nctr = nct.add(right.multiply(nearWidth/2));
		Vector3 ncbl = ncb.add(right.multiply(-nearWidth/2));
		Vector3 ncbr = ncb.add(right.multiply(nearWidth/2));
		*/
		
		float fctlDX = nctlX - camPos.x;
		float fctlDY = nctlY - camPos.y;
		float fctlDZ = nctlZ - camPos.z;
		
		float fctrDX = nctrX - camPos.x;
		float fctrDY = nctrY - camPos.y;
		float fctrDZ = nctrZ - camPos.z;
		
		float fcblDX = ncblX - camPos.x;
		float fcblDY = ncblY - camPos.y;
		float fcblDZ = ncblZ - camPos.z;
		
		float fcbrDX = ncbrX - camPos.x;
		float fcbrDY = ncbrY - camPos.y;
		float fcbrDZ = ncbrZ - camPos.z;
		
		float lengthfctl = (float) Math.sqrt(fctlDX * fctlDX + fctlDY * fctlDY + fctlDZ * fctlDZ);
		if (lengthfctl != 0) {
			fctlDX /= lengthfctl;
			fctlDY /= lengthfctl;
			fctlDZ /= lengthfctl;
		}
		
		float lengthfctr = (float) Math.sqrt(fctrDX * fctrDX + fctrDY * fctrDY + fctrDZ * fctrDZ);
		if (lengthfctr != 0) {
			fctrDX /= lengthfctr;
			fctrDY /= lengthfctr;
			fctrDZ /= lengthfctr;
		}
		
		float lengthfcbl = (float) Math.sqrt(fcblDX * fcblDX + fcblDY * fcblDY + fcblDZ * fcblDZ);
		if (lengthfcbl != 0) {
			fcblDX /= lengthfcbl;
			fcblDY /= lengthfcbl;
			fcblDZ /= lengthfcbl;
		}
		
		float lengthfcbr = (float) Math.sqrt(fcbrDX * fcbrDX + fcbrDY * fcbrDY + fcbrDZ * fcbrDZ);
		if (lengthfcbr != 0) {
			fcbrDX /= lengthfcbr;
			fcbrDY /= lengthfcbr;
			fcbrDZ /= lengthfcbr;
		}
		
		fctl.x = nctlX + fctlDX * farClip;
		fctl.y = nctlY + fctlDY * farClip;
		fctl.z = nctlZ + fctlDZ * farClip;
		
		fctr.x = nctrX + fctrDX * farClip;
		fctr.y = nctrY + fctrDY * farClip;
		fctr.z = nctrZ + fctrDZ * farClip;
		
		fcbl.x = ncblX + fcblDX * farClip;
		fcbl.y = ncblY + fcblDY * farClip;
		fcbl.z = ncblZ + fcblDZ * farClip;

		fcbr.x = ncbrX + fcbrDX * farClip;
		fcbr.y = ncbrY + fcbrDY * farClip;
		fcbr.z = ncbrZ + fcbrDZ * farClip;
		
		points[0] = nctlX;
		points[1] = nctlY;
		points[2] = nctlZ;
		
		points[3] = nctrX;
		points[4] = nctrY;
		points[5] = nctrZ;
		
		points[6] = ncblX;
		points[7] = ncblY;
		points[8] = ncblZ;
		
		points[9] = ncbrX;
		points[10] = ncbrY;
		points[11] = ncbrZ;
		
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
		
		aabb.updateWithPoints(points, 3);
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
	
	public boolean pointInFrustum(Vector3 point) {
		
		for (int i = 0; i < planes.length; ++i) {
			if (planes[i].distance(point) < 0) {
				return false;
			}
		}
		
		return true;
	}
	
	public boolean aabbInFrustum(AABB aabb) {
		
		for (int i = 1; i < COUNT_PLANES; ++i) {
			
			Vector3 normal = planes[i].normal;
			
			if (normal.x >= 0) {
				p.x = aabb.max.x;
			} else {
				p.x = aabb.min.x;
			}
			
			if (normal.y >= 0) {
				p.y = aabb.max.y;
			} else {
				p.y = aabb.min.y;
			}
			
			if (normal.z >= 0) {
				p.z = aabb.max.z;
			} else {
				p.z = aabb.min.z;
			}
			
			// is the positive vertex outside?
			if (normal.x * (p.x - planes[i].x) +
				normal.y * (p.y - planes[i].y) + 
				normal.z * (p.z - planes[i].z) < 0) {
				return false;
			}
		}
		
		return true;
	}
}
