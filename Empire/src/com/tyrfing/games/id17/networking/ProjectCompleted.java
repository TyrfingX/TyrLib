package com.tyrfing.games.id17.networking;

import java.util.List;

import com.tyrfing.games.id17.holdings.Holding;
import com.tyrfing.games.id17.world.World;
import com.tyrlib2.networking.Connection;

public class ProjectCompleted extends NetworkMessage {

	/**
	 * 
	 */
	private static final long serialVersionUID = -7977413462083627791L;

	public final short holdingID;
	
	public ProjectCompleted(int holdingID) {
		this.holdingID = (short) holdingID;
	}
	
	@Override
	public void process(Connection c) {
		List<Holding> holdings = World.getInstance().getHoldings();
		
		Holding h = holdings.get(holdingID);
		try {
			h.finishActiveProject();
		} catch (Exception e) {
			System.out.println(h.getFullName());
			e.printStackTrace();
		}
	}
	
}
