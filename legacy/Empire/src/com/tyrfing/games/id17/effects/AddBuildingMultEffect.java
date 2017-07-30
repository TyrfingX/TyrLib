package com.tyrfing.games.id17.effects;

import com.tyrfing.games.id17.buildings.Building;
import com.tyrfing.games.id17.holdings.Holding;
import com.tyrfing.games.id17.houses.House;

public class AddBuildingMultEffect implements IEffect {

	public final Building.TYPE type;
	public final float value;
	public final int stat;
	
	public AddBuildingMultEffect(String buildingName, int stat, float value) {
		this.type = Building.TYPE.valueOf(buildingName);
		this.value = value;
		this.stat = stat;
	}
	
	@Override
	public void apply(House house) {
		for (int i = 0; i < house.getHoldings().size(); ++i) {
			Holding h = house.getHoldings().get(i);
			Building b = h.isBuilt(type);
			if (b != null) {
				b.applyEffects(h, -b.getLevel());
			}
		}
		
		house.buildingMult[type.ordinal()][stat] += value;
		
		for (int i = 0; i < house.getHoldings().size(); ++i) {
			Holding h = house.getHoldings().get(i);
			Building b = h.isBuilt(type);
			if (b != null) {
				b.applyEffects(h, b.getLevel());
			}
		}
	}

	@Override
	public void unapply(House house) {
		for (int i = 0; i < house.getHoldings().size(); ++i) {
			Holding h = house.getHoldings().get(i);
			Building b = h.isBuilt(type);
			if (b != null) {
				b.applyEffects(h, -b.getLevel());
			}
		}
		
		house.buildingMult[type.ordinal()][stat] -= value;
		
		for (int i = 0; i < house.getHoldings().size(); ++i) {
			Holding h = house.getHoldings().get(i);
			Building b = h.isBuilt(type);
			if (b != null) {
				b.applyEffects(h, b.getLevel());
			}
		}
	}

}
