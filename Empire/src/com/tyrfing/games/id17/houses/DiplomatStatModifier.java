package com.tyrfing.games.id17.houses;

import com.tyrfing.games.id17.world.World;

public class DiplomatStatModifier extends StatModifier {

	
	/**
	 * 
	 */
	private static final long serialVersionUID = -6247019507517351718L;
	public static final float DURATION = 20 * World.DAYS_PER_YEAR * World.SECONDS_PER_DAY;
	public static final int COURT_POWER = 5;
	
	public DiplomatStatModifier(House sender, House receiver) {
		super("Diplomat", House.HAS_DIPLOMAT, sender, receiver, DURATION, 1);
	}
	

	@Override
	public void apply() {
		super.apply();
		World.getInstance().getHouses().get(target).changeHouseStat(World.getInstance().getHouses().get(affected), House.COURT_POWER, COURT_POWER);
	}
	
	@Override
	public void unapply() {
		if (World.getInstance().getHouses().get(target).getHouseStat(World.getInstance().getHouses().get(affected), House.HAS_DIPLOMAT) == 0) {
			World.getInstance().getHouses().get(target).endPacts(World.getInstance().getHouses().get(affected));
		}
	}

}
