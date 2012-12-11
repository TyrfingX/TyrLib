package com.tyrlib2.materials;

import java.nio.FloatBuffer;

import android.opengl.GLES20;

import com.tyrlib2.renderer.Material;
import com.tyrlib2.renderer.ProgramManager;

/**
 * A material for simple per vertex colouring
 * @author Sascha
 *
 */

public class BasicSingleColorMaterial extends Material {

	private int colorHandle;
	private Color color;
	private float[] colorArray;
	
	public BasicSingleColorMaterial(Color color) {
		
		this.color = color;
		
		program = ProgramManager.getInstance().getProgram("BASIC2");
		init(3,0,3, "uMVPMatrix", "vPosition");
		colorHandle = GLES20.glGetUniformLocation(program.handle, "vColor");
		colorArray = new float[]{ color.r, color.g, color.b, color.a };
	}
	
	public void render(FloatBuffer vertexBuffer, float[] modelMatrix) {
        // Set color for drawing
        GLES20.glUniform4fv(colorHandle, 1, colorArray, 0);
	}
	
	public Color getColor() {
		return color;
	}
	
}
