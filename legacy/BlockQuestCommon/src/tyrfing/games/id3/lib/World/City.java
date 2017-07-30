package tyrfing.games.id3.lib.World;

import java.util.ArrayList;

import android.graphics.Color;
import tyrfing.common.input.InputManager;
import tyrfing.common.math.Vector2;
import tyrfing.common.render.SceneManager;
import tyrfing.common.render.TargetMetrics;
import tyrfing.common.renderables.Text;
import tyrfing.common.struct.Node;
import tyrfing.common.ui.Event;
import tyrfing.games.id3.lib.MainGame;
import tyrfing.games.id3.lib.MenuConfig;
import tyrfing.games.id3.lib.R;
import tyrfing.games.id3.lib.mechanics.State;


public class City extends Location {

	private static final Vector2 cityPos = new Vector2(TargetMetrics.width*0.3f, TargetMetrics.width*0.8f);
	
	private ArrayList<Text> messages;
	private ArrayList<Building> buildings;
	

	private Building currentBuilding;
	
	public City(State state) {
		super(MainGame.getString(R.string.city), state);
		buildings = new ArrayList<Building>();
		messages = new ArrayList<Text>();
	}
	
	public void construct()
	{
		super.construct(cityPos, R.drawable.city1, R.drawable.city1click, -TargetMetrics.height*0.2f);	
	}

	@Override
	public void onClick(Event event) {
		this.back();
	}

	private void back()
	{
		if (currentBuilding == null)
		{
		
			back.disable();
			back.moveTo(new Vector2(TargetMetrics.width, MenuConfig.TOP + MenuConfig.OFFSET), MenuConfig.FADE_TIME);
			back.getMovementListener(0).setListening(true);
			
			for (Text text : messages)
			{
				text.fadeOut(new Vector2(0,0), 0.5f);
			}
			
			for (Building building : buildings)
			{
				building.hide();
			}
			
			state.worldMap.show();
			InputManager.removeBackListener(this);
		}
		else
		{
			currentBuilding.leave();
			this.openMenu();
		}		
	}
	
	@Override
	public void openMenu() {
		back.enable();
		back.setVisible(true);
		back.moveTo(new Vector2(MenuConfig.LEFT, MenuConfig.TOP), MenuConfig.FADE_TIME);
		back.getMovementListener(0).setListening(false);
		
		
		for (Text message : messages) {
			SceneManager.RENDER_THREAD.removeRenderable(message);
		}
		
		messages.clear();
		
		InputManager.addBackListener(this);
		
		if (buildings.size() == 0)
		{
			messages.add(SceneManager.createText(MainGame.getString(R.string.poor_1), Color.BLACK, new Node(TargetMetrics.width*0.13f,TargetMetrics.height*0.075f)));
			messages.add(SceneManager.createText(MainGame.getString(R.string.nothingToDo), Color.BLACK, new Node(TargetMetrics.width*0.13f,TargetMetrics.height*0.5f)));
		}
		else
		{
			if (state.deepestLevel < 20) {
				messages.add(SceneManager.createText(MainGame.getString(R.string.poor_2), Color.BLACK, new Node(TargetMetrics.width*0.13f,TargetMetrics.height*0.075f)));
			} else {
				messages.add(SceneManager.createText(MainGame.getString(R.string.medium_1), Color.BLACK, new Node(TargetMetrics.width*0.13f,TargetMetrics.height*0.075f)));
			}
				
			for (Building building : buildings)
			{
				building.display();
			}
		}
		
		for (Text text : messages)
		{
			text.blendIn(new Vector2(0,0), 0.5f);
		}
		
		currentBuilding = null;
	}
	
	public void addBuilding(Building building)
	{
		buildings.add(building);
		building.setCity(this);
	}
	
	public String buildingsToString()
	{	
		String res = "";
		for (Building building : buildings)
		{
			res += building.getName() + "," + building.isBuilt() + ";";
		}
		
		return res;		
	}
	
	public void enterBuilding(Building building)
	{
		for (Text text : messages)
		{
			text.fadeOut(new Vector2(0,0), 0.5f);
		}
		for (Building b : buildings)
		{
			b.hide();
		}
		currentBuilding = building;
		building.enter();
	}

	@Override
	public boolean onPressBack() {
		this.back();
		return true;
	}
	
	public boolean isBuilt(String building)
	{
		for (Building b : buildings)
		{
			if (b.getName().equals(building))
			{
				if (b.isBuilt())
				{
					return true;
				}
				else
				{
					return false;
				}
			}
		}
		
		return false;
	}
}
