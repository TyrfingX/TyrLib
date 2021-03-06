package com.tyrlib2.graphics.text.programs;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;

import android.util.Log;

import com.tyrlib2.graphics.renderer.TyrGL;
import com.tyrlib2.graphics.text.AttribVariable;

public class Utilities {

	public static final int BYTES_PER_FLOAT = 4;
	public static final int BYTES_PER_SHORT = 2;
	private static final String TAG = "Utilities";
	
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
	        	Log.v(TAG, TyrGL.glGetProgramInfoLog(mProgram));
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
	        	Log.v(TAG, "Shader fail info: " + TyrGL.glGetShaderInfoLog(shaderHandle));
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
