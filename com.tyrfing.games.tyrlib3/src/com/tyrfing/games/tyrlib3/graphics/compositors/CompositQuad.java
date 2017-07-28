package com.tyrfing.games.tyrlib3.graphics.compositors;

import com.tyrfing.games.tyrlib3.graphics.renderables.Quad;
import com.tyrfing.games.tyrlib3.graphics.renderer.Mesh;
import com.tyrfing.games.tyrlib3.graphics.renderer.OpenGLRenderer;
import com.tyrfing.games.tyrlib3.graphics.renderer.TyrGL;
import com.tyrfing.games.tyrlib3.math.Vector2F;

public class CompositQuad {
	public static final short[] DRAW_ORDER = Quad.DRAW_ORDER_QUAD;
	
	private Mesh mesh;
	
	public int positionHandle;
	
	public CompositQuad(Vector2F[] points) {
		int byteStride = 2;
		float[] vertexData = new float[byteStride * points.length];
		int positionOffset = 0;
		
		// Populate the vertex data
		for (int i = 0; i < points.length; ++i) {
			int pos = byteStride * i + positionOffset;
			vertexData[pos + positionOffset + 0] = points[i].x;
			vertexData[pos + positionOffset + 1] = points[i].y;
		}
		
		mesh = new Mesh(vertexData, DRAW_ORDER, vertexData.length / byteStride, false);
	}
	
	public void render() {
        if (mesh.isUsingIBO()) {
        	TyrGL.glDrawElements(TyrGL.GL_TRIANGLES, DRAW_ORDER.length, TyrGL.GL_UNSIGNED_SHORT, 0);	
        } else {
        	TyrGL.glDrawElements(TyrGL.GL_TRIANGLES, DRAW_ORDER.length, TyrGL.GL_UNSIGNED_SHORT, mesh.getDrawOrderBuffer());	
        }
	}
	
	public void unbind() {
		if (mesh.isUsingVBO()) {
			TyrGL.glBindBuffer(TyrGL.GL_ARRAY_BUFFER, 0);
		}
		
        if (mesh.isUsingIBO()) {
        	TyrGL.glBindBuffer(TyrGL.GL_ELEMENT_ARRAY_BUFFER, 0);
        }
	}

	public void bind() {
		
		if (mesh.isUsingVBO()) {
			TyrGL.glBindBuffer(TyrGL.GL_ARRAY_BUFFER, mesh.getVBOBuffer());
		}
		
        if (mesh.isUsingIBO()) {
        	TyrGL.glBindBuffer(TyrGL.GL_ELEMENT_ARRAY_BUFFER, mesh.getIBOBuffer());
        }
		
		TyrGL.glEnableVertexAttribArray(positionHandle);
		
    	if (mesh.isUsingVBO()) {
    		
	        // Prepare the coordinate data
	        TyrGL.glVertexAttribPointer(positionHandle,  2,
	        							TyrGL.GL_FLOAT, false,
	                                     2 * OpenGLRenderer.BYTES_PER_FLOAT, 
	                                     0);
        
    	} else {
    		
        	mesh.vertexBuffer.position(0);
	
	        // Prepare the coordinate data
	        TyrGL.glVertexAttribPointer(positionHandle,2,
	        							TyrGL.GL_FLOAT, false,
	                                     2 * OpenGLRenderer.BYTES_PER_FLOAT, 
	                                     mesh.vertexBuffer);
    	}
	}

	public Object getMesh() {
		return mesh;
	}
}
