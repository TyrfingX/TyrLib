package tyrfing.common.ui.widgets;

import tyrfing.common.game.BaseGame;
import tyrfing.common.math.Vector2;
import tyrfing.common.ui.ClickListener;
import tyrfing.common.ui.Event;
import tyrfing.common.ui.WindowManager;
import tyrfing.games.R;

public class YesNoMessageBox extends MessageBox implements ClickListener {

	
	private Button yes;
	private Button no;
	
	public YesNoMessageBox(String name, float x, float y, float w, float h, String text) {
		super(name, x, y, w, h, text);
		
	
		no = WindowManager.createButton(name + "/no", 0.75f*w-50, 0.7f*h, 100, 0.2f*h, BaseGame.CONTEXT.getString(R.string.no));
		frame.addChild(no);
		no.addClickListener(this);
	
	
		yes = WindowManager.createButton(name + "/yes", 0.25f*w-50, 0.7f*h, 100, 0.2f*h, BaseGame.CONTEXT.getString(R.string.yes));
		frame.addChild(yes);
		yes.addClickListener(this);
		
	
	}

	@Override
	public void onClick(Event event) {
		if (event.getEvoker() == yes)
			event.addParam("Result", "Yes");
		else
			event.addParam("Result", "No");
		
		this.evokeClick(event);
	}
	
	public boolean onTouchDown(Vector2 point)
	{	
		return this.isPointInWindow(point);
	}
	
	public void addClickListener(ClickListener clickListener)
	{
		yes.addClickListener(clickListener);
		no.addClickListener(clickListener);
	}

}
