package com.tyrfing.games.id17.effects;

import com.tyrfing.games.id17.buildings.Building;
import com.tyrfing.games.id17.houses.House;

public class EnableBuildingEffect implements IEffect {

	public final String[] buildingNames;
	public final String[] holdingTypes;
	
	public EnableBuildingEffect(String[] buildingNames, String[] holdingTypes) {
		this.buildingNames = buildingNames;
		this.holdingTypes = holdingTypes;
	}
	
	@Override
	public void apply(House house) {
		for (int i = 0; i < buildingNames.length; ++i) {
			int buildingID = Building.TYPE.valueOf(buildingNames[i]).ordinal();
			for (int j = 0; j < holdingTypes.length; ++j) {
				house.enableBuilding(holdingTypes[j], buildingID);
			}
		}
	}

	@Override
	public void unapply(House house) {
		for (int i = 0; i < buildingNames.length; ++i) {
			int buildingID = Building.TYPE.valueOf(buildingNames[i]).ordinal();
			for (int j = 0; j < holdingTypes.length; ++j) {
				house.disableBuilding(holdingTypes[j], buildingID);
			}
		}
	}

}
