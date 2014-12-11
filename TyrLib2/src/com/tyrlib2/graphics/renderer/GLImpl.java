package com.tyrlib2.graphics.renderer;

import java.nio.Buffer;

public interface GLImpl {
	
	public void glVertexAttrib3f(int handle, float x, float y, float z);
	public void glDisableVertexAttribArray(int handle);
	public void glUniformMatrix4fv(int location, int count, boolean transpose, float[] value, int offset);
	public void glDrawArrays(int mode, int first, int count);
	public int glGetAttribLocation(int programHandle, String attribute);
	public int glGetUniformLocation(int programHandle, String uniform);
	public void glVertexAttribPointer(int indx, int size, int type, boolean normalized, int stride, Buffer ptr);
	public void glEnableVertexAttribArray(int handle);
	public void glUniform4f(int handle, float x, float y, float z, float w);
	public void glVertexAttrib4f(int handle, float x, float y, float z, float w);
	public void glBindBuffer(int target, int buffer);
	public void glVertexAttribPointer(int indx, int size, int type, boolean normalized, int stride, int offset);
	public void glActiveTexture(int texture);
	public void glBindTexture(int target, int texture);
	public void glUniform1i(int handle, int value);
	public void glUniform1f(int handle, float value);
	public void glUniform3f(int handle, float x, float y, float z);
	public void glUniform2f(int handle, float x, float y);
	public void glDepthMask(boolean state);
	public void glDepthFunc(int depthFunc);
	public void glDisable(int cap);
	public void glEnable(int cap);
	public void glLineWidth(float width);
	public void glCullFace(int mode);
	public void glBlendFunc(int sfactor, int dfactor);
	public void glClearColor(float r, float g, float b, float a);
	public void glClear(int mask);
	public void glViewport(int x, int y, int width, int height);
	public void glUseProgram(int program);
	public void glLinkProgram(int program);
	public int glCreateProgram();
	public void glAttachShader(int program, int shader);
	public void glBindAttribLocation(int program, int location, String attribute);
	public void glGetProgramiv(int program, int pname, int[] params, int offset);
	public String glGetProgramInfoLog(int program);
	public void glDeleteProgram(int program);
	public void glDrawElements(int mode, int count, int type, Buffer indices);
	public void glDrawElements(int mode, int count, int type, int offset);
	public void glShaderSource(int shader, String shaderCode);
	public int glCreateShader(int type);
	public void glCompileShader(int shader);
	public String glGetShaderInfoLog(int shader);
	public void glGetShaderiv(int shader, int pname, int[] params, int offset);
	public void glDeleteShader(int shader);
	public void glGenTextures(int n, int[] textures, int offset);
	public void glTexParameteri(int target, int pname, int param);
	public void glDeleteTextures(int n, int[] textures, int offset);
	public void glGenFramebuffers(int n, int[] framebuffers, int offset);
	public void glBindFramebuffer(int target, int framebuffer);
	public void glFramebufferTexture2D(int target, int attachment, int textarget, int texture, int level);
	public void glReadPixels(int x, int y, int width, int height, int format, int type, Buffer pixels);
	public void glTexParameterf(int target, int pname, float param);
	public void glGenBuffers(int count, int[] buffers, int offset);
	public void glBufferData(int target, int size, Buffer data, int mode);
	public void glBufferSubData(int target, int offset, int size, Buffer data);
	public void glGenerateMipmap(int mode);
	public void glTexImage2D(int target, int level, int internalFormat,
			int width, int height, int border, int format, int type, Buffer data);
}
