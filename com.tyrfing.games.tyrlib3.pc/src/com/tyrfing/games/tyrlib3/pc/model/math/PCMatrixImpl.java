package com.tyrfing.games.tyrlib3.pc.model.math;
import com.tyrfing.games.tyrlib3.model.math.IMatrixImpl;
import com.tyrfing.games.tyrlib3.model.math.Quaternion;
import com.tyrfing.games.tyrlib3.model.math.Vector3F;


public class PCMatrixImpl implements IMatrixImpl{

   	public static final float[] matrix = new float[16];
	public static final float[] a = new float[16];
	public static final float[] b = new float[16];
	
	@Override
	public void setIdentityM(float[] m, int offset) {
		m[0 + offset] = 1;
		m[1 + offset] = 0;
		m[2 + offset] = 0;
		m[3 + offset] = 0;
		
		m[4 + offset] = 0;
		m[5 + offset] = 1;
		m[6 + offset] = 0;
		m[7 + offset] = 0;
		
		m[8 + offset] = 0;
		m[9 + offset] = 0;
		m[10 + offset] = 1;
		m[11 + offset] = 0;
		
		m[12 + offset] = 0;
		m[13 + offset] = 0;
		m[14 + offset] = 0;
		m[15 + offset] = 1;
	}
	
	@Override
	public void scaleM(float[] m, int offset, float scaleX, float scaleY, float scaleZ) {
		m[0 + offset] = m[0 + offset] * scaleX;
		m[4 + offset] = m[4 + offset] * scaleY;
		m[8 + offset] = m[8 + offset] * scaleZ;
		
		m[1 + offset] = m[1 + offset] * scaleX;
		m[5 + offset] = m[5 + offset] * scaleY;
		m[9 + offset] = m[9 + offset] * scaleZ;
		
		m[2 + offset] = m[2 + offset] * scaleX;
		m[6 + offset] = m[6 + offset] * scaleY;
		m[10 + offset] = m[10 + offset] * scaleZ;
		
		m[3 + offset] = m[3 + offset] * scaleX;
		m[7 + offset] = m[7 + offset] * scaleY;
		m[11 + offset] = m[11 + offset] * scaleZ;
	}
	
	@Override
	public void multiplyMM(float[] result, int offsetResult, float[] lhs, int offsetLeft, float[] rhs, int offsetRight) {
		for(int i=0; i<16; i++){
			a[i] = lhs[i+offsetLeft];
			b[i] = rhs[i+offsetRight];
		}
    	
		matrix[0]  = a[0] * b[0]  + a[4] * b[1]  + a[8] * b[2]   + a[12] * b[3];
		matrix[1]  = a[1] * b[0]  + a[5] * b[1]  + a[9] * b[2]   + a[13] * b[3];
		matrix[2]  = a[2] * b[0]  + a[6] * b[1]  + a[10] * b[2]  + a[14] * b[3];
		matrix[3]  = a[3] * b[0]  + a[7] * b[1]  + a[11] * b[2]  + a[15] * b[3];
		matrix[4]  = a[0] * b[4]  + a[4] * b[5]  + a[8] * b[6]   + a[12] * b[7];
		matrix[5]  = a[1] * b[4]  + a[5] * b[5]  + a[9] * b[6]   + a[13] * b[7];
		matrix[6]  = a[2] * b[4]  + a[6] * b[5]  + a[10] * b[6]  + a[14] * b[7];
		matrix[7]  = a[3] * b[4]  + a[7] * b[5]  + a[11] * b[6]  + a[15] * b[7];
		matrix[8]  = a[0] * b[8]  + a[4] * b[9]  + a[8] * b[10]  + a[12] * b[11];
		matrix[9]  = a[1] * b[8]  + a[5] * b[9]  + a[9] * b[10]  + a[13] * b[11];
		matrix[10] = a[2] * b[8]  + a[6] * b[9]  + a[10] * b[10] + a[14] * b[11];
		matrix[11] = a[3] * b[8]  + a[7] * b[9]  + a[11] * b[10] + a[15] * b[11];
		matrix[12] = a[0] * b[12] + a[4] * b[13] + a[8] * b[14]  + a[12] * b[15];
		matrix[13] = a[1] * b[12] + a[5] * b[13] + a[9] * b[14]  + a[13] * b[15];
		matrix[14] = a[2] * b[12] + a[6] * b[13] + a[10] * b[14] + a[14] * b[15];
		matrix[15] = a[3] * b[12] + a[7] * b[13] + a[11] * b[14] + a[15] * b[15];
		for(int i=0; i<16; i++){
			result[i+offsetResult] = matrix[i];
		}
	}
	
