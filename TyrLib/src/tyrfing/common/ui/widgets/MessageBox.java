package tyrfing.common.ui.widgets;

import android.graphics.Color;
import tyrfing.common.input.InputManager;
import tyrfing.common.math.Vector2;
import tyrfing.common.render.Ressources;
import tyrfing.common.ui.Window;
import tyrfing.common.ui.WindowManager;

public class MessageBox extends Window {

	protected ImageBox frame;
	protected Label caption;

	
	public MessageBox(String name, float x, float y, float w, float h, String text) {
		super(name, x, y, w, h);
		
		frame = WindowManager.createImageBox(name + "/frame", 0, 0, w, h, Ressources.getScaledBitmap("MessageBox", new Vector2(w,h)));
		this.addChild(frame);
		
		caption = WindowManager.createLabel(name + "/Caption", 0, 0, w, h/2.5f, text, Color.TRANSPARENT);
		caption.setCaptionColor(Color.BLACK);
		frame.addChild(caption);
		
		InputManager.addTouchListener(this);
	
	}

}
