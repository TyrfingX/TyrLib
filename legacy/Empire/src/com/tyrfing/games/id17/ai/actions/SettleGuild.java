package com.tyrfing.games.id17.ai.actions;

import com.tyrfing.games.id17.buildings.Building;
import com.tyrfing.games.id17.buildings.Guild;
import com.tyrfing.games.id17.holdings.Barony;
import com.tyrfing.games.id17.holdings.Holding;
import com.tyrfing.games.id17.houses.House;
import com.tyrfing.games.id17.world.World;

public class SettleGuild extends AIAction {

	/**
	 * 
	 */
	private static final long serialVersionUID = 3022382383805722561L;

	/**
	 * OPTIONS
	 * 0: Holding ID
	 * 1: Guild ID
	 * 2: Optional Target Holding ID
	 */
	
	public SettleGuild() {
		super("Settle Guild");
	}

	@Override
	public boolean isEnabled(House executor, int[] options) {
		Holding h = World.getInstance().getHolding(options[0]);
		return 		executor.getGold() >= Building.getPrice(Building.TYPE.Guild, h)
			   && 	h.isBuilt(Building.TYPE.Guild) != null;
	}

	@Override
	public void execute(House executor, int[] options) {
		Holding h = World.getInstance().getHolding(options[0]);
		Guild g = (Guild) h.isBuilt(Building.TYPE.Guild);
		if (g != null) {
			Barony target = null;
			if (options[2] != -1) {
				target = World.getInstance().getBarony(options[2]);
			}
			
			g.setupGuild(Guild.TYPE.values()[options[1]], target, h);
		}
	}

}
