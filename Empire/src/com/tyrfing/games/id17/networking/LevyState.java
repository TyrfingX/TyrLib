package com.tyrfing.games.id17.networking;

import java.util.List;

import com.tyrfing.games.id17.holdings.Holding;
import com.tyrfing.games.id17.war.Army;
import com.tyrfing.games.id17.world.World;
import com.tyrlib2.networking.Connection;

public class LevyState extends NetworkMessage {
	/**
	 * 
	 */
	private static final long serialVersionUID = -2431501688720915430L;
	
	public final short[] holdingIDs;
	public final boolean[] raised;
	public final short[] movingTo;
	public final byte[] unitType = new byte[Army.MAX_REGIMENTS];
	
	public LevyState(int countArmies) {
		holdingIDs = new short[countArmies];
		raised = new boolean[countArmies];
		movingTo = new short[countArmies];
	}
	
	@Override
	public void process(Connection c) {
		List<Army> armies = World.getInstance().armies;
		List<Holding> holdings = World.getInstance().getHoldings();
		
		for (int i = 0; i < raised.length; ++i) {
			if (raised[i]) {
				Army levy = armies.get(i);
				levy.raise(holdings.get(holdingIDs[i]));
				
				if (movingTo[i] != -1) {
					levy.moveTo(holdings.get(movingTo[i]));
				}
			}
		}
	}
}
