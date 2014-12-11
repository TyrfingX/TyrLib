package com.TyrLib2.PC.main;

import java.nio.Buffer;
import java.nio.ByteBuffer;
import java.nio.IntBuffer;

import javax.media.opengl.GL3;
import javax.media.opengl.GL3bc;

import com.tyrlib2.graphics.renderer.GLImpl;
import com.tyrlib2.graphics.renderer.TyrGL;


public class PCGL3 implements GLImpl {

	public static GL3 gl;
	
	public PCGL3(GL3 gl) {
		TyrGL.GL_POINTS = GL3.GL_POINTS;
		TyrGL.GL_FLOAT = GL3.GL_FLOAT;
		TyrGL.GL_SRC_ALPHA = GL3.GL_SRC_ALPHA;
		TyrGL.GL_ONE_MINUS_SRC_ALPHA = GL3.GL_ONE_MINUS_SRC_ALPHA;
		TyrGL.GL_ARRAY_BUFFER = GL3.GL_ARRAY_BUFFER;
		TyrGL.GL_TEXTURE0 = GL3.GL_TEXTURE0;
		TyrGL.GL_TEXTURE1 = GL3.GL_TEXTURE1;
		TyrGL.GL_TEXTURE2 = GL3.GL_TEXTURE2;
		TyrGL.GL_TEXTURE3 = GL3.GL_TEXTURE3;
		TyrGL.GL_TEXTURE_2D = GL3.GL_TEXTURE_2D;
		TyrGL.GL_CULL_FACE = GL3.GL_CULL_FACE;
		TyrGL.GL_TRIANGLES = GL3.GL_TRIANGLES;
		TyrGL.GL_LINES = GL3.GL_LINES;
		TyrGL.GL_FRONT = GL3.GL_FRONT;
		TyrGL.GL_BACK = GL3.GL_BACK;
		TyrGL.GL_LEQUAL = GL3.GL_LEQUAL;
		TyrGL.GL_ONE = GL3.GL_ONE;
		TyrGL.GL_DEPTH_BUFFER_BIT = GL3.GL_DEPTH_BUFFER_BIT;
		TyrGL.GL_COLOR_BUFFER_BIT = GL3.GL_COLOR_BUFFER_BIT;
		TyrGL.GL_LINE_LOOP = GL3.GL_LINE_LOOP;
		TyrGL.GL_DEPTH_TEST = GL3.GL_DEPTH_TEST;
		TyrGL.GL_BLEND = GL3.GL_BLEND;
		TyrGL.GL_LINK_STATUS = GL3.GL_LINK_STATUS;
		TyrGL.GL_VERTEX_SHADER = GL3.GL_VERTEX_SHADER;
		TyrGL.GL_FRAGMENT_SHADER = GL3.GL_FRAGMENT_SHADER;
		TyrGL.GL_UNSIGNED_SHORT = GL3.GL_UNSIGNED_SHORT;
		TyrGL.GL_COMPILE_STATUS = GL3.GL_COMPILE_STATUS;
		TyrGL.GL_INFO_LOG_LENGTH = GL3.GL_INFO_LOG_LENGTH;
		TyrGL.GL_TEXTURE_MIN_FILTER = GL3.GL_TEXTURE_MIN_FILTER;
		TyrGL.GL_LINEAR = GL3.GL_LINEAR;
		TyrGL.GL_TEXTURE_MAG_FILTER = GL3.GL_TEXTURE_MAG_FILTER;
		TyrGL.GL_LINEAR_MIPMAP_LINEAR = GL3.GL_LINEAR_MIPMAP_LINEAR;
		TyrGL.GL_FRAMEBUFFER = GL3.GL_FRAMEBUFFER;
		TyrGL.GL_COLOR_ATTACHMENT0 = GL3.GL_COLOR_ATTACHMENT0;
		TyrGL.GL_RGBA = GL3.GL_RGBA;
		TyrGL.GL_UNSIGNED_BYTE = GL3.GL_UNSIGNED_BYTE;
		TyrGL.GL_NEAREST = GL3.GL_NEAREST;
		TyrGL.GL_TEXTURE_WRAP_S = GL3.GL_TEXTURE_WRAP_S;
		TyrGL.GL_TEXTURE_WRAP_T = GL3.GL_TEXTURE_WRAP_T;
		TyrGL.GL_CLAMP_TO_EDGE = GL3.GL_CLAMP_TO_EDGE;
		TyrGL.GL_POINT_SPRITE = GL3bc.GL_POINT_SPRITE;
		TyrGL.GL_STATIC_DRAW = GL3.GL_STATIC_DRAW;
		TyrGL.GL_USE_VBO = 1;
		TyrGL.GL_ELEMENT_ARRAY_BUFFER = GL3.GL_ELEMENT_ARRAY_BUFFER;
		TyrGL.GL_STREAM_DRAW = GL3.GL_STREAM_DRAW;
		TyrGL.GL_DEPTH_COMPONENT16 = GL3.GL_DEPTH_COMPONENT16;
		TyrGL.GL_DEPTH_COMPONENT24 = GL3.GL_DEPTH_COMPONENT24;
		TyrGL.GL_DEPTH_COMPONENT = GL3.GL_DEPTH_COMPONENT;
		TyrGL.GL_DEPTH_ATTACHMENT = GL3.GL_DEPTH_ATTACHMENT;
		TyrGL.GL_RGB = GL3.GL_RGB;
		TyrGL.GL_UNSIGNED_INT = GL3.GL_UNSIGNED_INT;
		TyrGL.TARGET = TyrGL.PC_TARGET;
		PCGL3.gl = gl;
	}
	
