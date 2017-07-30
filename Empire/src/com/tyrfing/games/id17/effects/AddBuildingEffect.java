package com.tyrfing.games.id17.effects;

import com.tyrfing.games.id17.buildings.Building;
import com.tyrfing.games.id17.holdings.Holding;
import com.tyrfing.games.id17.houses.House;

public class AddBuildingEffect implements IEffect {

	public final String buildingName;
	public final String holdingType;
	
	public AddBuildingEffect(String buildingName, String holdingType) {
		this.buildingName = buildingName;
		this.holdingType = holdingType;
	}
	
	@Override
	public void apply(House house) {
		
		Building.TYPE type = Building.TYPE.valueOf(buildingName);
		
		for (int i = 0; i < house.getHoldings().size(); ++i) {
			Holding h = house.getHoldings().get(i);
			if (h.holdingData.typeName.equals(holdingType)) {
				Building building = h.isBuilt(type);
				if (building != null) {
					building.changeLevel(1);
				} else {
					building = Building.create(type, 1);
					h.addBuilding(building);
				}
			}
		}
	}

	@Override
	public void unapply(House house) {

	}

}
