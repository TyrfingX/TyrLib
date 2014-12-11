package com.tyrlib2.graphics.renderer;

import java.nio.Buffer;

import android.opengl.GLES20;

public class AndroidGLImpl implements GLImpl {

	public AndroidGLImpl() {
		TyrGL.GL_POINTS = GLES20.GL_POINTS;
		TyrGL.GL_FLOAT = GLES20.GL_FLOAT;
		TyrGL.GL_SRC_ALPHA = GLES20.GL_SRC_ALPHA;
		TyrGL.GL_ONE_MINUS_SRC_ALPHA = GLES20.GL_ONE_MINUS_SRC_ALPHA;
		TyrGL.GL_DST_ALPHA = GLES20.GL_DST_ALPHA;
		TyrGL.GL_SRC_COLOR = GLES20.GL_SRC_COLOR;
		TyrGL.GL_DST_COLOR = GLES20.GL_DST_COLOR;
		TyrGL.GL_ARRAY_BUFFER = GLES20.GL_ARRAY_BUFFER;
		TyrGL.GL_TEXTURE0 = GLES20.GL_TEXTURE0;
		TyrGL.GL_TEXTURE1 = GLES20.GL_TEXTURE1;
		TyrGL.GL_TEXTURE2 = GLES20.GL_TEXTURE2;
		TyrGL.GL_TEXTURE3 = GLES20.GL_TEXTURE3;
		TyrGL.GL_TEXTURE_2D = GLES20.GL_TEXTURE_2D;
		TyrGL.GL_CULL_FACE = GLES20.GL_CULL_FACE;
		TyrGL.GL_TRIANGLES = GLES20.GL_TRIANGLES;
		TyrGL.GL_LINES = GLES20.GL_LINES;
		TyrGL.GL_FRONT = GLES20.GL_FRONT;
		TyrGL.GL_BACK = GLES20.GL_BACK;
		TyrGL.GL_LEQUAL = GLES20.GL_LEQUAL;
		TyrGL.GL_ONE = GLES20.GL_ONE;
		TyrGL.GL_DEPTH_BUFFER_BIT = GLES20.GL_DEPTH_BUFFER_BIT;
		TyrGL.GL_COLOR_BUFFER_BIT = GLES20.GL_COLOR_BUFFER_BIT;
		TyrGL.GL_LINE_LOOP = GLES20.GL_LINE_LOOP;
		TyrGL.GL_DEPTH_TEST = GLES20.GL_DEPTH_TEST;
		TyrGL.GL_BLEND = GLES20.GL_BLEND;
		TyrGL.GL_LINK_STATUS = GLES20.GL_LINK_STATUS;
		TyrGL.GL_VERTEX_SHADER = GLES20.GL_VERTEX_SHADER;
		TyrGL.GL_FRAGMENT_SHADER = GLES20.GL_FRAGMENT_SHADER;
		TyrGL.GL_UNSIGNED_SHORT = GLES20.GL_UNSIGNED_SHORT;
		TyrGL.GL_COMPILE_STATUS = GLES20.GL_COMPILE_STATUS;
		TyrGL.GL_INFO_LOG_LENGTH = GLES20.GL_INFO_LOG_LENGTH;
		TyrGL.GL_TEXTURE_MIN_FILTER = GLES20.GL_TEXTURE_MIN_FILTER;
		TyrGL.GL_LINEAR = GLES20.GL_LINEAR;
		TyrGL.GL_TEXTURE_MAG_FILTER = GLES20.GL_TEXTURE_MAG_FILTER;
		TyrGL.GL_LINEAR_MIPMAP_LINEAR = GLES20.GL_LINEAR_MIPMAP_LINEAR;
		TyrGL.GL_FRAMEBUFFER = GLES20.GL_FRAMEBUFFER;
		TyrGL.GL_COLOR_ATTACHMENT0 = GLES20.GL_COLOR_ATTACHMENT0;
		TyrGL.GL_RGBA = GLES20.GL_RGBA;
		TyrGL.GL_UNSIGNED_BYTE = GLES20.GL_UNSIGNED_BYTE;
		TyrGL.GL_NEAREST = GLES20.GL_NEAREST;
		TyrGL.GL_TEXTURE_WRAP_S = GLES20.GL_TEXTURE_WRAP_S;
		TyrGL.GL_TEXTURE_WRAP_T = GLES20.GL_TEXTURE_WRAP_T;
		TyrGL.GL_CLAMP_TO_EDGE = GLES20.GL_CLAMP_TO_EDGE;
		TyrGL.GL_POINT_SPRITE = -1;
		TyrGL.GL_STATIC_DRAW = GLES20.GL_STATIC_DRAW;
		TyrGL.GL_USE_VBO = -1;
		TyrGL.GL_ELEMENT_ARRAY_BUFFER = GLES20.GL_ELEMENT_ARRAY_BUFFER;
		TyrGL.GL_STREAM_DRAW = GLES20.GL_STREAM_DRAW;
		TyrGL.GL_DEPTH_COMPONENT16 = GLES20.GL_DEPTH_COMPONENT16;
		TyrGL.GL_DEPTH_COMPONENT24 = GLES20.GL_DEPTH_COMPONENT16;
		TyrGL.GL_DEPTH_COMPONENT = GLES20.GL_DEPTH_COMPONENT;
		TyrGL.GL_DEPTH_ATTACHMENT = GLES20.GL_DEPTH_ATTACHMENT;
		TyrGL.GL_RGB = GLES20.GL_RGB;
		TyrGL.GL_UNSIGNED_INT = GLES20.GL_UNSIGNED_INT;
		TyrGL.TARGET = TyrGL.ANDROID_TARGET;
	}
	
