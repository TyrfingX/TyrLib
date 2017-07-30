package tyrfing.games.id3.lib.Blessings;


import android.graphics.Color;
import tyrfing.common.math.Vector2;
import tyrfing.common.render.Ressources;
import tyrfing.common.render.TargetMetrics;
import tyrfing.common.ui.ClickListener;
import tyrfing.common.ui.Event;
import tyrfing.common.ui.Window;
import tyrfing.common.ui.WindowManager;
import tyrfing.common.ui.widgets.ImageBox;
import tyrfing.common.ui.widgets.Label;
import tyrfing.games.id3.lib.MenuConfig;
import tyrfing.games.id3.lib.mechanics.State;

public class Blessing {

	protected String name;
	protected int cost;
	protected int costIncrease;
	protected String stat;
	protected int value;
	protected int upgrades;
	protected int maxUpgrades;
	protected boolean activated;
	protected String description;
	protected State state;
	protected Window blessing;
	protected Vector2 pos;
	
	protected Label upgradeLabel;
	protected String[] follow;
	
	
	public Blessing(State state, String name, String description, int cost, int costIncrease, String stat, int value, int upgrades, int maxUpgrades, Vector2 pos, String[] follow) {
		this.state = state;
		this.name = name;
		this.description = description;
		this.cost = cost;
		this.costIncrease = costIncrease;
		this.stat = stat;
		this.value = value;
		this.upgrades = upgrades;
		this.maxUpgrades = maxUpgrades;
		this.pos = pos;
		this.follow = follow;
	}
	
	public String getName()
	{
		return name;
	}
	
	public int moneyCost()
	{
		return cost + upgrades * costIncrease;
	}
	
	public void doBlessing()
	{
		state.character.setMoney(state.character.getMoney() - moneyCost());
		state.playerMoney.setCaption(state.character.getMoney()+"");
		if (stat.equals("Hp"))
		{
			state.character.getStats().setStat("MaxHp", state.character.getStats().getStat("MaxHp") + value);
			state.character.getStats().setStat("Hp", state.character.getStats().getStat("MaxHp"));
		} else {
			state.character.getStats().setStat(stat, state.character.getStats().getStat(stat) + value);
		}
		upgrades++;
	
		upgradeLabel.setCaption(upgrades + "/" + maxUpgrades);
		
		Label label = (Label) WindowManager.getWindow(blessing.getName() + "/MoneyCost/label");
		label.setCaption(this.moneyCost()+"");
		
		if (upgrades >= maxUpgrades)
		{
			activated = false;
			blessing.disable();
			blessing.fadeOut(0.5f);

			if (follow != null)
			{
				for (String followBlessing : follow)
				{
					state.blessingMenu.activateBlessing(followBlessing);
				}
			}
		
		}
		
		state.save();
	
	}
	
	public void activate()
	{
		activated = true;
		blessing = WindowManager.createButton("BlessingMenu/" + this.getName(), pos.x, pos.y, MenuConfig.WIDTH, MenuConfig.HEIGHT, this.getName());
		blessing.setVisible(false);
		
		//Show Upgrades
		upgradeLabel = WindowManager.createLabel(blessing.getName() + "/label", 20, 5, 20, 20, upgrades+"/"+maxUpgrades, Color.TRANSPARENT);
		blessing.addChild(upgradeLabel);
		
		//Show Money costs
		ImageBox costWindow = WindowManager.createImageBox(blessing.getName() + "/MoneyCost", blessing.getWidth()*0.65f, 10, 20, 20, Ressources.getScaledBitmap("money", new Vector2(20,20)));
		blessing.addChild(costWindow);
		costWindow.addChild(WindowManager.createLabel(costWindow.getName() + "/label", 20, 5, 20, 20, moneyCost() +"", Color.TRANSPARENT));
		blessing.addClickListener(new ClickListener(){
			public void onClick(Event event) {
				onClickButton();
			}
		});
	}
	
	public boolean isActivated()
	{
		return activated;
	}
	
	private void onClickButton()
	{
		
		if (state.character.getMoney() < moneyCost())
		{
			Window info = WindowManager.createConfirmMessageBox(name + "/infoBox",
					TargetMetrics.width * 0.1f, TargetMetrics.height * 0.25f,
					TargetMetrics.width * 0.8f, TargetMetrics.height * 0.35f, description + "\n\n\n\nNot enough money!");
			WindowManager.makePopup(info, name);			
		}
		else
		{
			Window info = WindowManager.createYesNoMessageBox(name + "/infoBox",
					TargetMetrics.width * 0.1f, TargetMetrics.height * 0.25f,
					TargetMetrics.width * 0.8f, TargetMetrics.height * 0.35f, description);
			info.addClickListener(new ClickListener() {
				public void onClick(Event event) {	
					if (event.getParam("Result").equals("Yes"))
					{
						doBlessing();
					}
				}
			});
			WindowManager.makePopup(info, name);

		}
	}

}
