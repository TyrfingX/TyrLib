package com.tyrlib2.util;

import com.tyrlib2.graphics.renderer.Mesh;

/**
 * Creates various meshes
 * @author Sascha
 *
 */

public class MeshCreator {
	
    public static Mesh createSphere(float radius, int rings, int sectors, int posOffset, int normalOffset, int uvOffset, int repeatX, int repeatY, int colorOffset, Color color, int vertexSize) {
        float R = 1.f/(float)(rings-1);
        float S = 1.f/(float)(sectors-1);
        int r, s;
        
        int vertexCount = rings * sectors;
        
        float[] vertexData = new float[vertexCount * vertexSize];
        int vertexPos = 0;
        
        for(r = 0; r < rings; r++) {
        	for(s = 0; s < sectors; s++) {
                float y = (float)Math.sin(-Math.PI/2 + Math.PI * r * R );
                float x = (float)(Math.cos(2*Math.PI * s * S) * Math.sin( Math.PI * r * R ));
                float z = (float)(Math.sin(2*Math.PI * s * S) * Math.sin( Math.PI * r * R ));
                
                if (uvOffset >= 0) {
                	vertexData[vertexPos + uvOffset + 0] = s*S * repeatX;
                	vertexData[vertexPos + uvOffset + 1] = r*R * repeatY;
                }
                
                if (posOffset >= 0) {
                	vertexData[vertexPos + posOffset + 0] = x * radius;
                	vertexData[vertexPos + posOffset + 1] = y * radius;
                	vertexData[vertexPos + posOffset + 2] = z * radius;
                }
                
                if (normalOffset >= 0) {
                	vertexData[vertexPos + normalOffset + 0] = x;
                	vertexData[vertexPos + normalOffset + 1] = y;
                	vertexData[vertexPos + normalOffset + 2] = z;
                }
                
                if (colorOffset >= 0) {
                	if (color != null) {
                    	vertexData[vertexPos + colorOffset + 0] = color.r;
                    	vertexData[vertexPos + colorOffset + 1] = color.g;
                    	vertexData[vertexPos + colorOffset + 2] = color.b;
                    	vertexData[vertexPos + colorOffset + 3] = color.a;
                	} else {
                    	vertexData[vertexPos + colorOffset + 0] = 1;
                    	vertexData[vertexPos + colorOffset + 1] = 1;
                    	vertexData[vertexPos + colorOffset + 2] = 1;
                    	vertexData[vertexPos + colorOffset + 3] = 1;
                	}
                }
                vertexPos += vertexSize;
        	}
        }

        short[] indexData = new short[vertexCount * 6];
        int indexPos = 0;
        for(r = 0; r < rings-1; r++) {
        	for(s = 0; s < sectors-1; s++) {
                indexData[indexPos++] = (short)(r * sectors + s);
                indexData[indexPos++] = (short)((r+1) * sectors + s);
                indexData[indexPos++] = (short)(r * sectors + (s+1));
                
                indexData[indexPos++] = (short)(r * sectors + (s+1));
                indexData[indexPos++] = (short)((r+1) * sectors + s);
                indexData[indexPos++] = (short)((r+1) * sectors + (s+1));
        	}
        }
        
        return new Mesh(vertexData, indexData, vertexCount);
    }
}
