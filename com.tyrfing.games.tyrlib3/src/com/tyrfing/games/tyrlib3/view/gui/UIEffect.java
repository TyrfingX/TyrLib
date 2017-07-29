package com.tyrfing.games.tyrlib3.view.gui;

import com.tyrfing.games.tyrlib3.math.Vector2F;
import com.tyrfing.games.tyrlib3.view.gui.WindowEvent.WindowEventType;

public class UIEffect {
	private static class MoveFinished implements IEventListener {
		
		private float bounceTime;
		private int sign;
		protected IEventListener onFinished;
		
		public MoveFinished(float bounceTime, int sign) {
			this.bounceTime = bounceTime;
			this.sign = sign;
		}
		
		@Override
		public void onEvent(WindowEvent event) {
			Window src = event.getSource();
			src.moveBy(new Vector2F(0, sign * src.getSize().y / 2), bounceTime);
			src.removeEventListener(WindowEventType.MOVEMENT_FINISHED, this);
			src.addEventListener(WindowEventType.MOVEMENT_FINISHED, onFinished);
		}
	};	
	
	public static void addBounce(final Window window, final float bounceTime) {
		MoveFinished onDown = new MoveFinished(bounceTime, -1);
		MoveFinished onUp = new MoveFinished(bounceTime, 1);
		
		onUp.onFinished = onDown;
		onDown.onFinished = onUp;
		
		window.addEventListener(WindowEventType.MOVEMENT_FINISHED, onUp);
	}
}
