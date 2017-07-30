package com.tyrfing.games.id17.networking;

import com.tyrfing.games.id17.houses.House;
import com.tyrfing.games.id17.laws.LawSet;
import com.tyrfing.games.id17.world.World;
import com.tyrlib2.networking.Connection;

public class ChangeLaw extends NetworkMessage {

	/**
	 * 
	 */
	private static final long serialVersionUID = 7142035476490088327L;

	public final short houseID;
	public final byte lawID;
	public final byte optionID;
	
	public ChangeLaw(int houseID, int lawID, int optionID) {
		this.houseID = (short) houseID;
		this.lawID = (byte) lawID;
		this.optionID = (byte) optionID;
	}
	
	@Override
	public String toString() {
		return "Change Law: " + houseID + ", " + lawID + ", " +  optionID;
	}
	
	@Override
	public void process(Connection c) {
		House h = World.getInstance().getHouses().get(houseID);
		LawSet.getLaw(lawID).selectOption(optionID, h);
	}
	
}
