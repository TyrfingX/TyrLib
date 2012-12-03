package com.tyrfing.tyrlibdemo01;

import tyrfing.common.game.Game2D;
import tyrfing.common.game.GameActivity;


/** 
 * GameActvity sets up basic stuff like Ressourcemanager and reads the metrics of the device, etc.
 * The app is also setup so that the screen does not rotate, gets rid of the title bar, etc.
 * As this class mainly provides services for convenience sake, it is recommended that you roll your
 * own activity class when creating a project where you require more freedom. (ie everything besides 
 * an example project) 
 * @author Sascha
 *
 */

public class Demo01 extends GameActivity {

	/** Actual logic for the game is setup in the BaseGame class from which the Game2D class derives **/
	private Game2D game;
	
	@Override
	public void go() {
		game = new MovingRectangle(this);
		
		/** This will actually make the game be the thing displayed on the screen **/
		this.setContentView(game);
		
		
		/**	Start your game here	**/
		game.go();

	}


}
