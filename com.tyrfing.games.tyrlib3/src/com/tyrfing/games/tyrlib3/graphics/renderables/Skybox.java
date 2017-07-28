package com.tyrfing.games.tyrlib3.graphics.renderables;

import com.tyrfing.games.tyrlib3.graphics.materials.DefaultMaterial3;
import com.tyrfing.games.tyrlib3.graphics.renderer.IRenderable;
import com.tyrfing.games.tyrlib3.graphics.renderer.Program;
import com.tyrfing.games.tyrlib3.graphics.renderer.ProgramManager;
import com.tyrfing.games.tyrlib3.graphics.renderer.Renderable;
import com.tyrfing.games.tyrlib3.graphics.renderer.TyrGL;
import com.tyrfing.games.tyrlib3.graphics.renderer.VertexLayout;
import com.tyrfing.games.tyrlib3.graphics.scene.SceneNode;
import com.tyrfing.games.tyrlib3.graphics.scene.SceneObject;
import com.tyrfing.games.tyrlib3.main.Media;
import com.tyrfing.games.tyrlib3.math.Vector3F;

/**
 * A skybox
 * @author Sascha
 *
 */


public class Skybox extends SceneObject implements IRenderable {
	
	private Renderable r;
	private int insertionID;
	
	public Skybox(String textureName, Vector3F min, Vector3F max) {		
		Program program = ProgramManager.getInstance().getProgram("SKYBOX");
		DefaultMaterial3 material = new DefaultMaterial3(program, textureName, 1, 1, null);
		int oldByteStride = material.getByteStride();
		material.getVertexLayout().setSize(VertexLayout.NORMAL, 0);
		material.getVertexLayout().setBytestride(oldByteStride);
		material.setLighted(false);
		r = new Box(material, min, max);
	}

	@Override
	public void render(float[] vpMatrix) {
		TyrGL.glCullFace(TyrGL.GL_FRONT);
		r.render(vpMatrix);
		TyrGL.glCullFace(TyrGL.GL_BACK);
	}
	
	public static void enableSkyboxes() {
		if (!ProgramManager.getInstance().isProgramLoaded("SKYBOX")) {
			ProgramManager.getInstance().createProgram("SKYBOX", 
														Media.CONTEXT.getResourceID("skybox_vs", "raw"),
														Media.CONTEXT.getResourceID("skybox_fs", "raw"), 
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

	@Override
	public void renderShadow(float[] vpMatrix) {
		// TODO Auto-generated method stub
		
	}
	
	@Override
	public void setInsertionID(int id) {
		this.insertionID = id;
	}

	@Override
	public int getInsertionID() {
		return insertionID;
	}

	@Override
	public void destroy() {
		if (r != null) {
			r.destroy();
		}
	}
}
