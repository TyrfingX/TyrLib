package com.tyrfing.games.id17.networking;

import com.tyrfing.games.id17.holdings.Barony;
import com.tyrfing.games.id17.houses.House;
import com.tyrfing.games.id17.world.World;
import com.tyrlib2.networking.Connection;

public class AddVisibleBarony extends NetworkMessage {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 8270322517437612555L;
	
	public final short houseID;
	public final short baronyID;
	public final boolean discovery;
	
	public AddVisibleBarony(short houseID, short baronyID, boolean discovery) {
		this.houseID = houseID;
		this.baronyID = baronyID;
		this.discovery = discovery;
	}
	
	@Override
	public void process(Connection c) {
		House h = World.getInstance().getHouses().get(houseID);
		Barony b = World.getInstance().getBarony(baronyID);
		h.addVisibleBarony(b, discovery);
	}
	
	
}
