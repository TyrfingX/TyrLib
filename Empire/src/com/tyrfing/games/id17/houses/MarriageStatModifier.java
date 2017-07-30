package com.tyrfing.games.id17.houses;

import com.tyrfing.games.id17.diplomacy.actions.Marriage;
import com.tyrfing.games.id17.world.World;

public class MarriageStatModifier extends StatModifier {
	private static final long serialVersionUID = 2598415741288020261L;
	
	
	public MarriageStatModifier(House house1, House house2) {
		super("HAS_MARRIAGE", House.HAS_MARRIAGE, house1, house2, Marriage.RELATION_DURATION, 1);
	}
	
	@Override
	public void apply() {
		super.apply();
		World.getInstance().getHouses().get(affected).countMarriages++;
	}
	
	@Override
	public void unapply() {
		super.unapply();
		World.getInstance().getHouses().get(affected).countMarriages--;
	}
}
