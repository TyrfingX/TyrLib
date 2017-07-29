package com.tyrfing.games.tyrlib3.view.graphics.compositors;

import com.tyrfing.games.tyrlib3.view.graphics.TyrGL;

public enum Precision {
	LOW, HIGH;
	
	public int getDataPrecision() {
		if (this == LOW) {
			return TyrGL.GL_UNSIGNED_BYTE;
		} else {
			return TyrGL.GL_UNSIGNED_INT;
		}
	}
	
	public int getDataSize() {
		if (this == LOW) {
			return 1;
		} else {
			return 4;
		}
	}
	
	public int getDepthPrecision() {
		if (this == LOW) {
			return TyrGL.GL_DEPTH_COMPONENT;
		} else {
			return TyrGL.GL_DEPTH_COMPONENT32;
		}
	}
	
	public int getTextureMode() {
		if (this == LOW) {
			return TyrGL.GL_NEAREST;
		} else {
			return TyrGL.GL_LINEAR;
		}
	}
	
	
	public int getColorMode() {
		if (this == LOW) {
			return TyrGL.GL_RGB;
		} else {
			return TyrGL.GL_RGB;
		}
	}
}
