package com.tyrfing.games.id17.networking;

import java.util.List;

import com.tyrfing.games.id17.holdings.Holding;
import com.tyrfing.games.id17.houses.House;
import com.tyrfing.games.id17.world.World;
import com.tyrlib2.networking.Connection;


public class ChangeHoldingOwner extends NetworkMessage {

	/**
	 * 
	 */
	private static final long serialVersionUID = -2054928905516386497L;

	public final short holdingID;
	public final short targetID;
	
	public ChangeHoldingOwner(int holdingID, int targetID) {
		this.holdingID = (short) holdingID;
		this.targetID = (short) targetID;
	}
	
	@Override
	public void process(Connection c) {
		List<Holding> holdings = World.getInstance().getHoldings();
		List<House> houses = World.getInstance().getHouses();
		
		Holding h = holdings.get(holdingID);
		House.transferHolding(houses.get(targetID), h, true);
	}
	
}
