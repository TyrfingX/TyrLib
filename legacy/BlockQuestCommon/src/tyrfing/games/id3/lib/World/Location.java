package tyrfing.games.id3.lib.World;

import tyrfing.common.input.BackListener;
import tyrfing.common.math.Vector2;
import tyrfing.common.render.Ressources;
import tyrfing.common.ui.ClickListener;
import tyrfing.common.ui.WindowManager;
import tyrfing.common.ui.widgets.Button;
import tyrfing.games.id3.lib.MainGame;
import tyrfing.games.id3.lib.MenuConfig;
import tyrfing.games.id3.lib.R;
import tyrfing.games.id3.lib.mechanics.State;

public abstract class Location implements ClickListener, BackListener {

	protected Button mapItem = null;
	protected State state;
	protected Button back;
	private String name;
	
	public Location(String name, State state)
	{
		this.name = name;
		this.state = state;
		back = MenuConfig.createMenuItem(name + "\back", MainGame.getString(R.string.back), 1, this);
	}
	
	public String getName()
	{
		return name;
	}
	
	public abstract void construct();
	public abstract void openMenu();
	
	protected void construct(Vector2 pos, int normal, int click, float offset)
	{
		
		if (mapItem == null)
		{
		
			Button.TEXT_OFFSET = offset;
			Ressources.loadRes("ButtonNormal", normal);
			Ressources.loadRes("ButtonClick",  click);
			Ressources.loadRes("ButtonDisabled",  normal);
			
			mapItem = WindowManager.createButton("WorldMap/" + name, pos.x, pos.y, WorldMap.mapItemSize.x, WorldMap.mapItemSize.y, name);
			mapItem.setVisible(false);
			mapItem.addClickListener(state.worldMap);
	
		}
		
	}
	
	public void setVisible(boolean visible)
	{
		mapItem.setVisible(visible);
	}
	
	public abstract boolean onPressBack();
	
}
