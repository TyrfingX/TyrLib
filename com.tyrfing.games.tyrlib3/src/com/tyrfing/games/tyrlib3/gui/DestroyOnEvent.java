package com.tyrfing.games.tyrlib3.gui;

public class DestroyOnEvent implements IEventListener {

	@Override
	public void onEvent(WindowEvent event) {
		WindowManager.getInstance().destroyWindow(event.getSource());
	}

}