	@Override
	public void glVertexAttrib3f(int handle, float x, float y, float z) {
		GLES20.glVertexAttrib3f(handle, x, y, z);
	}

	@Override
	public void glDisableVertexAttribArray(int handle) {
		GLES20.glDisableVertexAttribArray(handle);
	}
	@Override
	public void glUniformMatrix4fv(int location, int count, boolean transpose, float[] value, int offset) {
		GLES20.glUniformMatrix4fv(location, count, transpose, value, offset);
	}

	@Override
	public void glDrawArrays(int mode, int first, int count) {
		GLES20.glDrawArrays(mode, first, count);
	}

	@Override
	public int glGetAttribLocation(int programHandle, String attribute) {
		return GLES20.glGetAttribLocation(programHandle, attribute);
	}

	@Override
	public int glGetUniformLocation(int programHandle, String uniform) {
		return GLES20.glGetUniformLocation(programHandle, uniform);
	}

	@Override
	public void glVertexAttribPointer(int indx, int size, int type, boolean normalized, int stride, Buffer ptr) {
		GLES20.glVertexAttribPointer(indx, size, type, normalized, stride, ptr);
	}

	@Override
	public void glEnableVertexAttribArray(int handle) {
		GLES20.glEnableVertexAttribArray(handle);
	}

	@Override
	public void glUniform4f(int handle, float x, float y, float z, float w) {
		GLES20.glUniform4f(handle, x, y, z, w);
	}

	@Override
	public void glVertexAttrib4f(int handle, float x, float y, float z, float w) {
		GLES20.glVertexAttrib4f(handle, x, y, z, w);
	}
	
	@Override
	public void glBindBuffer(int target, int buffer) {
		GLES20.glBindBuffer(target, buffer);
	}

	@Override
	public void glVertexAttribPointer(int indx, int size, int type, boolean normalized, int stride, int offset) {
		GLES20.glVertexAttribPointer(indx, size, type, normalized, stride, offset);
	}

	@Override
	public void glActiveTexture(int texture) {
		GLES20.glActiveTexture(texture);
	}

	@Override
	public void glBindTexture(int target, int texture) {
		GLES20.glBindTexture(target, texture);
	}

	@Override
	public void glUniform1i(int handle, int value) {
		GLES20.glUniform1i(handle, value);
	}

	@Override
	public void glUniform1f(int handle, float value) {
		GLES20.glUniform1f(handle, value);
	}

	@Override
	public void glUniform3f(int handle, float x, float y, float z) {
		GLES20.glUniform3f(handle, x, y, z);
	}

	@Override
	public void glUniform2f(int handle, float x, float y) {
		GLES20.glUniform2f(handle, x, y);
	}

	@Override
	public void glDepthMask(boolean state) {
		GLES20.glDepthMask(state);
	}

	@Override
	public void glDisable(int cap) {
		GLES20.glDisable(cap);
	}

	@Override
	public void glEnable(int cap) {
		GLES20.glEnable(cap);
	}

	@Override
	public void glLineWidth(float width) {
		GLES20.glLineWidth(width);
	}

	@Override
	public void glCullFace(int mode) {
		GLES20.glCullFace(mode);
	}

	@Override
	public void glDepthFunc(int depthFunc) {
		GLES20.glDepthFunc(depthFunc);
	}

