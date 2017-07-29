package com.tyrfing.games.tyrlib3.model.game;

import com.tyrfing.games.tyrlib3.model.graphics.scene.BoundedSceneObject;
import com.tyrfing.games.tyrlib3.model.resource.ISaveable;

public abstract class GameObject extends BoundedSceneObject implements IUpdateable, ISaveable {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 5360261207692144426L;

	public GameObject() {
		
	}
	
	@Override
	public void onUpdate(float time) {
	}
	
	@Override
	public boolean isFinished() {
		return false;
	}
}
