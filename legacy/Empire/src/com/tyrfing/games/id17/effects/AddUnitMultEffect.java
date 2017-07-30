package com.tyrfing.games.id17.effects;

import com.tyrfing.games.id17.houses.House;
import com.tyrfing.games.id17.war.UnitType;

public class AddUnitMultEffect implements IEffect {

	public final String unitName;
	public final float value;
	
	public AddUnitMultEffect(String unitName, float value) {
		this.unitName = unitName;
		this.value = value;
	}
	
	@Override
	public void apply(House house) {
		house.unitTypeMult[UnitType.valueOf(unitName).ordinal()] += value;
	}

	@Override
	public void unapply(House house) {
		house.unitTypeMult[UnitType.valueOf(unitName).ordinal()] -= value;
	}

}
