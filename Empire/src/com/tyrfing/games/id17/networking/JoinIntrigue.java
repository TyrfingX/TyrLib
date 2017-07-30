package com.tyrfing.games.id17.networking;

import java.util.List;

import com.tyrfing.games.id17.houses.House;
import com.tyrfing.games.id17.world.World;
import com.tyrlib2.networking.Connection;

public class JoinIntrigue extends NetworkMessage {

	/**
	 * 
	 */
	private static final long serialVersionUID = 3779588275136139622L;

	
	public final short supporterID;
	public final short supportedID;
	
	/**
	 * 
	 * @param supporterID
	 * @param supportedID
	 */
	
	public JoinIntrigue(int supporterID, int supportedID) {
		this.supportedID = (short) supportedID;
		this.supporterID = (short) supporterID;
	}
	
	@Override
	public void process(Connection c) {
		List<House> houses = World.getInstance().getHouses();
		
		if (houses.get(supportedID).intrigueProject != null) {
			houses.get(supportedID).intrigueProject.addSupporter(houses.get(supporterID));
		}
	}
}