	@Override
	public void translateM(float[] m, int offset, float x, float y, float z) {
		for (int i=0 ; i<4 ; i++) {
            int mi = offset + i;
            m[12 + mi] += m[mi] * x + m[4 + mi] * y + m[8 + mi] * z;
        }
	}
	
	@Override
	public void multiplyMV(float[] result, int offsetResult, float[] m, int offsetMatrix, float[] v, int offsetVector) {		
		int indexM;
		
		float x = v[offsetVector];
		float y = v[1 + offsetVector];
		float z = v[2 + offsetVector];
		float w = v[3 + offsetVector];
		
		for (int j=0; j<4; ++j) {
			indexM = j + offsetMatrix;
			result[j + offsetResult] = m[indexM] * x + m[indexM + 4] * y + m[indexM + 8] * z + m[indexM + 12] * w; 
		}
	}

	@Override
	public void invertM(float[] inv, int offsetResult, float[] m, int offsetMatrix) {
	    float det;
	    int i;

	    inv[0 + offsetResult] = m[5 + offsetMatrix]  * m[10 + offsetMatrix] * m[15 + offsetMatrix] - 
	    		m[5 + offsetMatrix]  * m[11 + offsetMatrix] * m[14 + offsetMatrix] - 
	    		m[9 + offsetMatrix]  * m[6 + offsetMatrix]  * m[15 + offsetMatrix] + 
	    		m[9 + offsetMatrix]  * m[7 + offsetMatrix]  * m[14 + offsetMatrix] +
	    		m[13 + offsetMatrix] * m[6 + offsetMatrix]  * m[11 + offsetMatrix] - 
	    		m[13 + offsetMatrix] * m[7 + offsetMatrix]  * m[10 + offsetMatrix];

	    inv[4 + offsetResult] = -m[4 + offsetMatrix]  * m[10 + offsetMatrix] * m[15 + offsetMatrix] + 
	    		m[4 + offsetMatrix]  * m[11 + offsetMatrix] * m[14 + offsetMatrix] + 
	    		m[8 + offsetMatrix]  * m[6 + offsetMatrix]  * m[15 + offsetMatrix] - 
	    		m[8 + offsetMatrix]  * m[7 + offsetMatrix]  * m[14 + offsetMatrix] - 
	    		m[12 + offsetMatrix] * m[6 + offsetMatrix]  * m[11 + offsetMatrix] + 
	    		m[12 + offsetMatrix] * m[7 + offsetMatrix]  * m[10 + offsetMatrix];

	    inv[8 + offsetResult] = m[4 + offsetMatrix]  * m[9 + offsetMatrix] * m[15 + offsetMatrix] - 
	    		m[4 + offsetMatrix]  * m[11 + offsetMatrix] * m[13 + offsetMatrix] - 
	    		m[8 + offsetMatrix]  * m[5 + offsetMatrix] * m[15 + offsetMatrix] + 
	    		m[8 + offsetMatrix]  * m[7 + offsetMatrix] * m[13 + offsetMatrix] + 
	    		m[12 + offsetMatrix] * m[5 + offsetMatrix] * m[11 + offsetMatrix] - 
	    		m[12 + offsetMatrix] * m[7 + offsetMatrix] * m[9 + offsetMatrix];

	    inv[12 + offsetResult] = -m[4 + offsetMatrix]  * m[9 + offsetMatrix] * m[14 + offsetMatrix] + 
	    		m[4 + offsetMatrix]  * m[10 + offsetMatrix] * m[13 + offsetMatrix] +
	    		m[8 + offsetMatrix]  * m[5 + offsetMatrix] * m[14 + offsetMatrix] - 
	    		m[8 + offsetMatrix]  * m[6 + offsetMatrix] * m[13 + offsetMatrix] - 
	    		m[12 + offsetMatrix] * m[5 + offsetMatrix] * m[10 + offsetMatrix] + 
	    		m[12 + offsetMatrix] * m[6 + offsetMatrix] * m[9 + offsetMatrix];

	    inv[1 + offsetResult] = -m[1 + offsetMatrix]  * m[10 + offsetMatrix] * m[15 + offsetMatrix] + 
	    		m[1 + offsetMatrix]  * m[11 + offsetMatrix] * m[14 + offsetMatrix] + 
	    		m[9 + offsetMatrix]  * m[2 + offsetMatrix] * m[15 + offsetMatrix] - 
	    		m[9 + offsetMatrix]  * m[3 + offsetMatrix] * m[14 + offsetMatrix] - 
	    		m[13 + offsetMatrix] * m[2 + offsetMatrix] * m[11 + offsetMatrix] + 
	    		m[13 + offsetMatrix] * m[3 + offsetMatrix] * m[10 + offsetMatrix];

	    inv[5 + offsetResult] = m[0 + offsetMatrix]  * m[10 + offsetMatrix] * m[15 + offsetMatrix] - 
	    		m[0 + offsetMatrix]  * m[11 + offsetMatrix] * m[14 + offsetMatrix] - 
	    		m[8 + offsetMatrix]  * m[2 + offsetMatrix] * m[15 + offsetMatrix] + 
	    		m[8 + offsetMatrix]  * m[3 + offsetMatrix] * m[14 + offsetMatrix] + 
	    		m[12 + offsetMatrix] * m[2 + offsetMatrix] * m[11 + offsetMatrix] - 
	    		m[12 + offsetMatrix] * m[3 + offsetMatrix] * m[10 + offsetMatrix];

	    inv[9 + offsetResult] = -m[0]  * m[9] * m[15 + offsetMatrix] + 
	    		m[0 + offsetMatrix]  * m[11 + offsetMatrix] * m[13 + offsetMatrix] + 
	    		m[8 + offsetMatrix]  * m[1 + offsetMatrix] * m[15 + offsetMatrix] - 
	    		m[8 + offsetMatrix]  * m[3 + offsetMatrix] * m[13 + offsetMatrix] - 
	    		m[12 + offsetMatrix] * m[1 + offsetMatrix] * m[11 + offsetMatrix] + 
	    		m[12 + offsetMatrix] * m[3 + offsetMatrix] * m[9 + offsetMatrix];

	    inv[13 + offsetResult] = m[0 + offsetMatrix]  * m[9 + offsetMatrix] * m[14 + offsetMatrix] - 
	    		m[0 + offsetMatrix]  * m[10 + offsetMatrix] * m[13 + offsetMatrix] - 
	    		m[8 + offsetMatrix]  * m[1 + offsetMatrix] * m[14 + offsetMatrix] + 
	    		m[8 + offsetMatrix]  * m[2 + offsetMatrix] * m[13 + offsetMatrix] + 
	    		m[12 + offsetMatrix] * m[1 + offsetMatrix] * m[10 + offsetMatrix] - 
	    		m[12 + offsetMatrix] * m[2 + offsetMatrix] * m[9 + offsetMatrix];

	    inv[2 + offsetResult] = m[1 + offsetMatrix]  * m[6 + offsetMatrix] * m[15 + offsetMatrix] - 
	    		m[1 + offsetMatrix]  * m[7 + offsetMatrix] * m[14 + offsetMatrix] - 
	    		m[5 + offsetMatrix]  * m[2 + offsetMatrix] * m[15 + offsetMatrix] + 
	    		m[5 + offsetMatrix]  * m[3 + offsetMatrix] * m[14 + offsetMatrix] + 
	    		m[13 + offsetMatrix] * m[2 + offsetMatrix] * m[7 + offsetMatrix] - 
	    		m[13 + offsetMatrix] * m[3 + offsetMatrix] * m[6 + offsetMatrix];

	    inv[6 + offsetResult] = -m[0 + offsetMatrix]  * m[6 + offsetMatrix] * m[15 + offsetMatrix] + 
	    		m[0 + offsetMatrix]  * m[7 + offsetMatrix] * m[14 + offsetMatrix] + 
	    		m[4 + offsetMatrix]  * m[2 + offsetMatrix] * m[15 + offsetMatrix] - 
	    		m[4 + offsetMatrix]  * m[3 + offsetMatrix] * m[14 + offsetMatrix] - 
	    		m[12 + offsetMatrix] * m[2 + offsetMatrix] * m[7 + offsetMatrix] + 
	    		m[12 + offsetMatrix] * m[3 + offsetMatrix] * m[6 + offsetMatrix];

	    inv[10 + offsetResult] = m[0 + offsetMatrix]  * m[5 + offsetMatrix] * m[15 + offsetMatrix] - 
	    		m[0 + offsetMatrix]  * m[7 + offsetMatrix] * m[13 + offsetMatrix] - 
	    		m[4 + offsetMatrix]  * m[1 + offsetMatrix] * m[15 + offsetMatrix] + 
	    		m[4 + offsetMatrix]  * m[3 + offsetMatrix] * m[13 + offsetMatrix] + 
	    		m[12 + offsetMatrix] * m[1 + offsetMatrix] * m[7 + offsetMatrix] - 
	    		m[12 + offsetMatrix] * m[3 + offsetMatrix] * m[5 + offsetMatrix];

	    inv[14 + offsetResult] = -m[0 + offsetMatrix]  * m[5 + offsetMatrix] * m[14 + offsetMatrix] + 
	    		m[0 + offsetMatrix]  * m[6 + offsetMatrix] * m[13 + offsetMatrix] + 
	    		m[4 + offsetMatrix]  * m[1 + offsetMatrix] * m[14 + offsetMatrix] - 
	    		m[4 + offsetMatrix]  * m[2 + offsetMatrix] * m[13 + offsetMatrix] - 
	    		m[12 + offsetMatrix] * m[1 + offsetMatrix] * m[6 + offsetMatrix] + 
	    		m[12 + offsetMatrix] * m[2 + offsetMatrix] * m[5 + offsetMatrix];

	    inv[3 + offsetResult] = -m[1 + offsetMatrix] * m[6 + offsetMatrix] * m[11 + offsetMatrix] + 
	    		m[1 + offsetMatrix] * m[7 + offsetMatrix] * m[10 + offsetMatrix] + 
	    		m[5 + offsetMatrix] * m[2 + offsetMatrix] * m[11 + offsetMatrix] - 
	    		m[5 + offsetMatrix] * m[3 + offsetMatrix] * m[10 + offsetMatrix] - 
	    		m[9 + offsetMatrix] * m[2 + offsetMatrix] * m[7 + offsetMatrix] + 
	    		m[9 + offsetMatrix] * m[3 + offsetMatrix] * m[6 + offsetMatrix];

	    inv[7 + offsetResult] = m[0 + offsetMatrix] * m[6 + offsetMatrix] * m[11 + offsetMatrix] - 
	    		m[0 + offsetMatrix] * m[7 + offsetMatrix] * m[10 + offsetMatrix] - 
	    		m[4 + offsetMatrix] * m[2 + offsetMatrix] * m[11 + offsetMatrix] + 
	    		m[4 + offsetMatrix] * m[3 + offsetMatrix] * m[10 + offsetMatrix] + 
	    		m[8 + offsetMatrix] * m[2 + offsetMatrix] * m[7 + offsetMatrix] - 
	    		m[8 + offsetMatrix] * m[3 + offsetMatrix] * m[6 + offsetMatrix];

	    inv[11 + offsetResult] = -m[0 + offsetMatrix] * m[5 + offsetMatrix] * m[11 + offsetMatrix] + 
	    		m[0 + offsetMatrix] * m[7 + offsetMatrix] * m[9 + offsetMatrix] + 
	    		m[4 + offsetMatrix] * m[1 + offsetMatrix] * m[11 + offsetMatrix] - 
	    		m[4 + offsetMatrix] * m[3 + offsetMatrix] * m[9 + offsetMatrix] - 
	    		m[8 + offsetMatrix] * m[1 + offsetMatrix] * m[7 + offsetMatrix] + 
	    		m[8 + offsetMatrix] * m[3 + offsetMatrix] * m[5 + offsetMatrix];

	    inv[15 + offsetResult] = m[0 + offsetMatrix] * m[5 + offsetMatrix] * m[10 + offsetMatrix] - 
	    		m[0 + offsetMatrix] * m[6 + offsetMatrix] * m[9 + offsetMatrix] - 
	    		m[4 + offsetMatrix] * m[1 + offsetMatrix] * m[10 + offsetMatrix] + 
	    		m[4 + offsetMatrix] * m[2 + offsetMatrix] * m[9 + offsetMatrix] + 
	    		m[8 + offsetMatrix] * m[1 + offsetMatrix] * m[6 + offsetMatrix] - 
	    		m[8 + offsetMatrix] * m[2 + offsetMatrix] * m[5 + offsetMatrix];

	    det = m[0 + offsetMatrix] * inv[0 + offsetResult] + m[1 + offsetMatrix] * inv[4 + offsetResult] + m[2 + offsetMatrix] * inv[8 + offsetResult] + m[3 + offsetMatrix] * inv[12 + offsetResult];

	    if (det == 0)
	    	return;

	    det = 1.0f / det;

	    for (i = 0; i < 16; i++)
	    	inv[i + offsetResult] = inv[i + offsetResult] * det;
	}