	@Override
	public void glBlendFunc(int sfactor, int dfactor) {
		GLES20.glBlendFunc(sfactor, dfactor);
	}

	@Override
	public void glClearColor(float r, float g, float b, float a) {
		GLES20.glClearColor(r, g, b, a);
	}

	@Override
	public void glClear(int mask) {
		GLES20.glClear(mask);
	}

	@Override
	public void glViewport(int x, int y, int width, int height) {
		GLES20.glViewport(x, y, width, height);
	}

	@Override
	public void glUseProgram(int program) {
		GLES20.glUseProgram(program);
	}

	@Override
	public void glLinkProgram(int program) {
		GLES20.glLinkProgram(program);
	}

	@Override
	public int glCreateProgram() {
		return GLES20.glCreateProgram();
	}

	@Override
	public void glAttachShader(int program, int shader) {
		GLES20.glAttachShader(program, shader);
	}

	@Override
	public void glBindAttribLocation(int program, int location, String attribute) {
		GLES20.glBindAttribLocation(program, location, attribute);
	}

	@Override
	public void glGetProgramiv(int program, int pname, int[] params, int offset) {
		GLES20.glGetProgramiv(program, pname, params, offset);
	}

	@Override
	public void glDeleteProgram(int program) {
		GLES20.glDeleteProgram(program);
	}

	@Override
	public void glDrawElements(int mode, int count, int type, Buffer indices) {
		GLES20.glDrawElements(mode, count, type, indices);
	}

	@Override
	public void glShaderSource(int shader, String shaderCode) {
		GLES20.glShaderSource(shader, shaderCode);
	}

	@Override
	public int glCreateShader(int type) {
		return GLES20.glCreateShader(type);
	}

	@Override
	public void glCompileShader(int shader) {
		GLES20.glCompileShader(shader);
	}

	@Override
	public void glGetShaderiv(int shader, int pname, int[] params, int offset) {
		GLES20.glGetShaderiv(shader, pname, params, offset);
	}

	@Override
	public void glDeleteShader(int shader) {
		GLES20.glDeleteShader(shader);
	}

	@Override
	public void glGenTextures(int n, int[] textures, int offset) {
		GLES20.glGenTextures(n, textures, offset);
	}

	@Override
	public void glTexParameteri(int target, int pname, int param) {
		GLES20.glTexParameteri(target, pname, param);
	}

	@Override
	public void glDeleteTextures(int n, int[] textures, int offset) {
		GLES20.glDeleteTextures(n, textures, offset);
	}

	@Override
	public void glGenFramebuffers(int n, int[] framebuffers, int offset) {
		GLES20.glGenFramebuffers(n, framebuffers, offset);
	}

	@Override
	public void glBindFramebuffer(int target, int framebuffer) {
		GLES20.glBindFramebuffer(target, framebuffer);
	}

	@Override
	public void glFramebufferTexture2D(int target, int attachment, int textarget, int texture, int level) {
		GLES20.glFramebufferTexture2D(target, attachment, textarget, texture, level);
	}

	@Override
	public void glReadPixels(int x, int y, int width, int height, int format, int type, Buffer pixels) {
		GLES20.glReadPixels(x, y, width, height, format, type, pixels);
	}

	@Override
	public void glTexParameterf(int target, int pname, float param) {
		GLES20.glTexParameterf(target, pname, param);
	}

	@Override
	public String glGetProgramInfoLog(int program) {
		return GLES20.glGetProgramInfoLog(program);
	}

	@Override
	public String glGetShaderInfoLog(int shader) {
		return GLES20.glGetShaderInfoLog(shader);
	}

	@Override
	public void glGenBuffers(int count, int[] buffers, int offset) {
		GLES20.glGenBuffers(count, buffers, offset);
	}

	@Override
	public void glBufferData(int target, int size, Buffer data, int mode) {
		GLES20.glBufferData(target, size, data, mode);
	}

	@Override
	public void glDrawElements(int mode, int count, int type, int offset) {

	}

	@Override
	public void glBufferSubData(int target, int offset, int size, Buffer data) {
	}

	@Override
	public void glGenerateMipmap(int mode) {
		GLES20.glGenerateMipmap(mode);
	}

	@Override
	public void glTexImage2D(int target, int level, int internalFormat,
							 int width, int height, int border, int format, int type, Buffer data) {
		GLES20.glTexImage2D(target, level, internalFormat, width, height, border, format, type, data);
	}

}
