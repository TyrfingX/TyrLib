package tyrfing.games.id3.lib.mechanics;

import java.util.ArrayList;
import java.util.Observable;
import java.util.Observer;

import android.graphics.Color;
import tyrfing.common.files.FileReader;
import tyrfing.common.files.FileWriter;
import tyrfing.common.game.BaseGame;
import tyrfing.common.math.Vector2;
import tyrfing.common.render.Ressources;
import tyrfing.common.render.SceneManager;
import tyrfing.common.render.TargetMetrics;
import tyrfing.common.ui.WindowManager;
import tyrfing.common.ui.widgets.Label;
import tyrfing.games.id3.lib.BlockQuestActivity;
import tyrfing.games.id3.lib.MainGame;
import tyrfing.games.id3.lib.MenuConfig;
import tyrfing.games.id3.lib.Blessings.BlessingMenu;
import tyrfing.games.id3.lib.GodPowers.Power;
import tyrfing.games.id3.lib.GodPowers.PowerFactory;
import tyrfing.games.id3.lib.GodPowers.PowerType;
import tyrfing.games.id3.lib.Rituals.Ritual;
import tyrfing.games.id3.lib.Rituals.RitualFactory;
import tyrfing.games.id3.lib.Rituals.RitualType;
import tyrfing.games.id3.lib.World.WorldMap;
import tyrfing.games.id3.lib.prayers.PrayerPower;


public class State implements Observer {
	public Player character;
	public int deepestLevel;
	public Floor currentFloor;
	public WorldMap worldMap;
	public ArrayList<Power> powers;
	public ArrayList<Ritual> rituals;
	public Tutorial tutorial;
	public boolean newGame;
	public BlessingMenu blessingMenu;
	
	public Label playerMoney;
	public Label hp;
	public Label atk;
	public Label def;
	public Label exp;
	public Label lvl;
	
	public State(boolean loadData)
	{
		SceneManager.RENDER_THREAD.pause();
		powers = new ArrayList<Power>();
		PowerFactory powerFactory = new PowerFactory();
		rituals = new ArrayList<Ritual>();
		RitualFactory ritualFactory = new RitualFactory();
		
		if(loadData)
		{
			
			newGame = false;
			
			character = new Player("character.bs");
			deepestLevel = Integer.parseInt(FileReader.readFile(MainGame.CONTEXT, "floors.bs"));
			String[] strPowers = FileReader.readFile(MainGame.CONTEXT, "powers.bs").split(";");
			
			for (String strPower : strPowers)
			{
				if (!strPower.equals(""))
				{
					if (!this.hasPower(strPower))
					{
						addPower(powerFactory.create(PowerType.valueOf(strPower)));
					}
				}
			}

			String[] strRituals = FileReader.readFile(MainGame.CONTEXT, "rituals.bs").split(";");
			for (String strRitual : strRituals)
			{
				if (!strRitual.equals(""))
				{
					addRitual(ritualFactory.create(RitualType.valueOf(strRitual)));
				}
			}
			
			tutorial = new Tutorial("tutorial.bs");
			
		}
		else
		{	
			
			newGame = true;
			character = new Player();
			deepestLevel = 1;
			addPower(powerFactory.create(PowerType.RITUALS));
			addRitual(ritualFactory.create(RitualType.RITUAL_OF_BLESSINGS));
			this.save();
			FileWriter.writeFile(MainGame.CONTEXT, "buildings.bs","");
			FileWriter.writeFile(BaseGame.CONTEXT, "prayers.bs", "");
			tutorial = new Tutorial();
			
			BlockQuestActivity.tracker.trackPageView("/Level1");
			BlockQuestActivity.tracker.dispatch();


		}

		
		playerMoney = WindowManager.createLabel("playerMoney", MenuConfig.LEFT + MenuConfig.WIDTH+TargetMetrics.width*0.05f, 10, 100, 20, character.getMoney()+"", Color.TRANSPARENT);
		playerMoney.addChild(WindowManager.createImageBox("playerMoney/Image", TargetMetrics.width*0.0125f, 0, TargetMetrics.width*0.05f, TargetMetrics.width*0.05f, Ressources.getScaledBitmap("money", new Vector2(20,20))));
		playerMoney.setVisible(false);
		
		worldMap = new WorldMap(this);
		worldMap.construct();
		
		String[] buildings = FileReader.readFile(MainGame.CONTEXT, "buildings.bs").split(";");
		worldMap.constructBuildings(buildings);
	
		
		if (this.hasPower("Prayer"))
		{
			if (FileReader.fileExists(BaseGame.CONTEXT, "prayers.bs"))
			{
				PrayerPower power = (PrayerPower)this.getPower("Prayer");
				power.setState(this);
				power.init(FileReader.readFile(BaseGame.CONTEXT, "prayers.bs"));
			}
		}
		
		SceneManager.RENDER_THREAD.unPause();
	
	}
	
