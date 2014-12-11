package com.tyrlib2.util;

public class FloatArray {
	public float[] buffer;
	private int size;

	public static final int MIN_SIZE = 4;
	
	public FloatArray(int size) {
		buffer = new float[size * 2];
	}

	public void pushBack(float f) {
		if (size == buffer.length) {
			resize();
		}

		buffer[size++] = f;
	}

	public void popBack() {
		size--;

		if (size == buffer.length / 4 && size > MIN_SIZE) {
			resize();
		}
	}
	
	public void popBack(int number) {
		for (int i = 0; i < number; ++i) {
			popBack();
		}
	}

	private void resize() {
		float[] newBuffer = new float[size * 2];
		System.arraycopy(buffer, 0, newBuffer, 0, size);
		buffer = newBuffer;
	}
	
	public void swap(int srcPos, int dstPos, int size) {
		for (int i = 0; i < size; ++i) {
			float tmp = buffer[srcPos + i];
			buffer[srcPos + i] = buffer[dstPos + i];
			buffer[dstPos + i] = tmp;
		}
	}
	
	public float back() {
		return buffer[size - 1];
	}

	public int getSize() {
		return size;
	}
	
	public String toString() {
		StringBuilder b = new StringBuilder();
		
		for (int i = 0; i < size; ++i) {
			b.append(buffer[i]);
			
			if (i != size - 1) {
				b.append(", ");
			}
		}
		
		return b.toString();
	}

}
