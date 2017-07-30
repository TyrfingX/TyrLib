package com.tyrfing.games.id17.effects;

import com.tyrfing.games.id17.houses.House;
import com.tyrfing.games.id17.war.UnitType;

public class EnableUnitEffect implements IEffect {

	public final String unitName;
	
	public EnableUnitEffect(String unitName) {
		this.unitName = unitName;
	}
	
	@Override
	public void apply(House house) {
		house.enableUnit(UnitType.valueOf(unitName));
	}

	@Override
	public void unapply(House house) {
		house.disableUnit(UnitType.valueOf(unitName));
	}

}
