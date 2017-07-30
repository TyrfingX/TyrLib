package com.tyrfing.games.id17.effects;

import com.tyrfing.games.id17.houses.House;

public interface IEffect {
	public void apply(House house);
	public void unapply(House house);
}
