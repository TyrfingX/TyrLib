package tyrfing.games.id3.lib.World;

import java.util.ArrayList;
import java.util.List;

import android.graphics.Color;
import tyrfing.common.game.objects.Stats;
import tyrfing.common.math.Vector2;
import tyrfing.common.render.Animation;
import tyrfing.common.render.Ressources;
import tyrfing.common.render.SceneManager;
import tyrfing.common.render.TargetMetrics;
import tyrfing.common.renderables.Image;
import tyrfing.common.renderables.Text;
import tyrfing.common.struct.Node;
import tyrfing.common.ui.ClickListener;
import tyrfing.common.ui.Event;
import tyrfing.common.ui.Window;
import tyrfing.common.ui.WindowManager;
import tyrfing.common.ui.widgets.Button;
import tyrfing.common.ui.widgets.ImageBox;
import tyrfing.common.ui.widgets.Label;
import tyrfing.games.id3.lib.MenuConfig;
import tyrfing.games.id3.lib.mechanics.State;

public class Market extends Building{

	public static final int BASE_HP_POTION_EFFECT = 100;
	
	public static final int EXTRA_MONEY_ROW = 5;
	public static final int EXTRA_MONEY_DUNGEON = 100;
	
	public static final int BASE_POTION_COST = 1000;
	public static final int INC_POTION_COST = 1000;
	public static final int MAX_POTIONS = 3;
	
	public static final int BASE_IMPROVE_COST = 500;
	public static final int INC_IMPROVE_COST = 500;
	public static final int INC_IMPROVE_VALUE = 100;
	
	private Button stockPotion;
	private Button improvePotion;
	
	
	private Text inStock;
	private Text labelHpPotions;
	private List<Animation> hpPotions;
	
	private Stats data;
	
	public Market(State state, boolean built) {
		super(state, "Market", 2, built, 1000);
		
		data = state.character.getStats();
		
		inStock = SceneManager.createText("Currently in stock:", Color.BLACK, new Node(TargetMetrics.width*0.13f,TargetMetrics.height*0.075f));
		inStock.setVisible(false);
		
		hpPotions = new ArrayList<Animation>();
		labelHpPotions = SceneManager.createText("HP-Potions:", Color.BLACK, new Node(TargetMetrics.width*0.14f,TargetMetrics.height*0.12f));
		labelHpPotions.setVisible(false);
		for (int i = 0; i < data.getStat("HpPotions"); ++i)
		{
			Node potionNode =  new Node(TargetMetrics.width*0.15f + TargetMetrics.width*0.04f*i,TargetMetrics.height*0.12f);
			Animation hpPotion = SceneManager.createAnimation(Ressources.getScaledBitmap("potion", new Vector2(TargetMetrics.width*0.05f*5, TargetMetrics.width*0.05f)), 5,0, potionNode);
			hpPotions.add(hpPotion);
			hpPotion.setVisible(false);
		}
		
		stockPotion = WindowManager.createButton("Market/stockPotions", TargetMetrics.width, MenuConfig.TOP + MenuConfig.OFFSET, MenuConfig.WIDTH, MenuConfig.HEIGHT, "Stock HP-Potions");
		ImageBox costWindow = WindowManager.createImageBox(stockPotion.getName() + "/MoneyCost", stockPotion.getWidth()*0.4f+60, 10, 20, 20, Ressources.getScaledBitmap("money", new Vector2(20,20)));
		stockPotion.addChild(costWindow);
		costWindow.addChild(WindowManager.createLabel(costWindow.getName() + "/label", 20, 5, 20, 20, this.getCurrentStockPotionCost()+"", Color.TRANSPARENT));

		stockPotion.addClickListener(this);
		
		
		improvePotion = MenuConfig.createMenuItem("Market/improvePotions", "(Enlighten) Increase Potency", 2, this);
		costWindow = WindowManager.createImageBox(improvePotion.getName() + "/MoneyCost", improvePotion.getWidth()*0.4f+60, 10, 20, 20, Ressources.getScaledBitmap("money", new Vector2(20,20)));
		improvePotion.addChild(costWindow);
		costWindow.addChild(WindowManager.createLabel(costWindow.getName() + "/label", 20, 5, 20, 20, this.getCurrentImprovePotionCost()+"", Color.TRANSPARENT));
		improvePotion.setEnabled(false);
	}
	
	public void build()
	{
		super.build();
		state.character.getStats().setStat("ExtraMoney", state.character.getStats().getStat("ExtraMoney") + EXTRA_MONEY_ROW);
		state.character.getStats().setStat("ExtraMoneyFloor", state.character.getStats().getStat("ExtraMoneyFloor") + EXTRA_MONEY_DUNGEON);
		state.save();
	}
	
	public int getCurrentStockPotionCost()
	{
		return data.getStat("HpPotions") * INC_POTION_COST + BASE_POTION_COST;
	}

