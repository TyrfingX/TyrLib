package com.tyrfing.games.id17.networking;

import com.tyrfing.games.id17.world.World;
import com.tyrlib2.networking.Connection;

public class ChooseHouse extends NetworkMessage {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = -3742631354332675104L;

	public final int houseID;
	
	public ChooseHouse(int houseID) {
		this.houseID = houseID;
	}
	
	@Override
	public void process(Connection c) {
		NetworkController hc = World.getInstance().getPlayer(c);
		hc.control(World.getInstance().getHouses().get(houseID));
	}
}
