package com.tyrfing.games.tyrlib3.view.game;

import com.tyrfing.games.tyrlib3.model.game.GameObject;
import com.tyrfing.games.tyrlib3.model.graphics.scene.SceneNode;

public abstract class AGameObjectView {
	private GameObject model;
	private SceneNode node;
	
	public void setModel(GameObject model) {
		this.model = model;
	}
	
	public GameObject getModel() {
		return model;
	}
	
	public SceneNode getNode() {
		return node;
	}
	
	public void setNode(SceneNode node) {
		this.node = node;
	}
}
