package tyrfing.games.BlockQuest.lib;

import tyrfing.common.render.TargetMetrics;
import tyrfing.common.ui.ClickListener;
import tyrfing.common.ui.SetVisibleOnArrival;
import tyrfing.common.ui.WindowManager;
import tyrfing.common.ui.widgets.Button;

public class MenuConfig {
	public static final float FADE_TIME = 0.5f;
	public static final float LEFT = TargetMetrics.width * 0.25f;
	public static final float TOP = TargetMetrics.height * 0.2f;
	public static final float WIDTH = TargetMetrics.width * 0.5f;
	public static final float HEIGHT = TargetMetrics.height * 0.12f;
	public static final float OFFSET = TargetMetrics.height * 0.175f;
	
	
	public static Button createMenuItem(String name, String text, int position, ClickListener listener)
	{
		Button window = WindowManager.createButton(name, TargetMetrics.width, MenuConfig.TOP + MenuConfig.OFFSET * position, MenuConfig.WIDTH, MenuConfig.HEIGHT, text);
		window.setVisible(false);
		window.addClickListener(listener);
		window.addMovementListener(new SetVisibleOnArrival(false, window));
		window.getMovementListener(0).setListening(false);
		return window;
	}
	
}
