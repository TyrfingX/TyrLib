package com.tyrlib2.game;

import com.tyrlib2.graphics.scene.BoundedSceneObject;

public abstract class GameObject extends BoundedSceneObject implements IUpdateable {
	
	protected Stats stats;
	
	public GameObject() {
		stats = new Stats();
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
	
	public float getStat(Integer name) {
		return stats.getStat(name);
	}
	
	public void setStat(Integer name, float value) {
		stats.setStat(name, value);
	}
	
	public boolean hasStat(Integer name) {
		return stats.hasStat(name);
	}
	
	public void changeStat(Integer name, float value) {
		stats.changeStat(name, value);
	}
	
	public void changeStats(Stats stats) {
		stats.changeStats(stats);
	}
}
