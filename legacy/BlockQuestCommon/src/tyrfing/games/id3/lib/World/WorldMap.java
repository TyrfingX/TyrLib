package tyrfing.games.id3.lib.World;

import java.util.HashMap;
import java.util.Map;
import java.util.Observable;

import android.graphics.Color;
import tyrfing.common.math.Vector2;
import tyrfing.common.render.TargetMetrics;
import tyrfing.common.ui.ClickListener;
import tyrfing.common.ui.Event;
import tyrfing.common.ui.Window;
import tyrfing.common.ui.WindowManager;
import tyrfing.common.ui.widgets.Label;
import tyrfing.games.id3.lib.MainGame;
import tyrfing.games.id3.lib.PaperSkin;
import tyrfing.games.id3.lib.R;
import tyrfing.games.id3.lib.Rituals.RitualFactory;
import tyrfing.games.id3.lib.Rituals.RitualType;
import tyrfing.games.id3.lib.mechanics.State;
import tyrfing.games.id3.lib.prayers.PrayerPower;
import tyrfing.games.id3.lib.prayers.PrayerType;


public class WorldMap extends Observable implements ClickListener {
	
	private Map<String, Location> map;
	private Label header; 
	
	public static final Vector2 mapItemSize = new Vector2(TargetMetrics.width*0.5f, TargetMetrics.width*0.5f);
	
	
	private static final String worldMapMessage = "As you gaze downwards, you see the mortal world\nunfolding before you...";
	
	private State state;
	
	public WorldMap(State state)
	{
		
		map = new HashMap<String, Location>();

		header = WindowManager.createLabel("worldMapMessage", 0, 0, TargetMetrics.width, 100, worldMapMessage, Color.TRANSPARENT);
		header.setVisible(false);
		
		Location loc = new Dungeon(state);
		map.put("WorldMap/" + loc.getName(), loc);
		loc = new City(state);
		map.put("WorldMap/" + loc.getName(), loc);
		loc = new Shrine(state);
		map.put("WorldMap/" + loc.getName(), loc);
		
		this.state = state;

		
	}

	@Override
	public void onClick(Event event) {
		Window evoker = event.getEvoker();
		
		Location location = map.get(evoker.getName());
		location.openMenu();
		
		this.hide();

	}
	
	public void hide()
	{
		for (Location item : map.values())
		{
			item.getMapItem().disable();
			item.getMapItem().fadeOut(0.5f);
		}
		
		header.fadeOut(0.5f);	
	}
	