	@Override
	public void glVertexAttrib3f(int handle, float x, float y, float z) {
		gl.glVertexAttrib3f(handle, x, y, z);
	}

	@Override
	public void glDisableVertexAttribArray(int handle) {
		gl.glDisableVertexAttribArray(handle);
	}

	@Override
	public void glUniformMatrix4fv(int location, int count, boolean transpose,
			float[] value, int offset) {
		gl.glUniformMatrix4fv(location, count, transpose, value, offset);
	}

	@Override
	public void glDrawArrays(int mode, int first, int count) {
		gl.glDrawArrays(mode, first, count);
	}

	@Override
	public int glGetAttribLocation(int programHandle, String attribute) {
		return gl.glGetAttribLocation(programHandle, attribute);
	}

	@Override
	public int glGetUniformLocation(int programHandle, String uniform) {
		return gl.glGetUniformLocation(programHandle, uniform);
	}

	@Override
	public void glVertexAttribPointer(int indx, int size, int type,
			boolean normalized, int stride, Buffer ptr) {
		//gl.glVertexAttribPointer(indx, size, type, normalized, stride, ptr);
		System.err.println("glVertexAttribPointer is DEPRECATED!");
	}

	@Override
	public void glEnableVertexAttribArray(int handle) {
		gl.glEnableVertexAttribArray(handle);
	}

	@Override
	public void glUniform4f(int handle, float x, float y, float z, float w) {
		gl.glUniform4f(handle, x, y, z, w);
	}

	@Override
	public void glVertexAttrib4f(int handle, float x, float y, float z, float w) {
		gl.glVertexAttrib4f(handle, x, y, z, w);
	}

	@Override
	public void glBindBuffer(int target, int buffer) {
		gl.glBindBuffer(target, buffer);
	}

	@Override
	public void glVertexAttribPointer(int indx, int size, int type,
			boolean normalized, int stride, int offset) {
		gl.glVertexAttribPointer(indx, size, type, normalized, stride, offset);
	}

	@Override
	public void glActiveTexture(int texture) {
		gl.glActiveTexture(texture);
	}

	@Override
	public void glBindTexture(int target, int texture) {
		//PCBitmap.bind(target, texture);
		gl.glBindTexture(target, texture);
	}

	@Override
	public void glUniform1i(int handle, int value) {
		gl.glUniform1i(handle, value);
	}

	@Override
	public void glUniform1f(int handle, float value) {
		gl.glUniform1f(handle, value);
	}

	@Override
	public void glUniform3f(int handle, float x, float y, float z) {
		gl.glUniform3f(handle, x, y, z);
	}

	@Override
	public void glUniform2f(int handle, float x, float y) {
		gl.glUniform2f(handle, x, y);
	}

	@Override
	public void glDepthMask(boolean state) {
		gl.glDepthMask(state);
	}

	@Override
	public void glDepthFunc(int depthFunc) {
		gl.glDepthFunc(depthFunc);
	}

	@Override
	public void glDisable(int cap) {
		gl.glDisable(cap);
	}

	@Override
	public void glEnable(int cap) {
		gl.glEnable(cap);
	}

	@Override
	public void glLineWidth(float width) {
		gl.glLineWidth(width);
	}

	@Override
	public void glCullFace(int mode) {
		gl.glCullFace(mode);
	}

	@Override
	public void glBlendFunc(int sfactor, int dfactor) {
		gl.glBlendFunc(sfactor, dfactor);
	}

	@Override
	public void glClearColor(float r, float g, float b, float a) {
		gl.glClearColor(r, g, b, a);
	}

	@Override
	public void glClear(int mask) {
		gl.glClear(mask);
	}

	@Override
	public void glViewport(int x, int y, int width, int height) {
		gl.glViewport(x, y, width, height);
	}

	@Override
	public void glUseProgram(int program) {
		gl.glUseProgram(program);
	}

	@Override
	public void glLinkProgram(int program) {
		gl.glLinkProgram(program);
	}

