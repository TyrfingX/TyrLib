package com.tyrfing.games.tyrlib3.gui;

public class FadeOutOnEvent implements IEventListener{
	
	private float time;
	
	public FadeOutOnEvent(float time) {
		this.time = time;
	}

	@Override
	public void onEvent(WindowEvent event) {
		event.getSource().fadeOut(0, time);
	}
}
