package tyrfing.games.id3.lib.World;

import android.graphics.Color;
import tyrfing.common.math.Vector2;
import tyrfing.common.render.Ressources;
import tyrfing.common.render.TargetMetrics;
import tyrfing.common.ui.ClickListener;
import tyrfing.common.ui.Event;
import tyrfing.common.ui.Window;
import tyrfing.common.ui.WindowManager;
import tyrfing.common.ui.widgets.Button;
import tyrfing.common.ui.widgets.ImageBox;
import tyrfing.games.id3.lib.MenuConfig;
import tyrfing.games.id3.lib.mechanics.State;

public abstract class Building implements ClickListener{
	
	
	private String name;
	protected State state;
	private Button openMenu;
	private boolean built;
	private int costs;
	private City city;
	
	public Building(State state, String name, int posIndex, boolean built, int costs)
	{
		this.name = name;
		this.state = state;
		this.built = built;
		this.costs = costs;
		
		String caption = "";
		
		if (built)
		{
			caption = "Visit " + name;
		}
		else
		{
			caption = "(Vision) Build "+ name;
		}
		
		
		openMenu = WindowManager.createButton(	name + "Building", TargetMetrics.width, MenuConfig.TOP + MenuConfig.OFFSET * posIndex, 
												MenuConfig.WIDTH, MenuConfig.HEIGHT, caption);
		
		if (!built)
		{
			ImageBox costWindow = WindowManager.createImageBox(name + "/MoneyCost", openMenu.getWidth()*0.4f+60, 10, 20, 20, Ressources.getScaledBitmap("money", new Vector2(20,20)));
			openMenu.addChild(costWindow);
			costWindow.addChild(WindowManager.createLabel(costWindow.getName() + "/label", 20, 5, 20, 20, costs +"", Color.TRANSPARENT));
		}
		
		openMenu.setEnabled(false);
		
		openMenu.addClickListener(this);
	
	}
	
	public void display()
	{
		openMenu.moveTo(new Vector2(MenuConfig.LEFT, openMenu.getY()), MenuConfig.FADE_TIME);
		if (state.tutorial.isItemDone("VisionPower"))
		{
			openMenu.setEnabled(true);
		}
	}
	
	public void hide()
	{
		openMenu.moveTo(new Vector2(TargetMetrics.width, openMenu.getY()), MenuConfig.FADE_TIME);
		openMenu.setEnabled(false);
	}
	
	public abstract void enter();
	public abstract void leave();
	
	public boolean isBuilt()
	{
		return built;
	}
	
	public void build()
	{
		built = true;
		openMenu.setCaption("Visit " + name);
		WindowManager.getWindow(name + "/MoneyCost").destroy();
	}

	@Override
	public void onClick(Event event) {
		if (event.getEvoker() == openMenu)
		{
			if (!this.built)
			{
				final String text = this.getDescription();
				Window info = WindowManager.createYesNoMessageBox(name + "/infoBox",
						TargetMetrics.width * 0.1f, TargetMetrics.height * 0.25f,
						TargetMetrics.width * 0.8f, TargetMetrics.height * 0.35f, text);
				info.addClickListener(new ClickListener() {
					public void onClick(Event event) {	
						if (event.getParam("Result").equals("Yes"))
						{
							if (costs <= state.character.getMoney())
							{
								state.character.setMoney(state.character.getMoney() - costs);
								state.playerMoney.setCaption(state.character.getMoney()+"");
								build();
							}
							else
							{
								Window info = WindowManager.createConfirmMessageBox(name + "/noMoney/infoBox",
										TargetMetrics.width * 0.1f, TargetMetrics.height * 0.25f,
										TargetMetrics.width * 0.8f, TargetMetrics.height * 0.35f, "Not enough money!");							
								WindowManager.makePopup(info, name + "/noMoney");
							}
						}
					}
				});
				WindowManager.makePopup(info, name);
			}
			else
			{
				city.enterBuilding(this);
			}
		}
	}
	
	public String getName()
	{
		return name;
	}
	
	public void setCity(City city)
	{
		this.city = city;
	}
	
	public abstract String getDescription();
}
