package tyrfing.games.id3.lib.GodPowers;

import tyrfing.common.math.Vector2;
import tyrfing.common.render.TargetMetrics;
import tyrfing.common.ui.ClickListener;
import tyrfing.common.ui.SetVisibleOnArrival;
import tyrfing.common.ui.WindowManager;
import tyrfing.common.ui.widgets.Button;

public abstract class Power {
	
	protected String name;
	protected boolean hasMenu;
	protected Button openMenu;
	protected PowerType type;
	
	public Power(PowerType type, String name, boolean hasMenu)
	{
		this.type = type;
		this.name = name;
		this.hasMenu = hasMenu;
	}
	
	public String getName()
	{
		return name;
	}
	
	public boolean hasMenu()
	{
		return this.hasMenu;
	}
	
	public void displayMenu(Vector2 pos, Vector2 size, float displayTime)
	{
		if (this.hasMenu)
		{
			if (openMenu == null)
			{
				this.openMenu = WindowManager.createButton("Power/" + name + "/openMenu", TargetMetrics.width, pos.y, size.x, size.y, name);
				openMenu.addMovementListener(new SetVisibleOnArrival(false, openMenu));
			}
			
			this.openMenu.setVisible(true);
			this.openMenu.enable();
			this.openMenu.moveTo(new Vector2(pos.x, pos.y), displayTime);
			this.openMenu.getMovementListener(0).setListening(false);
			
		}
	}
	
	public void hideMenu(float hideTime)
	{
		if (this.hasMenu)
		{
			if (this.openMenu != null)
			{
				this.openMenu.disable();
				this.openMenu.moveTo(new Vector2(TargetMetrics.width, openMenu.getY()), hideTime);
				this.openMenu.getMovementListener(0).setListening(true);
			}
		}
	}
	
	public void setMenuListener(ClickListener listener)
	{
		this.openMenu.addClickListener(listener);	
	}
	
	public PowerType getType()
	{
		return type;
	}
	
}
