package com.tyrfing.games.id18.model.battle;

import com.tyrfing.games.id18.model.unit.Faction;

public interface IObjective {
	public boolean isAchieved(Battle battle);
	public Faction getFaction();
}
