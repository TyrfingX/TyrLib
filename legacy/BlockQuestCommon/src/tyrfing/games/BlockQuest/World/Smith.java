package tyrfing.games.BlockQuest.World;

import android.graphics.Color;
import tyrfing.common.game.objects.Stats;
import tyrfing.common.math.Vector2;
import tyrfing.common.render.Ressources;
import tyrfing.common.render.TargetMetrics;
import tyrfing.common.ui.ClickListener;
import tyrfing.common.ui.Event;
import tyrfing.common.ui.Window;
import tyrfing.common.ui.WindowManager;
import tyrfing.common.ui.widgets.Button;
import tyrfing.common.ui.widgets.ImageBox;
import tyrfing.common.ui.widgets.Label;
import tyrfing.games.BlockQuest.lib.MenuConfig;
import tyrfing.games.BlockQuest.mechanics.State;

public class Smith extends Building {

	
	private Button upgradeWeapons;
	private Button upgradeArmor;
	private Label weaponCosts;
	private Label armorCosts;
	
	private static final int WEAPON_BASE_COST = 750;
	private static final int WEAPON_INC_COST = 750;
	private static final int ARMOR_BASE_COST = 1000;
	private static final int ARMOR_INC_COST = 1500;
	
	private Stats data;
	
	public Smith(State state, boolean built) {
		super(state, "Smithery", 1, built, 700);
		
		data = state.character.getStats();
		upgradeWeapons = WindowManager.createButton("Smith/upgradeWeapons", TargetMetrics.width, MenuConfig.TOP + MenuConfig.OFFSET, MenuConfig.WIDTH, MenuConfig.HEIGHT, "(Enlighten) Sharpen swords");
		ImageBox costWindow = WindowManager.createImageBox(upgradeWeapons.getName() + "/MoneyCost", upgradeWeapons.getWidth()*0.4f+60, 10, 20, 20, Ressources.getScaledBitmap("money", new Vector2(20,20)));
		upgradeWeapons.addChild(costWindow);
		weaponCosts = WindowManager.createLabel(costWindow.getName() + "/label", 20, 5, 20, 20, this.getCurrentWeaponCosts()+"", Color.TRANSPARENT);
		costWindow.addChild(weaponCosts);
		
		upgradeArmor = WindowManager.createButton("Smith/upgradeArmor", TargetMetrics.width, MenuConfig.TOP + MenuConfig.OFFSET*2, MenuConfig.WIDTH, MenuConfig.HEIGHT, "(Enlighten) Improve armor");
		costWindow = WindowManager.createImageBox(upgradeArmor.getName() + "/MoneyCost", upgradeArmor.getWidth()*0.4f+60, 10, 20, 20, Ressources.getScaledBitmap("money", new Vector2(20,20)));
		upgradeArmor.addChild(costWindow);
		armorCosts = WindowManager.createLabel(costWindow.getName() + "/label", 20, 5, 20, 20, this.getCurrentArmorCosts()+"", Color.TRANSPARENT);
		costWindow.addChild(armorCosts);
		
		upgradeWeapons.setEnabled(false);
		upgradeArmor.setEnabled(false);
		
		upgradeWeapons.addClickListener(this);
		upgradeArmor.addClickListener(this);
	}
	
	public int getCurrentWeaponCosts()
	{
		return data.getStat("WeaponLevel") * WEAPON_INC_COST + WEAPON_BASE_COST;
	}
	
	public int getCurrentArmorCosts()
	{

		return data.getStat("ArmorLevel") * ARMOR_INC_COST + ARMOR_BASE_COST;
	}
	
	
	public void build()
	{
		super.build();
		state.character.getStats().setStat("Atk", state.character.getStats().getStat("Atk") + 1);
		state.character.getStats().setStat("Def", state.character.getStats().getStat("Def") + 1);
		state.save();
	}

	@Override
	public String getDescription() {
		return "Command your followers to build a smithery.\nSupplies the adventurers with new weapons\nand armors.\n+1Atk, +1Def.\n\nCapable of improving weapon\nand armor.";
	}

	@Override
	public void enter() {
		upgradeWeapons.moveTo(new Vector2(MenuConfig.LEFT, upgradeWeapons.getY()), MenuConfig.FADE_TIME);
		upgradeArmor.moveTo(new Vector2(MenuConfig.LEFT, upgradeArmor.getY()), MenuConfig.FADE_TIME);
		if (state.tutorial.isItemDone("EnlightPower"))
		{
			upgradeWeapons.setEnabled(true);
			upgradeArmor.setEnabled(true);
		}
	}

	@Override
	public void leave() {
		upgradeWeapons.moveTo(new Vector2(TargetMetrics.width, upgradeWeapons.getY()), MenuConfig.FADE_TIME);
		upgradeArmor.moveTo(new Vector2(TargetMetrics.width, upgradeArmor.getY()), MenuConfig.FADE_TIME);
		upgradeWeapons.setEnabled(false);
		upgradeArmor.setEnabled(false);
	}
	
	@Override
	public void onClick(Event event) {
		if (event.getEvoker() == upgradeWeapons)
		{
			final String text = "Grant your smiths the wisdom\nneeded to forge better swords.\n\n+1Atk";
			this.upgrade(text, weaponCosts, this.getCurrentWeaponCosts(), this.getCurrentWeaponCosts() + WEAPON_INC_COST,"WeaponLevel", "Atk");
		}
		else if (event.getEvoker() == upgradeArmor)
		{
			final String text = "Grant your smiths the wisdom\nneeded to forge better armor.\n\n+1Def";
			this.upgrade(text, armorCosts, this.getCurrentArmorCosts(), this.getCurrentArmorCosts() + ARMOR_INC_COST, "ArmorLevel", "Def");
		}
		else
		{
			super.onClick(event);
		}
	}

	protected void upgrade(String text, final Label clicked, final int costs, final int newCosts, final String levelInc, final String statInc)
	{

		Window info = WindowManager.createYesNoMessageBox(clicked.getName() + "/infoBox",
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
						if (statInc != null) state.character.getStats().setStat(statInc, state.character.getStats().getStat(statInc) + 1);
						if (levelInc != null) state.character.getStats().setStat(levelInc, state.character.getStats().getStat(levelInc) + 1);
						clicked.setCaption(newCosts+"");
						state.save();
					}
					else
					{
						Window info = WindowManager.createConfirmMessageBox(clicked.getName() + "/noMoney/infoBox",
								TargetMetrics.width * 0.1f, TargetMetrics.height * 0.25f,
								TargetMetrics.width * 0.8f, TargetMetrics.height * 0.35f, "Not enough money!");							
						WindowManager.makePopup(info, clicked.getName() + "/noMoney");
					}
				}
			}
		});
		WindowManager.makePopup(info, clicked.getName());		
	}
	
}
