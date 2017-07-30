package com.tyrfing.games.id17.geometry;

import com.tyrfing.games.id17.world.World;
import com.tyrfing.games.id17.world.WorldChunk;
import com.tyrlib2.graphics.renderables.Entity;
import com.tyrlib2.graphics.scene.SceneManager;
import com.tyrlib2.graphics.scene.SceneNode;
import com.tyrlib2.math.Vector3;

public class Grass {
	
	public final Entity grass;
	public final SceneNode node;
	
	public Grass(WorldChunk chunk, SceneNode node, String texture, String entity) {
		this.node = node;
		grass = SceneManager.getInstance().createEntity(entity, true);
		SceneManager.getInstance().getRenderer().removeRenderable(grass);
		node.attachSceneObject(grass);
		float initTime = node.getRelativePos().length()*100;
		GrassMaterial mat = new GrassMaterial(chunk, texture, new Vector3(1,1,0), 0.1f, initTime/4);
		grass.getSubEntity(0).setMaterial(mat);
		
		node.scale(new Vector3(2,2,-0.3f));
		
		World.getInstance().getUpdater().addItem(mat);
	}
}