	public void show()
	{
		if (state.hp == null) state.createHeroPreview();
		state.playerMoney.setVisible(true);
		
		for (Location item : map.values())
		{
			item.getMapItem().enable();
			item.getMapItem().blendIn(0.5f);
		}		
		
		header.blendIn(0.5f);
	
		if (!state.tutorial.isItemDone("Welcome"))
		{
			final String welcomeMsg = "Welcome to Dungeon God!\nYou are the local deity of this small area,\nwhich has nothing much of value except...\nA dungeon!\n\nNow it is up to you to guide your followers\ntowards everlasting glory..!"; 
			state.tutorial.createInfo(welcomeMsg);
			state.tutorial.doItem("Welcome");
		}
		
		if (state.deepestLevel >= 7 && !state.tutorial.isItemDone("Market")) {
			City city = (City) map.get("WorldMap/" + MainGame.getString(R.string.city));	
			city.addBuilding(new Smith(state, false));
			state.tutorial.createInfo("Your advances within the dungeon have\nlet the trade within the city flourish,\nattracting skilled craftsman.\n\nA \"Smithery\" can now be built.");
			state.tutorial.doItem("Smith");
			state.save();
		} else if (state.deepestLevel >= 7 && !state.tutorial.isItemDone("Vision")) {
			state.tutorial.createInfo("You feel your follower's trust for you\ndeepening...\n\nYou can now perform the \"Ritual of Vision\".");
			state.tutorial.doItem("Vision");
			RitualFactory ritualFactory = new RitualFactory();
			state.addRitual(ritualFactory.create(RitualType.RITUAL_OF_VISION));
			state.save(); 
		} else if (state.deepestLevel >= 12 && !state.tutorial.isItemDone("Smith")) {
			City city = (City) map.get("WorldMap/" + MainGame.getString(R.string.city));	
			city.addBuilding(new Market(state, false));
			state.tutorial.createInfo("The sold loot brought in by the\n adventurers sparks the interest of the\noutside world.\n\nA \"Market\" can now be built.");
			state.tutorial.doItem("Market");
			state.save();
		} else if (state.deepestLevel >= 35 && !state.tutorial.isItemDone("Prayer") && state.hasPower("Blessings")) {
			state.tutorial.createInfo("You feel your follower's trust for you\ndeepening...\n\nYou can now perform the \"Ritual of Prayer\".");
			state.tutorial.doItem("Prayer");
			RitualFactory ritualFactory = new RitualFactory();
			state.addRitual(ritualFactory.create(RitualType.RITUAL_OF_PRAYER));
			state.save(); 
		} else if (state.deepestLevel >= 17 && !state.tutorial.isItemDone("Enlighten") && state.tutorial.isItemDone("VisionPower")) {
			state.tutorial.createInfo("You feel your follower's trust for you\ndeepening...\n\nYou can now perform the\n\"Ritual of Enlightenment\".");
			state.tutorial.doItem("Enlighten");
			RitualFactory ritualFactory = new RitualFactory();
			state.addRitual(ritualFactory.create(RitualType.RITUAL_OF_ENLIGHTENMENT));
			state.save();	
		} else if (state.deepestLevel >= 21 && !state.tutorial.isItemDone("TradeRoute1") && ((City) map.get("WorldMap/" + MainGame.getString(R.string.city))).isBuilt("Market")) {
			state.tutorial.createInfo("The growth of the city's market\nhas attracted nearby merchants..\n\n\nA trade route has been established.\n\n+100 Gold per cleared floor");
			state.character.getStats().setStat("ExtraMoneyFloor", state.character.getStats().getStat("ExtraMoneyFloor") + 100);
			state.tutorial.doItem("TradeRoute1");
			state.save();
		} else if (state.deepestLevel >= 27 && !state.tutorial.isItemDone("Alchemy") && ((City) map.get("WorldMap/" + MainGame.getString(R.string.city))).isBuilt("Market")) {
			state.tutorial.createInfo("You feel your follower's trust for you\ndeepening...\n\nYou can now perform the\n\"Ritual of Alchemy\".");
			state.tutorial.doItem("Alchemy");
			RitualFactory ritualFactory = new RitualFactory();
			state.addRitual(ritualFactory.create(RitualType.RITUAL_OF_ALCHEMY));
			state.save();	
		} else if (state.deepestLevel >= 41 && !state.tutorial.isItemDone("PowerUp1")) {
			state.tutorial.createInfo("From the depths of the dungeon you\nfeel can something...\nA presence, ever so slighty.\nIt reminds you of something...\n\nMonsters in the dungeon have\nbecome stronger!");
			state.tutorial.doItem("PowerUp1");
			state.save();
		}
		
		if (state.hasPower("Prayer") && Math.random() <= 0.5f)
		{
			PrayerPower prayerPower = (PrayerPower)state.getPower("Prayer");
			double random = Math.random();
			if (random <= 0.3333f)
			{
				prayerPower.addPrayer(PrayerType.LOST, (int)(Math.random()*(state.deepestLevel-5))+5);
			}
			else if (random <= 0.6666f)
			{
				prayerPower.addPrayer(PrayerType.RUN, (int)(Math.random()*(state.deepestLevel-6))+6);
			}
			else
			{
				prayerPower.addPrayer(PrayerType.QUAKE, (int)(Math.random()*(state.deepestLevel-3))+3);
			}
			state.save();
		}
		
		
	}
	
	public void construct()
	{
		for (Location item : map.values())
		{
			item.construct();
		}	
		
		WindowManager.replaceSkin(new PaperSkin());
		
	}
	
	public String buildingsToString()
	{	
		City city = (City) map.get("WorldMap/" + MainGame.getString(R.string.city));
		return city.buildingsToString();		
	}
	
	public void constructBuildings(String[] buildings)
	{
		City city = (City) map.get("WorldMap/" + MainGame.getString(R.string.city));
		for (String building : buildings)
		{
			String[] buildingData = building.split(",");
			if (buildingData[0].equals("Smithery"))
			{
				city.addBuilding(new Smith(state, Boolean.valueOf(buildingData[1])));
			}
			if (buildingData[0].equals("Market"))
			{
				city.addBuilding(new Market(state, Boolean.valueOf(buildingData[1])));
			}
		}
	}
	
	public Location getLocation(String name)
	{
		return map.get("WorldMap/" + name);
	}
	
}
