package tyrfing.common.render;

import tyrfing.common.game.objects.IUpdateable;

public interface IFrameListener extends IUpdateable {
	public void onClearRenderer();
}
