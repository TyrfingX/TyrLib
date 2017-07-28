package com.tyrfing.games.tyrlib3.game;

/**
 * Interface for updateable objects
 * @author Sascha
 *
 */

public interface IUpdateable {
	public void onUpdate(float time);
	public boolean isFinished();
}
