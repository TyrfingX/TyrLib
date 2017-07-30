package com.tyrfing.games.id17.gui.technology;

import com.tyrlib2.graphics.materials.TexturedMaterial;
import com.tyrlib2.graphics.renderer.Mesh;
import com.tyrlib2.graphics.renderer.ProgramManager;
import com.tyrlib2.graphics.renderer.Texture;
import com.tyrlib2.graphics.renderer.TextureRegion;
import com.tyrlib2.graphics.renderer.TyrGL;
import com.tyrlib2.util.Color;

public class TechMaterial extends TexturedMaterial {
	
	private float progress;
	private int progressHandle;
	private int bgHandle;
	
	protected Color bgColor = new Color(1,1,1,1);
	
	public TechMaterial(Texture texture) {
		super(texture, new TextureRegion(), ProgramManager.getInstance().getProgram("TECH"));
		progressHandle = TyrGL.glGetUniformLocation(program.handle, "u_Progress");
		bgHandle = TyrGL.glGetUniformLocation(program.handle, "u_BG");
		this.setColor(new Color(0.6f,0.6f,1,1));
	}
	
	@Override
	public void render(Mesh mesh, float[] modelMatrix) {
		super.render(mesh, modelMatrix);
		
		TyrGL.glUniform1f(progressHandle, progress);
		TyrGL.glUniform3f(bgHandle, bgColor.r, bgColor.g, bgColor.b);
	}
	
	public void setProgress(float progress) {
		this.progress = progress;
	}
}
