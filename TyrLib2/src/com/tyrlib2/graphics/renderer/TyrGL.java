package com.tyrlib2.graphics.renderer;

import java.nio.Buffer;

public class TyrGL {
	public static GLImpl IMPL;
	
	public static int GL_POINTS;
	public static int GL_FLOAT;
	public static int GL_SRC_ALPHA;
	public static int GL_ONE_MINUS_SRC_ALPHA;
	public static int GL_ARRAY_BUFFER;
	public static int GL_TEXTURE0;
	public static int GL_TEXTURE_2D;
	public static int GL_CULL_FACE;
	public static int GL_TRIANGLES;
	public static int GL_LINES;
	public static int GL_FRONT;
	public static int GL_BACK;
	public static int GL_LEQUAL;
	public static int GL_ONE;
	public static int GL_DEPTH_BUFFER_BIT;
	public static int GL_COLOR_BUFFER_BIT;
	public static int GL_LINE_LOOP;
	public static int GL_DEPTH_TEST;
	public static int GL_BLEND;
	public static int GL_LINK_STATUS;
	public static int GL_VERTEX_SHADER;
	public static int GL_FRAGMENT_SHADER;
	public static int GL_UNSIGNED_SHORT;
	public static int GL_COMPILE_STATUS;
	public static int GL_TEXTURE_MIN_FILTER;
	public static int GL_LINEAR;
	public static int GL_TEXTURE_MAG_FILTER;
	public static int GL_LINEAR_MIPMAP_LINEAR;
	public static int GL_FRAMEBUFFER;
	public static int GL_COLOR_ATTACHMENT0;
	public static int GL_RGBA;
	public static int GL_UNSIGNED_BYTE;
	public static int GL_NEAREST;
	public static int GL_TEXTURE_WRAP_S;
	public static int GL_TEXTURE_WRAP_T;
	public static int GL_CLAMP_TO_EDGE;
	public static int GL_POINT_SPRITE;
	public static int GL_STATIC_DRAW;
	public static int GL_USE_VBO;
	public static int GL_ELEMENT_ARRAY_BUFFER;
	public static int GL_STREAM_DRAW;
	
	public static void glVertexAttrib3f(int handle, float x, float y, float z) {
		IMPL.glVertexAttrib3f(handle, x, y, z);
	}
	
	public static void glDisableVertexAttribArray(int handle) {
		IMPL.glDisableVertexAttribArray(handle);
	}
	
	public static void glUniformMatrix4fv(int location, int count, boolean transpose, float[] value, int offset) {
		IMPL.glUniformMatrix4fv(location, count, transpose, value, offset);
	}
	
	public static void glDrawArrays(int mode, int first, int count) {
		IMPL.glDrawArrays(mode, first, count);
	}
	
	public static int glGetAttribLocation(int programHandle, String attribute) {
		return IMPL.glGetAttribLocation(programHandle, attribute);
	}
	
	public static int glGetUniformLocation(int programHandle, String uniform) {
		return IMPL.glGetUniformLocation(programHandle, uniform);
	}
	
	public static void glVertexAttribPointer(int indx, int size, int type, boolean normalized, int stride, Buffer ptr) {
		IMPL.glVertexAttribPointer(indx, size, type, normalized, stride, ptr);
	}
	
	public static void glEnableVertexAttribArray(int handle) {
		IMPL.glEnableVertexAttribArray(handle);
	}
	
	public static void glUniform4f(int handle, float x, float y, float z, float w) {
		IMPL.glUniform4f(handle, x, y, z, w);
	}
	
	public static void glVertexAttrib4f(int handle, float x, float y, float z, float w) {
		IMPL.glVertexAttrib4f(handle, x, y, z, w);
	}
	
	public static void glBindBuffer(int target, int buffer) {
		IMPL.glBindBuffer(target, buffer);
	}
	
	public static void glVertexAttribPointer(int indx, int size, int type, boolean normalized, int stride, int offset) {
		IMPL.glVertexAttribPointer(indx, size, type, normalized, stride, offset);
	}
	
	public static void glActiveTexture(int texture) {
		IMPL.glActiveTexture(texture);
	}
	
	public static void glBindTexture(int target, int texture) {
		IMPL.glBindTexture(target, texture);
	}
	
	public static void glUniform1i(int handle, int value) {
		IMPL.glUniform1i(handle, value);
	}
	
	public static void glUniform1f(int handle, float value) {
		IMPL.glUniform1f(handle, value);
	}
	
	public static void glUniform3f(int handle, float x, float y, float z) {
		IMPL.glUniform3f(handle, x, y, z);
	}
	
	public static void glUniform2f(int handle, float x, float y) {
		IMPL.glUniform2f(handle, x, y);
	}
	