	@Override
	public int glCreateProgram() {
		return gl.glCreateProgram();
	}

	@Override
	public void glAttachShader(int program, int shader) {
		gl.glAttachShader(program, shader);
	}

	@Override
	public void glBindAttribLocation(int program, int location, String attribute) {
		gl.glBindAttribLocation(program, location, attribute);
	}

	@Override
	public void glGetProgramiv(int program, int pname, int[] params, int offset) {
		gl.glGetProgramiv(program, pname, params, offset);
	}

	@Override
	public String glGetProgramInfoLog(int program) {
		
		IntBuffer intBuffer = IntBuffer.allocate(1);
		gl.glGetProgramiv(program, GL3bc.GL_INFO_LOG_LENGTH, intBuffer);
		int size = intBuffer.get(0);
		
        ByteBuffer byteBuffer = ByteBuffer.allocate(size);
        gl.glGetProgramInfoLog(program, size, intBuffer, byteBuffer);
        String res = "";
        for (byte b : byteBuffer.array()) {
            res += (char) b;
        }
        
		return res;
	}

	@Override
	public void glDeleteProgram(int program) {
		gl.glDeleteProgram(program);
	}

	@Override
	public void glDrawElements(int mode, int count, int type, Buffer indices) {
		//gl.glDrawElements(mode, count, type, indices);
		System.err.println("glDrawElements is DEPRECATED!");
	}

	@Override
	public void glShaderSource(int shader, String shaderCode) {
		gl.glShaderSource(shader, 1, new String[] { shaderCode }, new int[] { shaderCode.length()}, 0);
	}

	@Override
	public int glCreateShader(int type) {
		return gl.glCreateShader(type);
	}

	@Override
	public void glCompileShader(int shader) {
		gl.glCompileShader(shader);
	}

	@Override
	public String glGetShaderInfoLog(int shader) {
		IntBuffer intBuffer = IntBuffer.allocate(1);
		gl.glGetShaderiv(shader, GL3.GL_INFO_LOG_LENGTH, intBuffer);
		int size = intBuffer.get(0);
		
        ByteBuffer byteBuffer = ByteBuffer.allocate(size);
        gl.glGetShaderInfoLog(shader, size, intBuffer, byteBuffer);
        String res = "";
        for (byte b : byteBuffer.array()) {
            res += (char) b;
        }
        
        return res;
	}

	@Override
	public void glGetShaderiv(int shader, int pname, int[] params, int offset) {
		gl.glGetShaderiv(shader, pname, params, offset);
	}

	@Override
	public void glDeleteShader(int shader) {
		gl.glDeleteShader(shader);
	}

	@Override
	public void glGenTextures(int n, int[] textures, int offset) {
		gl.glGenTextures(n, textures, offset);
	}

	@Override
	public void glTexParameteri(int target, int pname, int param) {
		gl.glTexParameteri(target, pname, param);
	}

	@Override
	public void glDeleteTextures(int n, int[] textures, int offset) {
		gl.glDeleteTextures(n, textures, offset);
	}

	@Override
	public void glGenFramebuffers(int n, int[] framebuffers, int offset) {
		gl.glGenFramebuffers(n, framebuffers, offset);
	}

	@Override
	public void glBindFramebuffer(int target, int framebuffer) {
		gl.glBindFramebuffer(target, framebuffer);
	}

	@Override
	public void glFramebufferTexture2D(int target, int attachment,
			int textarget, int texture, int level) {
		gl.glFramebufferTexture2D(target, attachment, textarget, texture, level);
	}

	@Override
	public void glReadPixels(int x, int y, int width, int height, int format,
			int type, Buffer pixels) {
		gl.glReadPixels(x, y, width, height, format, type, pixels);
	}

	@Override
	public void glTexParameterf(int target, int pname, float param) {
		gl.glTexParameterf(target, pname, param);
	}

	@Override
	public void glGenBuffers(int count, int[] buffers, int offset) {
		gl.glGenBuffers(count, buffers, offset);
	}

	@Override
	public void glBufferData(int target, int size, Buffer data, int mode) {
		gl.glBufferData(target, size, data, mode);
	}

	@Override
	public void glDrawElements(int mode, int count, int type, int offset) {
		gl.glDrawElements(mode, count, type, offset);
	}

	@Override
	public void glBufferSubData(int target, int offset, int size, Buffer data) {
		gl.glBufferSubData(target, offset, size, data);
	}

	@Override
	public void glGenerateMipmap(int mode) {
		gl.glGenerateMipmap(mode);
	}
	
	@Override
	public void glTexImage2D(int target, int level, int internalFormat,
							 int width, int height, int border, int format, int type, Buffer data) {
		gl.glTexImage2D(target, level, internalFormat, width, height, border, format, type, data);
	}

}
