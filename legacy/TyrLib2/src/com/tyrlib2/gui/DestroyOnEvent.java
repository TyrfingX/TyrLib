package com.tyrlib2.gui;

public class DestroyOnEvent implements IEventListener {

	@Override
	public void onEvent(WindowEvent event) {
		WindowManager.getInstance().destroyWindow(event.getSource());
	}

}
