package com.tyrlib2.gui;

public class DestroyOnFadeOut implements IEventListener {

	@Override
	public void onEvent(WindowEvent event) {
		WindowManager.getInstance().destroyWindow(event.getSource());
	}

}