	@Override
	public void rotateM(float[] result, int offsetResult, float rotation, float x, float y, float z) {
		Quaternion q = Quaternion.fromAxisAngle(new Vector3F(x,y,z), rotation);
		q.toMatrix(result);
	}
	
	@Override
	public void orthoM(float[] result, int offsetResult, float left, float right, float bottom, float top, float near, float far) {
		result[0 + offsetResult] = 2 / (right - left);
		result[1 + offsetResult] = 0;
		result[2 + offsetResult] = 0;
		result[3 + offsetResult] = 0;
		
		result[4 + offsetResult] = 0;
		result[5 + offsetResult] = 2 / (top - bottom);
		result[6 + offsetResult] = 0;
		result[7 + offsetResult] = 0;
		
		result[8 + offsetResult] = 0;
		result[9 + offsetResult] = 0;
		result[10 + offsetResult] = -2 / (far - near);
		result[11 + offsetResult] = 0;
	
		result[12 + offsetResult] = - (right+left) / (right - left);
		result[13 + offsetResult] = - (top+bottom) / (top - bottom);
		result[14 + offsetResult] = - (far+near) / (far - near);
		result[15 + offsetResult] = 1;
	}
	
	@Override
	public void frustumM(float[] m, int offsetResult, float left, float right, float bottom, float top, float near, float far) {
		final float r_width  = 1.0f / (right - left);
        final float r_height = 1.0f / (top - bottom);
        final float r_depth  = 1.0f / (near - far);
        final float x = 2.0f * (near * r_width);
        final float y = 2.0f * (near * r_height);
        final float A = 2.0f * ((right + left) * r_width);
        final float B = (top + bottom) * r_height;
        final float C = (far + near) * r_depth;
        final float D = 2.0f * (far * near * r_depth);
        
        m[offsetResult + 0] = x;
        m[offsetResult + 5] = y;
        m[offsetResult + 8] = A;
        m[offsetResult +  9] = B;
        m[offsetResult + 10] = C;
        m[offsetResult + 14] = D;
        m[offsetResult + 11] = -1.0f;
        m[offsetResult +  1] = 0.0f;
        m[offsetResult +  2] = 0.0f;
        m[offsetResult +  3] = 0.0f;
        m[offsetResult +  4] = 0.0f;
        m[offsetResult +  6] = 0.0f;
        m[offsetResult +  7] = 0.0f;
        m[offsetResult + 12] = 0.0f;
        m[offsetResult + 13] = 0.0f;
        m[offsetResult + 15] = 0.0f;
	}
	
