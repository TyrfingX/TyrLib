package com.tyrlib2.renderer;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import java.nio.ShortBuffer;

/**
 * This class represents a mesh, containing the neccessary vertex data, etc.
 * @author Sascha
 *
 */

public class Mesh {
	protected FloatBuffer vertexBuffer;
	protected ShortBuffer drawListBuffer;
	protected float[] vertexData;
	protected short[] drawOrder;
	
	public Mesh(float[] vertexData, short[] drawOrder) {
		this.vertexData = vertexData;
		this.drawOrder = drawOrder;
		
	    // initialize vertex byte buffer for shape coordinates
        ByteBuffer bb = ByteBuffer.allocateDirect(vertexData.length * OpenGLRenderer.BYTES_PER_FLOAT);
        
        // use the device hardware's native byte order
        bb.order(ByteOrder.nativeOrder());

        // create a floating point buffer from the ByteBuffer
        vertexBuffer = bb.asFloatBuffer();
        // add the coordinates to the FloatBuffer
        vertexBuffer.put(vertexData);
        // set the buffer to read the first coordinate
        vertexBuffer.position(0);
        
        // initialize byte buffer for the draw list
        ByteBuffer dlb = ByteBuffer.allocateDirect(
        // (# of coordinate values * 2 bytes per short)
                drawOrder.length * 2);
        dlb.order(ByteOrder.nativeOrder());
        drawListBuffer = dlb.asShortBuffer();
        drawListBuffer.put(drawOrder);
        drawListBuffer.position(0);

	}
	
	public float[] getVertexData() {
		return vertexData;
	}
	
	public short[] getDrawOrder() {
		return drawOrder;
	}
}
