package tyrfing.common.ui;

import tyrfing.common.game.objects.MovementListener;

public class SetVisibleOnArrival extends MovementListener {
	
	private boolean visible;
	private Window window;
	
	public SetVisibleOnArrival(boolean visible, Window window)
	{
		this.visible = visible;
		this.window = window;
	}

	@Override
	public void onFinishMovement() {
		window.setVisible(visible);
	}

}
