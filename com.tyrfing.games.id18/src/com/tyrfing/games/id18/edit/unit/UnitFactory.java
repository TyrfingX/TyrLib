package com.tyrfing.games.id18.edit.unit;

import com.tyrfing.games.id18.model.unit.Faction;
import com.tyrfing.games.id18.model.unit.Unit;

public class UnitFactory {
	public static final UnitFactory INSTANCE = new UnitFactory();
	
	public Unit createUnit(Faction faction) {
		Unit unit = new Unit();
		unit.setFaction(faction);
		
		return unit;
	}
}
