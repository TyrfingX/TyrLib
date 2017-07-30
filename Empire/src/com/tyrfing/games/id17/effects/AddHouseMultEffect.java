package com.tyrfing.games.id17.effects;

import com.tyrfing.games.id17.houses.House;

public class AddHouseMultEffect implements IEffect {

	public final int stat;
	public final float value;
	
	public AddHouseMultEffect(int stat, float value) {
		this.stat = stat;
		this.value = value;
	}
	
	@Override
	public void apply(House house) {
		house.stats[stat] += value;
	}

	@Override
	public void unapply(House house) {
		house.stats[stat] -= value;
	}

}
