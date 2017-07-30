package com.tyrfing.games.id17.networking;

import java.util.List;

import com.tyrfing.games.id17.houses.House;
import com.tyrfing.games.id17.technology.TechnologyProject;
import com.tyrfing.games.id17.world.World;
import com.tyrlib2.networking.Connection;

public class TechnologyEvent extends NetworkMessage {

	/**
	 * 
	 */
	private static final long serialVersionUID = -5711290235979894750L;

	public final byte techID;
	public final short houseID;
	public final short fromHouseID;
	
	public TechnologyEvent(int techID, int houseID, int fromHouseID) {
		this.techID = (byte) techID;
		this.houseID = (short) houseID;
		this.fromHouseID = (short) fromHouseID;
	}
	
	@Override
	public void process(Connection c) {
		List<House> houses = World.getInstance().getHouses();
		
		House house = houses.get(houseID);
		if (fromHouseID == -1) {
			if (house.techProject == null || house.techProject.tech.ID != techID) {
				house.startTechnologyProject(new TechnologyProject(house, World.getInstance().techTreeSet.trees[0].techs[techID]));
			} else {
				house.techProject.finish();
			}
		} else {
			World.getInstance().techTreeSet.trees[0].techs[techID].onSpread(houses.get(fromHouseID), house);
		}
	}
}
