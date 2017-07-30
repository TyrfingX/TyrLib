package com.tyrfing.games.id17.networking;

import java.util.List;

import com.tyrfing.games.id17.houses.House;
import com.tyrfing.games.id17.war.War;
import com.tyrfing.games.id17.world.World;
import com.tyrlib2.networking.Connection;


public class EndWar extends NetworkMessage {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 2907800511837159612L;
	public final short warID;
	public final short houseID;
	
	public EndWar(int warID, int houseID) {
		this.warID = (short) warID;
		this.houseID = (short) houseID;
	}
	
	@Override
	public void process(Connection c) {
		List<House> houses = World.getInstance().getHouses();
		
		House house = houses.get(houseID);
		if (warID < house.getCountWars()) {
			War war = house.getWar(warID);
			war.end();
		}
	}
	
	public String toString() {
		return "End War: " + warID + "," + houseID;
	}
}
