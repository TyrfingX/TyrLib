package com.tyrfing.games.id17.networking;

import com.tyrfing.games.id17.holdings.Holding;
import com.tyrfing.games.id17.holdings.UnrestSource;
import com.tyrfing.games.id17.world.World;
import com.tyrlib2.networking.Connection;

public class UnrestSourceChange extends NetworkMessage {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = -616162346377410258L;
	
	public final static byte ADD = 0;
	public final static byte REMOVE = 1;
	public final static byte CLEAR = 2;
	
	public final UnrestSource unrest;
	public final byte action;
	public short holdingID;
	
	public UnrestSourceChange(short holdingID, UnrestSource unrest, byte action) {
		this.unrest = unrest;
		this.action = action;
		this.holdingID = holdingID;
	}
	
	@Override
	public void process(Connection c) {
		Holding h = World.getInstance().getHolding(holdingID);
		switch(action) {
		case ADD:
			h.addUnrestSource(unrest);
			break;
		case REMOVE:
			h.removeUnrestSource(unrest);
			break;
		case CLEAR:
			h.clearUnrest();
			break;
		default:
			throw new RuntimeException("UnrestSourceChange::process: Invalid UnrestSource action!");
		}
	}
}
