package com.tyrfing.games.id18.model.battle;

import java.io.Serializable;

import com.tyrfing.games.id18.model.unit.Faction;

public interface IObjective extends Serializable {
	public boolean isAchieved(Battle battle);
	public Faction getFaction();
}
