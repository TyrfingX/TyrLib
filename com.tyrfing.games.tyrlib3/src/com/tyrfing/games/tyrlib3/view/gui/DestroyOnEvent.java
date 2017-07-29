package com.tyrfing.games.tyrlib3.view.gui;

public class DestroyOnEvent implements IEventListener {

	@Override
	public void onEvent(WindowEvent event) {
		WindowManager.getInstance().destroyWindow(event.getSource());
	}

}
