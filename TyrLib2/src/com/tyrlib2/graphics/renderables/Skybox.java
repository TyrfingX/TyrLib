package com.tyrlib2.graphics.renderables;

import android.content.Context;
import android.opengl.GLES20;

import com.tyrlib2.graphics.materials.DefaultMaterial3;
import com.tyrlib2.graphics.renderer.IRenderable;
import com.tyrlib2.graphics.renderer.Program;
import com.tyrlib2.graphics.renderer.ProgramManager;
import com.tyrlib2.graphics.renderer.Renderable;
import com.tyrlib2.graphics.scene.SceneNode;
import com.tyrlib2.graphics.scene.SceneObject;
import com.tyrlib2.math.Vector3;

/**
 * A skybox
 * @author Sascha
 *
 */


public class Skybox extends SceneObject implements IRenderable {
	
	private Renderable r;
	
	public Skybox(String textureName, Vector3 min, Vector3 max) {		
		Program program = ProgramManager.getInstance().getProgram("SKYBOX");
		DefaultMaterial3 material = new DefaultMaterial3(program, textureName, 1, 1, null);
	
		r = new Box(material, min, max);
	}

	@Override
	public void render(float[] vpMatrix) {
		GLES20.glCullFace(GLES20.GL_FRONT);
		r.render(vpMatrix);
		GLES20.glCullFace(GLES20.GL_BACK);
	}
	
	public static void enableSkyboxes(Context context) {
		if (!ProgramManager.getInstance().isProgramLoaded("SKYBOX")) {
			ProgramManager.getInstance().createProgram("SKYBOX", 
														context, 
														com.tyrlib2.R.raw.skybox_vs,
														com.tyrlib2.R.raw.skybox_fs, 
														new String[] { "a_Position", "a_TexCoordinate"});
		}
	}

	@Override
	public void attachTo(SceneNode node)  {
		r.attachTo(node);
		super.attachTo(node);
	}
	

	@Override
	public SceneNode detach() {
		r.detach();
		return super.detach();	
	}
}
