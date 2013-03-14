package com.tyrlib2.game;

import com.tyrlib2.graphics.scene.SceneObject;

public abstract class GameObject extends SceneObject implements IUpdateable {
	
	protected Stats stats;
	
	public GameObject() {
		stats = new Stats();
	}
	
	@Override
	public void onUpdate(float time) {
	}

	@Override
	public boolean isFinished() {
		return false;
	}
	
	public float getStat(String name) {
		return stats.getStat(name);
	}
	
	public void setStat(String name, float value) {
		stats.setStat(name, value);
	}
	
	public boolean hasStat(String name) {
		return stats.hasStat(name);
	}
}