	public static void glDepthMask(boolean state) {
		IMPL.glDepthMask(state);
	}
	
	public static void glDisable(int cap) {
		IMPL.glDisable(cap);
	}
	
	public static void glEnable(int cap) {
		IMPL.glEnable(cap);
	}
	
	public static void glLineWidth(float width) {
		IMPL.glLineWidth(width);
	}
	
	public static void glCullFace(int mode) {
		IMPL.glCullFace(mode);
	}
	
	public static void glDepthFunc(int depthFunc) {
		IMPL.glDepthFunc(depthFunc);
	}
	
	public static void glBlendFunc(int sfactor, int dfactor) {
		IMPL.glBlendFunc(sfactor, dfactor);
	}
	
	public static void glClearColor(float r, float g, float b, float a) {
		IMPL.glClearColor(r, g, b, a);
	}
	
	public static void glClear(int mask) {
		IMPL.glClear(mask);
	}
	
	public static void glViewport(int x, int y, int width, int height) {
		IMPL.glViewport(x, y, width, height);
	}
	
	public static void glUseProgram(int program) {
		IMPL.glUseProgram(program);
	}
	
	public static void glLinkProgram(int program) {
		IMPL.glLinkProgram(program);
	}
	
	public static int glCreateProgram() {
		return IMPL.glCreateProgram();
	}
	
	public static void glAttachShader(int program, int shader) {
		IMPL.glAttachShader(program, shader);
	}
	
	public static void glBindAttribLocation(int program, int location, String attribute) {
		IMPL.glBindAttribLocation(program, location, attribute);
	}
	
	public static void glGetProgramiv(int program, int pname, int[] params, int offset) {
		IMPL.glGetProgramiv(program, pname, params, offset);
	}
	
	public static void glDeleteProgram(int program) {
		IMPL.glDeleteProgram(program);
	}
	
	public static void glDrawElements(int mode, int count, int type, Buffer indices) {
		IMPL.glDrawElements(mode, count, type, indices);
	}
	
	public static void glDrawElements(int mode, int count, int type, int offset) {
		IMPL.glDrawElements(mode, count, type, offset);
	}
	
	public static void glShaderSource(int shader, String shaderCode) {
		IMPL.glShaderSource(shader, shaderCode);
	}
	
	public static int glCreateShader(int type) {
		return IMPL.glCreateShader(type);
	}
	
	public static void glCompileShader(int shader) {
		IMPL.glCompileShader(shader);
	}
	
	public static void glGetShaderiv(int shader, int pname, int[] params, int offset) {
		IMPL.glGetShaderiv(shader, pname, params, offset);
	}
	
	public static void glDeleteShader(int shader) {
		IMPL.glDeleteShader(shader);
	}
	
	public static void glGenTextures(int n, int[] textures, int offset) {
		IMPL.glGenTextures(n, textures, offset);
	}
	
	public static void glTexParameteri(int target, int pname, int param) {
		IMPL.glTexParameteri(target, pname, param);
	}
	
	public static void glDeleteTextures(int n, int[] textures, int offset) {
		IMPL.glDeleteTextures(n, textures, offset);
	}
	
	public static void glGenFramebuffers(int n, int[] framebuffers, int offset) {
		IMPL.glGenFramebuffers(n, framebuffers, offset);
	}
	
	public static void glBindFramebuffer(int target, int framebuffer) {
		IMPL.glBindFramebuffer(target, framebuffer);
	}
	
	public static void glFramebufferTexture2D(int target, int attachment, int textarget, int texture, int level) {
		IMPL.glFramebufferTexture2D(target, attachment, textarget, texture, level);
	}
	
	public static void glReadPixels(int x, int y, int width, int height, int format, int type, Buffer pixels) {
		IMPL.glReadPixels(x, y, width, height, format, type, pixels);
	}
	
	public static void glTexParameterf(int target, int pname, float param) {
		IMPL.glTexParameterf(target, pname, param);
	}
	
	public static String glGetProgramInfoLog(int program) {
		return IMPL.glGetProgramInfoLog(program);
	}
	
	public static String glGetShaderInfoLog(int shader) {
		return IMPL.glGetShaderInfoLog(shader);
	}
	
	public static void glGenBuffers(int count, int[] buffers, int offset) {
		IMPL.glGenBuffers(count, buffers, offset);
	}
	
	public static void glBufferData(int target, int size, Buffer data, int mode) {
		IMPL.glBufferData(target, size, data, mode);
	}
	
	public static void glBufferSubData(int target, int offset, int size, Buffer data) {
		IMPL.glBufferSubData(target, offset, size, data);
	}
}
