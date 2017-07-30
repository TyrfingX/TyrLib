package com.tyrfing.games.id17.events;

import com.tyrfing.games.id17.houses.House;
import com.tyrlib2.game.IUpdateable;

public abstract class Event implements IUpdateable {
	public abstract boolean conditionsMet(House house);
	public abstract void activate();
}
