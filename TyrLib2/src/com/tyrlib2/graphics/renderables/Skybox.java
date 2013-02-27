package com.tyrlib2.graphics.renderables;

import android.content.Context;
import android.opengl.GLES20;

import com.tyrlib2.graphics.lighting.LightingType;
import com.tyrlib2.graphics.materials.DefaultMaterial3;
import com.tyrlib2.graphics.materials.TexturedMaterial;
import com.tyrlib2.graphics.renderer.IRenderable;
import com.tyrlib2.graphics.renderer.Program;
import com.tyrlib2.graphics.renderer.ProgramManager;
import com.tyrlib2.graphics.renderer.Texture;
import com.tyrlib2.graphics.renderer.TextureManager;
import com.tyrlib2.graphics.scene.SceneNode;
import com.tyrlib2.graphics.scene.SceneObject;
import com.tyrlib2.math.AABB;
import com.tyrlib2.math.Vector3;

/**
 * A skybox
 * @author Sascha
 *
 */


public class Skybox extends SceneObject implements IRenderable {
	
	private Box box;
	
	public Skybox(String textureName, Vector3 min, Vector3 max) {		
		Program program = ProgramManager.getInstance().getProgram("SKYBOX");
		DefaultMaterial3 material = new DefaultMaterial3(program, textureName, 1, 1, null);
		
		box = new Box(material, min, max);
	}

	@Override
	public void render(float[] vpMatrix) {
		GLES20.glCullFace(GLES20.GL_FRONT);
		box.render(vpMatrix);
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
		box.attachTo(node);
		super.attachTo(node);
	}
	

	@Override
	public SceneNode detach() {
		box.detach();
		return super.detach();	
	}
	
	@Override
	public AABB getBoundingBox() {
		return null;
	}

	@Override
	public void setBoundingBoxVisible(boolean visible) {
	}
}
