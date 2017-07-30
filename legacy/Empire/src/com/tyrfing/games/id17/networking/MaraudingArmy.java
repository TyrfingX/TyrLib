package com.tyrfing.games.id17.networking;

import com.tyrfing.games.id17.intrigue.actions.Maraude;
import com.tyrlib2.networking.Connection;

public class MaraudingArmy extends NetworkMessage {


	/**
	 * 
	 */
	private static final long serialVersionUID = 3376894262954246781L;
	public final short armyID;
	
	public MaraudingArmy(short armyID)  {
		this.armyID = armyID;
	}
	
	@Override
	public void process(Connection c) {
		Maraude.createMaraudeSubFaction(armyID);
	}
	
}