	public void createHeroPreview()
	{
		hp = WindowManager.createLabel("HP", TargetMetrics.width*0.06f, 10, 50, 20, character.getStats().getStat("Hp")  + "/" + character.getStats().getStat("MaxHp"), Color.TRANSPARENT);
		hp.addChild(WindowManager.createImageBox("HP/Image", -TargetMetrics.width*0.04f, 0, TargetMetrics.width*0.05f, TargetMetrics.width*0.05f, Ressources.getScaledBitmap("heart", new Vector2(20,20))));
		atk = WindowManager.createLabel("ATK", 0.25f*TargetMetrics.width, 10, 50, 20, character.getStats().getStat("Atk").toString(), Color.TRANSPARENT);
		atk.addChild(WindowManager.createImageBox("ATK/Image", -TargetMetrics.width*0.025f, 0, TargetMetrics.width*0.05f, TargetMetrics.width*0.05f, Ressources.getScaledBitmap("sword", new Vector2(20,20))));
		def = WindowManager.createLabel("DEF", 0.375f*TargetMetrics.width, 10, 50, 20, character.getStats().getStat("Def").toString(), Color.TRANSPARENT);
		def.addChild(WindowManager.createImageBox("DEF/Image", -TargetMetrics.width*0.025f, 0, TargetMetrics.width*0.05f, TargetMetrics.width*0.05f, Ressources.getScaledBitmap("shield", new Vector2(20,20))));
		lvl = WindowManager.createLabel("LVL", 0.44f*TargetMetrics.width, 10, 100, 20, "Lvl. " + character.getStats().getStat("Level"), Color.TRANSPARENT);
		exp = WindowManager.createLabel("EXP", 0.625f*TargetMetrics.width, 10, 50, 20, "Exp: " + character.getStats().getStat("Exp") + "/" + character.getStats().getStat("NextExp"), Color.TRANSPARENT);
	}
	
	public void updateHeroPreview()
	{
		hp.setCaption(character.getStats().getStat("Hp") + "/" + character.getStats().getStat("MaxHp"));
		lvl.setCaption("Lvl. " + character.getStats().getStat("Level"));
		exp.setCaption("Exp: " + character.getStats().getStat("Exp") + "/" + character.getStats().getStat("NextExp"));
		atk.setCaption(character.getStats().getStat("Atk").toString());
		def.setCaption(character.getStats().getStat("Def").toString());
		playerMoney.setCaption(character.getMoney()+"");
	}
	
	public boolean hasPower(String name)
	{
		for (Power power : powers)
		{
			if (power.getName().equals(name))
			{
				return true;
			}
		}
		return false;
	}

	public Power getPower(String name)
	{
		for (Power power : powers)
		{
			if (power.getName().equals(name))
			{
				return power;
			}
		}
		return null;
	}
	
	public void save()
	{
		if (hp != null) this.updateHeroPreview();
		FileWriter.writeFile(BaseGame.CONTEXT, "character.bs", character.toString());
		FileWriter.writeFile(BaseGame.CONTEXT, "floors.bs", deepestLevel+"");
		FileWriter.writeFile(BaseGame.CONTEXT, "powers.bs", powersToString());
		FileWriter.writeFile(BaseGame.CONTEXT, "rituals.bs", ritualsToString());
		if (worldMap != null) FileWriter.writeFile(BaseGame.CONTEXT, "buildings.bs",worldMap.buildingsToString());
		if (blessingMenu != null) blessingMenu.save();
		if (this.hasPower("Prayer"))
		{
			PrayerPower prayerPower = (PrayerPower) this.getPower("Prayer");
			FileWriter.writeFile(BaseGame.CONTEXT, "prayers.bs", prayerPower.toString());
		}
	}
	
	private String powersToString()
	{
		String res = "";
		for (Power power : powers)
		{
			res += power.getType() + ";";
		}
		
		return res;
	}
	
	private String ritualsToString()
	{
		String res = "";
		for (Ritual ritual : rituals)
		{
			res += ritual.getType() + ";";
		}
		
		return res;		
	}
	
	@Override
	public void update(Observable arg0, Object arg1) {
		if (arg0 == tutorial)
		{
			worldMap.show();
			tutorial.deleteObserver(this);
		}
	}
	
	public void addPower(Power power) {
		for (Power power2 : powers) {
			if (power2.getType().equals(power.getType())) {
				return;
			}
		}
		
		powers.add(power);
	}
	
	public void addRitual(Ritual ritual)  {
		for (Ritual ritual2 : rituals) {
			if (ritual2.getType().equals(ritual.getType())) {
				return;
			}
		}
		
		rituals.add(ritual);
		
	}
}