	public void setLookAtM(float[] rm, int rmOffset, float eyeX, float eyeY,
			float eyeZ, float centerX, float centerY, float centerZ, float upX,
			float upY, float upZ) {
		
		float fx = centerX - eyeX;
		float fy = centerY - eyeY;
		float fz = centerZ - eyeZ;

		// Normalize f
		float rlf = 1.0f / Vector3F.length(fx, fy, fz);
		fx *= rlf;
		fy *= rlf;
		fz *= rlf;

		// compute s = f x up (x means "cross product")
		float sx = fy * upZ - fz * upY;
		float sy = fz * upX - fx * upZ;
		float sz = fx * upY - fy * upX;

		// and normalize s
		float rls = 1.0f / Vector3F.length(sx, sy, sz);
		sx *= rls;
		sy *= rls;
		sz *= rls;

		// compute u = s x f
		float ux = sy * fz - sz * fy;
		float uy = sz * fx - sx * fz;
		float uz = sx * fy - sy * fx;

		rm[rmOffset + 0] = sx;
		rm[rmOffset + 1] = ux;
		rm[rmOffset + 2] = -fx;
		rm[rmOffset + 3] = 0.0f;

		rm[rmOffset + 4] = sy;
		rm[rmOffset + 5] = uy;
		rm[rmOffset + 6] = -fy;
		rm[rmOffset + 7] = 0.0f;

		rm[rmOffset + 8] = sz;
		rm[rmOffset + 9] = uz;
		rm[rmOffset + 10] = -fz;
		rm[rmOffset + 11] = 0.0f;

		rm[rmOffset + 12] = 0.0f;
		rm[rmOffset + 13] = 0.0f;
		rm[rmOffset + 14] = 0.0f;
		rm[rmOffset + 15] = 1.0f;

		translateM(rm, rmOffset, -eyeX, -eyeY, -eyeZ);
		
	}

}
