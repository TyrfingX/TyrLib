package com.tyrfing.tyrlibdemo01;

import tyrfing.common.game.Game2D;
import tyrfing.common.game.objects.Direction;
import tyrfing.common.game.objects.IMovementListener;
import tyrfing.common.game.objects.Movement;
import tyrfing.common.game.objects.Speed;
import tyrfing.common.game.objects.Updater;
import tyrfing.common.math.Vector2;
import tyrfing.common.render.SceneManager;
import tyrfing.common.render.TargetMetrics;
import tyrfing.common.struct.Node;
import android.content.Context;
import android.graphics.Color;

/** 
 * A class deriving from BaseGame/Game2D provides a surface to draw on and sets most of the logic
 * such as rendering/input handling, etc.
 * 
 * In this example a red rectangle will move continuously from left to right and back etc. while accelerating.
 * 
 * @author Sascha
 *
 */

public class MovingRectangle extends Game2D {
	
	/** Our rectangle will be simply moving from left to right and back, etc **/
	private Direction direction = Direction.LEFT;
	
	/** You can access the metrics(such as width and height) of the target device via TargetMetrics **/
	
	private	float rectWidth 	= TargetMetrics.width 	* 0.25f;
	private	float rectHeight 	= TargetMetrics.height 	* 0.2f;
	
	public MovingRectangle(Context context) {
		super(context);
	}

	@Override
	public void go() {
		

		
		/** Since devices have different physical properties, it is recommended to define sizes, lengths, etc
		 *  with regards towards the TargetMetrics.
		 */
		
		/** Nodes are the main object for positioning purposes. They can have objects attached to them
		 * 	and attach themselves to each other.
		 * 	This places a node, so that the rectangle will be in the middle of the screen.
		 */
		final Node node = new Node(TargetMetrics.width*0.5f - rectWidth/2, TargetMetrics.height*0.5f - rectHeight/2);
		
		/** This creates the actual rectangle. All rendeable objects should be instantiated with the
		 * 	SceneManager class. Furthermore, in order to convey the position of the rendering,
		 * 	a renderable has to be attached to a node.
		 */
		
		SceneManager.createRectangle(rectWidth, rectHeight, Color.RED, node);
		
		/** The Movement class takes care of moving nodes from one point to another width regards to a movement speed **/
		Speed speed = new Speed(100, 20);
		final Movement movement = new Movement(node, speed);
		
		/** In order to receive time advances an object needs to be either a FrameListener or an IUpdateable
		 * 	which is then updated by an Updater(Which is a FrameListener).
		 */
		
		Updater updater = new Updater();
		
		/** In order to give a FrameListener time updates, simply add it as a FrameListener to the SceneManager.
		 * 	It will then receive time updates after each render call.
		 */
		
		SceneManager.RENDER_THREAD.addFrameListener(updater);
		
		
		/**	This will make the updater further pass down the time updates to the objects added to it **/
		updater.addItem(movement);
		updater.addItem(speed);
		
		/** Lets get this going! **/
		movement.addPoint(new Vector2(0, node.getY()));
		
		/** The MovementListener reacts when movement has finished the added path **/
		movement.addMovementListener(new IMovementListener() {

			@Override
			public void setListening(boolean listening) {}

			@Override
			public boolean isListening() {
				return true;
			}

			@Override
			public void onFinishMovement() {
				if (direction == Direction.LEFT) {
					movement.addPoint(new Vector2(TargetMetrics.width - rectWidth, node.getY()));
					direction = Direction.RIGHT;
				} else {
					movement.addPoint(new Vector2(0, node.getY()));
					direction = Direction.LEFT;
				}
			}
			
		});
		
	}

}
