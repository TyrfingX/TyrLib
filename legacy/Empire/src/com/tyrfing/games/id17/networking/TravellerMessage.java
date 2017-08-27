package com.tyrfing.games.id17.networking;

import com.tyrfing.games.id17.holdings.Traveller;
import com.tyrlib2.networking.Connection;


public class TravellerMessage extends NetworkMessage {

	/**
	 * 
	 */
	private static final long serialVersionUID = -7682983190540001715L;

	private final short srcHolding;
	private final short dstHolding;
	
	public TravellerMessage(int srcHolding, int dstHolding) {
		this.srcHolding = (short) srcHolding;
		this.dstHolding = (short) dstHolding;
	}
	
	@Override
	public void process(Connection c) {
		new Traveller(srcHolding, dstHolding);
	}
	
}
