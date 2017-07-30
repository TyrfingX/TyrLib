package tyrfing.games.id3.lib.Blessings;

import java.util.ArrayList;
import java.util.List;

import tyrfing.common.files.FileReader;
import tyrfing.common.files.FileWriter;
import tyrfing.common.math.Vector2;
import tyrfing.games.id3.lib.MainGame;
import tyrfing.games.id3.lib.MenuConfig;
import tyrfing.games.id3.lib.mechanics.State;

public class BlessingMenu {
	
	private List<Blessing> blessings;
	private State state;
	
	public BlessingMenu(State state)
	{
		this.state = state;
		state.blessingMenu = this;
		blessings = new ArrayList<Blessing>();
		this.createBlessings();
		
		if (state.newGame)
		{
			for (Blessing blessing : blessings)
			{
				if (blessing.getName().equals("Novice"))
					blessing.activate();
			}
			
			
			this.save();
		}
		else
		{
			this.load();
		}
		
		
	}
	
	public void display()
	{
		for (Blessing blessing : blessings)
		{
			if (blessing.isActivated())
			{
				blessing.blessing.enable();
				blessing.blessing.blendIn(0.5f);
			}
		}
	}
	
	public void hide()
	{
		for (Blessing blessing : blessings)
		{
			if (blessing.isActivated())
			{
				blessing.blessing.disable();
				blessing.blessing.fadeOut(0.5f);
			}
		}
	}
	
	private void createBlessings()
	{
		//T1
		blessings.add(new Blessing(state, "Novice", "Your followers gain 16Hp.", 100, 100, "Hp", 16, 0, 5, new Vector2(MenuConfig.LEFT, MenuConfig.OFFSET + MenuConfig.TOP), new String[]{"Warrior", "Mage", "Thief"}));
		
		//T2
		blessings.add(new Blessing(state, "Warrior", "Your followers gain 20Hp.", 500, 500, "Hp", 20, 0, 10, new Vector2(MenuConfig.LEFT, MenuConfig.OFFSET + MenuConfig.TOP), new String[]{"Knight"}));
		blessings.add(new Blessing(state, "Thief", "Your followers gain 4 extra gold\nper cleared row.", 300, 300, "ExtraMoney", 4, 0, 10, new Vector2(MenuConfig.LEFT, MenuConfig.OFFSET*2 + MenuConfig.TOP), new String[]{"Assassin"}));
		blessings.add(new Blessing(state, "Mage", "Your followers gain 1Atk.", 800, 800, "Atk", 1, 0, 10, new Vector2(MenuConfig.LEFT, MenuConfig.OFFSET*3 + MenuConfig.TOP), new String[]{"Arcmage"}));
		
		//T3
		blessings.add(new Blessing(state, "Knight", "Your followers gain 1Def.", 2000, 2000, "Def", 1, 0, 10, new Vector2(MenuConfig.LEFT, MenuConfig.OFFSET + MenuConfig.TOP), new String[]{"Paladin"}));
		blessings.add(new Blessing(state, "Assassin", "Your followers gain 10MovementSpeed", 1800, 1800, "Speed", 10, 0, 10, new Vector2(MenuConfig.LEFT, MenuConfig.OFFSET*2 + MenuConfig.TOP), new String[]{"Shadow"}));
		blessings.add(new Blessing(state, "Arcmage", "Your followers gain 2Atk.", 4000, 4000, "Atk", 2, 0, 10, new Vector2(MenuConfig.LEFT, MenuConfig.OFFSET*3 + MenuConfig.TOP), new String[]{"Sage"}));
	
		//T4
		blessings.add(new Blessing(state, "Paladin", "Your followers gain 1Hp/s HP-Regeneration.", 15000, 15000, "HP_REGEN", 1, 0, 10, new Vector2(MenuConfig.LEFT, MenuConfig.OFFSET + MenuConfig.TOP), null));
		blessings.add(new Blessing(state, "Shadow", "Your followers fight battles 20% faster.", 12000, 12000, "TICKS_PER_SECOND", 1, 0, 10, new Vector2(MenuConfig.LEFT, MenuConfig.OFFSET*2 + MenuConfig.TOP), null));
		blessings.add(new Blessing(state, "Sage", "Your followers gain 30% extra EXP.", 10000, 10000, "EXTRA_EXP", 30, 0, 10, new Vector2(MenuConfig.LEFT, MenuConfig.OFFSET*3 + MenuConfig.TOP), null));
	}
	
	public void save()
	{
		FileWriter.writeFile(MainGame.CONTEXT, "Blessings.bs", this.blessingsToString());
	}
	
	public void load()
	{
		if (FileReader.fileExists(MainGame.CONTEXT, "Blessings.bs"))
		{
			String data = FileReader.readFile(MainGame.CONTEXT, "Blessings.bs");
			String[] strBlessings = data.split(";");
			for (String strBlessing : strBlessings)
			{
				String[] strValue = strBlessing.split(":");
				boolean activated = Boolean.valueOf(strValue[1]);
				int upgrades = Integer.valueOf(strValue[2]);
				if (activated)
				{
					for (Blessing blessing : blessings)
					{
						if (blessing.getName().equals(strValue[0]))
						{
							if (blessing.maxUpgrades > upgrades)
							{
								blessing.upgrades = upgrades;
								blessing.activate();
								blessing.upgradeLabel.setCaption(blessing.upgrades + "/" + blessing.maxUpgrades);
							}
							break;
						}
					}
				}
			}
		}
	}
	
	private String blessingsToString()
	{
		String res = "";
		for (Blessing blessing : blessings)
		{
			res += blessing.getName() + ":" + blessing.isActivated() + ":" + blessing.upgrades + ";";
		}
		return res;
	}
	
	public void activateBlessing(String blessingName)
	{
		for (Blessing blessing : blessings)
		{
			if (blessing.getName().equals(blessingName))
			{
				blessing.activate();
				blessing.blessing.blendIn(0.5f);
			}
		}
		this.save();
	}
	
}
