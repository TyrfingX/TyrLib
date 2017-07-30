package com.tyrfing.games.id17.networking;

import java.util.List;

import com.tyrfing.games.id17.houses.House;
import com.tyrfing.games.id17.intrigue.Intrigue;
import com.tyrfing.games.id17.intrigue.actions.IntrigueAction;
import com.tyrfing.games.id17.war.WarJustification;
import com.tyrfing.games.id17.world.World;
import com.tyrlib2.networking.Connection;


public class HouseState extends NetworkMessage {

	/**
	 * 
	 */
	private static final long serialVersionUID = -5123629404078826933L;

	public short houseID;
	public short overlordID;
	
	public short intrigueTarget;
	public byte intrigueID;
	public int[] intrigueOptions;
	
	public boolean[] researched;
	public boolean[] discovered;
	
	public List<WarJustification> justifications;
	
	@Override
	public void process(Connection c) {
		List<House> houses = World.getInstance().getHouses();
		
		House house = houses.get(houseID);
		
		if (overlordID != -1) {
			House overlord = houses.get(overlordID);
			overlord.addSubHouse(house);
		} else {
			if (house.getOverlord() != null) {
				house.getOverlord().removeSubHouse(house);
			}
		}
		
		if (intrigueID != -1) {
			IntrigueAction a = Intrigue.actions.get(intrigueID);
			a.startProjectNoNetwork(house, houses.get(intrigueTarget), intrigueOptions);
		}
		
		for (int i = 0; i < researched.length; ++i) {
			if (researched[i]) {
				World.getInstance().techTreeSet.trees[0].techs[i].onApply(house, discovered[i]);
			}
		}
	}
	
}
