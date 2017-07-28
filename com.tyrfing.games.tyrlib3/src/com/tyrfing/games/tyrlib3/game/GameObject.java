package com.tyrfing.games.tyrlib3.game;

import com.tyrfing.games.tyrlib3.graphics.scene.BoundedSceneObject;

public abstract class GameObject extends BoundedSceneObject implements IUpdateable {
	
	public GameObject() {
		
	}
	
	@Override
	public void onUpdate(float time) {
	}

	public void onCollide(GameObject object) {
	}
	
	@Override
	public boolean isFinished() {
		return false;
	}
}
