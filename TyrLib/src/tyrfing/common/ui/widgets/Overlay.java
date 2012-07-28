package tyrfing.common.ui.widgets;

import tyrfing.common.input.InputManager;
import tyrfing.common.render.SceneManager;
import tyrfing.common.render.TargetMetrics;
import tyrfing.common.renderables.Renderable;
import tyrfing.common.ui.Window;

public class Overlay extends Window {

	protected Renderable rect;
	
	public Overlay(String name, int color, int alpha) {
		super(name, 0, 0, TargetMetrics.width, TargetMetrics.height);
		rect = SceneManager.createRectangle(TargetMetrics.width, TargetMetrics.height, color, node);
		rect.getPaint().setAlpha(alpha);
		components.add(rect);
		InputManager.addTouchListener(this);
	}

}
