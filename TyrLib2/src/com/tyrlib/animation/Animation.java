package com.tyrlib.animation;

/**
 * This class represents an animation by manipulating a skeleton.
 * @author Sascha
 *
 */

public class Animation {
	
	/** Animation data **/
	protected String name;
	protected boolean loop;
	protected float duration;
	
	public Animation(String name, float duration) {
		this.name = name;
		this.duration = duration;
	}
	
	public void play() {
		
	}
	
	public void pause() {
		
	}
	
	public void reset() {
		
	}
	
	public String getName() {
		return name;
	}
	
	public boolean isLooping() {
		return loop;
	}
	
	public float duration() {
		return duration;
	}
}
