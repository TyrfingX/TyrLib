package com.tyrfing.games.id17.world;

import com.tyrlib2.graphics.animation.Skeleton;
import com.tyrlib2.graphics.materials.DefaultMaterial3;
import com.tyrlib2.graphics.renderer.Material;
import com.tyrlib2.graphics.renderer.Mesh;
import com.tyrlib2.graphics.renderer.ProgramManager;
import com.tyrlib2.graphics.renderer.TyrGL;
import com.tyrlib2.math.Matrix;
import com.tyrlib2.util.Color;

public class FogMaterial  extends Material {
	
	private int modelMatrixHandle;
	private int colorHandle;
	
	private static final float[] mvpMatrix = new float[16];
	
	private Material old;
	private Skeleton skeleton;
	
	private Color color;
	
	public FogMaterial(Material old, Skeleton skeleton) {
		program = ProgramManager.getInstance().getProgram("TERRAIN_FOG");
		lighted = false;
		this.skeleton = skeleton;
		this.old = old;
		this.castShadow = false;
		init(DefaultMaterial3.posOffset,3, "u_MVPMatrix", "a_Position");
		
		vertexLayout.setBytestride(old.getVertexLayout().getByteStride());
	}
	
	public FogMaterial(Material old, Color color) {
		program = ProgramManager.getInstance().getProgram("TERRAIN_STRATEGIC");
		lighted = false;
		this.old = old;
		init(DefaultMaterial3.posOffset,3, "u_MVPMatrix", "a_Position");
		
		vertexLayout.setBytestride(old.getVertexLayout().getByteStride());
	}
	
	@Override
	public void updateHandles() {
		super.updateHandles();
		modelMatrixHandle = TyrGL.glGetUniformLocation(program.handle, "u_M");
		
		if (color != null) {
			colorHandle = TyrGL.glGetUniformLocation(program.handle, "u_Color");
		}
	}
	
	@Override
	public void render(Mesh mesh, float[] modelMatrix) {
		super.render(mesh, modelMatrix);
		
		if (color != null) {
			TyrGL.glUniform4f(colorHandle, color.r, color.g, color.b, color.a);
		}
		
		Matrix.setLookAtM(	mvpMatrix, 
				0, 
				0,0,1, 
				0,0,0,
				1,0,0);

        // Apply the projection and view transformation
		Matrix.multiplyMM(mvpMatrix, 0, mvpMatrix, 0, modelMatrix, 0);
        // Combine the rotation matrix with the projection and camera view
		TyrGL.glUniformMatrix4fv(modelMatrixHandle, 1, false, mvpMatrix, 0);
		
	}
	
	public Skeleton getOldSkeleton()  {
		return skeleton;
	}

	public Material getOldMaterial() {
		return old;
	}
}
