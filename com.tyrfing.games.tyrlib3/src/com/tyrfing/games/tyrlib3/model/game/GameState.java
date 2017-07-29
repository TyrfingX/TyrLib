package com.tyrfing.games.tyrlib3.model.game;


/**
 * Basic abstract class for game states (main menu, ingame, game over, etc)
 * @author Sascha
 *
 */

public abstract class GameState implements IUpdateable {
	
	private boolean inGameState = false;
	
	public void enterGameState() {
		inGameState = true;
	}
	
	public void leaveGameState() {
		inGameState = false;
	}
	
	public void onUpdate(float time) {
	}
	
	public boolean isFinished() {
		return inGameState;
	}
}
