package com.tyrfing.games.tyrlib3.view.gui.events;

import com.tyrfing.games.tyrlib3.view.gui.WindowManager;

public class DestroyOnEvent implements IEventListener {

	@Override
	public void onEvent(WindowEvent event) {
		WindowManager.getInstance().destroyWindow(event.getSource());
	}

}
