package com.tyrfing.games.tyrlib3.view.graphics.text.programs;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;

import com.tyrfing.games.tyrlib3.view.graphics.TyrGL;
import com.tyrfing.games.tyrlib3.view.graphics.text.AttribVariable;

public class Utilities {

	public static final int BYTES_PER_FLOAT = 4;
	public static final int BYTES_PER_SHORT = 2;
	
	public static int createProgram(int vertexShaderHandle, int fragmentShaderHandle, AttribVariable[] variables) {
		int  mProgram = TyrGL.glCreateProgram();
		
		if (mProgram != 0) {
			TyrGL.glAttachShader(mProgram, vertexShaderHandle);
	        TyrGL.glAttachShader(mProgram, fragmentShaderHandle);
	
	        for (AttribVariable var: variables) {
	        	TyrGL.glBindAttribLocation(mProgram, var.getHandle(), var.getName());
	        }   
	        
	        TyrGL.glLinkProgram(mProgram);
	     
	        final int[] linkStatus = new int[1];
	        TyrGL.glGetProgramiv(mProgram, TyrGL.GL_LINK_STATUS, linkStatus, 0);
	
	        if (linkStatus[0] == 0)
	        {
	        	TyrGL.glDeleteProgram(mProgram);
	            mProgram = 0;
	        }
	    }
	     
	    if (mProgram == 0)
	    {
	        throw new RuntimeException("Error creating program.");
	    }
		return mProgram;
	}

	public static int loadShader(int type, String shaderCode){
	    int shaderHandle = TyrGL.glCreateShader(type);
	     
	    if (shaderHandle != 0)
	    {
	    	TyrGL.glShaderSource(shaderHandle, shaderCode);
	    	TyrGL.glCompileShader(shaderHandle);
	    
	        // Get the compilation status.
	        final int[] compileStatus = new int[1];
	        TyrGL.glGetShaderiv(shaderHandle, TyrGL.GL_COMPILE_STATUS, compileStatus, 0);
	     
	        // If the compilation failed, delete the shader.
	        if (compileStatus[0] == 0)
	        {
	        	TyrGL.glDeleteShader(shaderHandle);
	            shaderHandle = 0;
	        }
	    }
	    
	     
	    if (shaderHandle == 0)
	    {
	        throw new RuntimeException("Error creating shader " + type);
	    }
	    return shaderHandle;
	}

	public static FloatBuffer newFloatBuffer(float[] verticesData) {
		FloatBuffer floatBuffer;
		floatBuffer = ByteBuffer.allocateDirect(verticesData.length * BYTES_PER_FLOAT)
				.order(ByteOrder.nativeOrder()).asFloatBuffer();
		floatBuffer.put(verticesData).position(0);
		return floatBuffer;
	}
}