	public int getCurrentImprovePotionCost()
	{
		return data.getStat("PotionLevel") * INC_IMPROVE_COST + BASE_IMPROVE_COST;
	}
	
	@Override
	public String getDescription() {
		return  "Command your followers to build a market.\n\n+" + EXTRA_MONEY_ROW 
				+ " Gold per cleared row.\n+" + EXTRA_MONEY_DUNGEON 
				+ " Gold per cleared floor.\n\nCapable of selling potions.";
	}

	@Override
	public void enter() {	
		
		inStock.blendIn(new Vector2(0,0), MenuConfig.FADE_TIME);
		labelHpPotions.blendIn(new Vector2(0,0), MenuConfig.FADE_TIME);
		
		if (data.getStat("HpPotions") < 3)
		{
			stockPotion.moveTo(new Vector2(MenuConfig.LEFT, stockPotion.getY()), MenuConfig.FADE_TIME);
			stockPotion.setEnabled(true);
		}
		
		Label costs = (Label) WindowManager.getWindow(stockPotion.getName() + "/MoneyCost/label");
		costs.setCaption((this.getCurrentStockPotionCost()+INC_POTION_COST)+"");

		improvePotion.moveTo(new Vector2(MenuConfig.LEFT, improvePotion.getY()), MenuConfig.FADE_TIME);
		
		improvePotion.setVisible(true);
		improvePotion.getMovementListener(0).setListening(false);
		
		if (state.tutorial.isItemDone("EnlightPower"))
		{
			improvePotion.setEnabled(true);
		}
		
		
		while (hpPotions.size() > data.getStat("HpPotions"))
		{
			Image hpPotion = hpPotions.get(hpPotions.size()-1);
			SceneManager.RENDER_THREAD.removeRenderable(hpPotion);
			hpPotions.remove(hpPotions.size()-1);
		}
		
		for (Image potion : hpPotions)
		{
			potion.blendIn(new Vector2(0,0), MenuConfig.FADE_TIME);
		}
		
	}

	@Override
	public void leave() {
		stockPotion.moveTo(new Vector2(TargetMetrics.width, stockPotion.getY()), MenuConfig.FADE_TIME);
		stockPotion.setEnabled(false);

		improvePotion.moveTo(new Vector2(TargetMetrics.width, improvePotion.getY()), MenuConfig.FADE_TIME);
		improvePotion.setEnabled(false);
		improvePotion.getMovementListener(0).setListening(true);
		
		inStock.fadeOut(new Vector2(0,0), MenuConfig.FADE_TIME);
		labelHpPotions.fadeOut(new Vector2(0,0), MenuConfig.FADE_TIME);
		
		for (Image potion : hpPotions)
		{
			potion.fadeOut(new Vector2(0,0), MenuConfig.FADE_TIME);
		}
	
	
	}
	
	public void onClick(Event event) {
		if (event.getEvoker() == stockPotion)
		{
			final String text = "Let potions be stocked.\nInstead of dying, your followers\nwill use a potion.\nEach potion can only be used once.\nMax. 3 potions can be carried.\n\nHeals " + state.character.getStats().getStat("HpPotionEffect") + "Hp.";
			this.upgrade(text, stockPotion, this.getCurrentStockPotionCost(), this.getCurrentStockPotionCost() + INC_POTION_COST,"HpPotions", null);
		}
		else if (event.getEvoker() == improvePotion)
		{
			final String text = "Enlighten your follower in the ways of\nherbal skills.\n\n+" + INC_IMPROVE_VALUE + "Hp per potion";
			this.upgrade(text, improvePotion, this.getCurrentImprovePotionCost(), this.getCurrentImprovePotionCost() + INC_IMPROVE_COST,"PotionLevel", null);
		}
		else
		{
			super.onClick(event);
		}
	}
	
	protected void upgrade(String text, final Button clicked, final int costs, final int newCosts, final String levelInc, final String statInc)
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
						Label costs = (Label) WindowManager.getWindow(clicked.getName() + "/MoneyCost/label");
						costs.setCaption(newCosts+"");
						if (clicked == stockPotion) {
							Node potionNode =  new Node(TargetMetrics.width*0.15f + TargetMetrics.width*0.04f*hpPotions.size(),TargetMetrics.height*0.12f);
							Animation hpPotion = SceneManager.createAnimation(Ressources.getScaledBitmap("potion", new Vector2(TargetMetrics.width*0.05f*5, TargetMetrics.width*0.05f)), 5,0, potionNode);
							hpPotions.add(hpPotion);
							if (data.getStat("HpPotions") == 3)
							{
								stockPotion.disable();
								stockPotion.moveTo(new Vector2(TargetMetrics.width, stockPotion.getY()), MenuConfig.FADE_TIME);
							}
						} else if (clicked == improvePotion) {
							state.character.getStats().setStat("HpPotionEffect", state.character.getStats().getStat("HpPotionEffect") + INC_IMPROVE_VALUE);
						}
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
