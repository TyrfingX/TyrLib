package com.tyrfing.games.id17.networking;

import com.tyrfing.games.id17.war.WarJustification;
import com.tyrfing.games.id17.world.World;
import com.tyrlib2.networking.Connection;

public class ChangeJustification extends NetworkMessage {
	/**
	 * 
	 */
	private static final long serialVersionUID = -5325810773410240119L;
	public static final byte ADD = 0;
	public static final byte REMOVE = 1;
	
	private WarJustification justification;
	private byte action;
	private short houseID;
	
	public ChangeJustification(WarJustification justification, short houseID, byte action) {
		this.justification = justification;
		this.houseID = houseID;
		this.action = action;
	}
	
	@Override
	public void process(Connection c) {
		if (houseID < World.getInstance().getHouses().size()) {
			if (action == ADD) {
				World.getInstance().getHouses().get(houseID).addJustification(justification);
			} else if (action == REMOVE) {
				World.getInstance().getHouses().get(houseID).removeJustification(justification);
			}
		}
	}
}
