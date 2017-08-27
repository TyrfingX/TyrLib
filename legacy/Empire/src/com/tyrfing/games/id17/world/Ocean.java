package com.tyrfing.games.id17.world;

import com.tyrlib2.game.IUpdateable;
import com.tyrlib2.graphics.renderables.Box;
import com.tyrlib2.graphics.renderer.IRenderable;
import com.tyrlib2.graphics.renderer.TyrGL;
import com.tyrlib2.graphics.scene.SceneManager;
import com.tyrlib2.graphics.scene.SceneNode;
import com.tyrlib2.math.Vector3;
import com.tyrlib2.util.Color;

public class Ocean implements IRenderable, IUpdateable {

	private Box box;
	private int insertionID;
	private float passedTime;
	private SceneNode node;
	
	public Ocean(WorldMap map) {
		OceanMaterial mat = new OceanMaterial("WATER", map.width/2, map.height/2, new Color[] { new Color(1,1,1,0.1f) });
		mat.setTransparent(true);
		mat.setBlendMode(TyrGL.GL_ONE_MINUS_SRC_ALPHA);
		box = new Box(mat, new Vector3(-map.width+20,-map.height+20,8.5f*WorldChunk.HEIGHT_FACTOR*WorldChunk.BLOCK_SIZE), new Vector3(map.width-20,map.height-20,8.5f*WorldChunk.HEIGHT_FACTOR*WorldChunk.BLOCK_SIZE));
		node = SceneManager.getInstance().getRootSceneNode().createChild();
		node.attachSceneObject(box);
		World.getInstance().getUpdater().addItem(this);
	}
	
	@Override
	public void render(float[] vpMatrix) {
		box.render(vpMatrix);
		//round.render(vpMatrix);
	}

	@Override
	public void renderShadow(float[] vpMatrix) {

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
		box.destroy();
	}

	@Override
	public void onUpdate(float time) {
		passedTime += time;
		node.setRelativePos(0, 0, (float)Math.sin(passedTime/8)/1.125f+(float)Math.sin(passedTime)/4);
	}

	@Override
	public boolean isFinished() {
		return false;
	}

}
