package com.tyrfing.games.id17.effects;

import com.tyrfing.games.id17.houses.House;

public class AddGoodMultEffect implements IEffect {

	public final int good;
	public final int stat;
	public final float value;
	
	public AddGoodMultEffect(int good, int stat, float value) {
		this.good = good;
		this.stat = stat;
		this.value = value;
	}
	
	@Override
	public void apply(House house) {
		if (good != -1) {
			house.goodsMult[good][stat] += value;
		} else {
			for (int i = 0; i < house.goodsMult.length; ++i) {
				house.goodsMult[i][stat] += value;
			}
		}
	}

	@Override
	public void unapply(House house) {
		if (good != -1) {
			house.goodsMult[good][stat] -= value;
		} else {
			for (int i = 0; i < house.goodsMult.length; ++i) {
				house.goodsMult[i][stat] -= value;
			}
		}
	}

}
