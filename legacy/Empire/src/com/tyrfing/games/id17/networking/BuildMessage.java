package com.tyrfing.games.id17.networking;

import com.tyrfing.games.id17.EmpireFrameListener;
import com.tyrfing.games.id17.ai.actions.UpgradeBuilding;
import com.tyrfing.games.id17.buildings.Building;
import com.tyrfing.games.id17.holdings.Holding;
import com.tyrfing.games.id17.holdings.projects.BuildingProject;
import com.tyrfing.games.id17.world.World;
import com.tyrlib2.networking.Connection;


public class BuildMessage extends NetworkMessage {

	/**
	 * 
	 */
	private static final long serialVersionUID = 5135494018500065815L;

	public final byte type;
	public final short holdingID;
	public final short days;
	
	public BuildMessage(int type, int holdingID) {
		this.type = (byte) type;
		this.holdingID = (short) holdingID;
		this.days = -1;
	}
	
	public BuildMessage(int type, int holdingID, short days) {
		this.type = (byte) type;
		this.holdingID = (short) holdingID;
		this.days = days;
	}
	
	@Override
	public void process(Connection c) {
		Holding h = World.getInstance().getHolding(holdingID);
		
		if (	!EmpireFrameListener.MAIN_FRAME.getNetwork().isHost()
			|| 	UpgradeBuilding.sIsEnabled(h.getOwner(), new int[] { holdingID, type })) {
			h.startProject(new BuildingProject(Building.TYPE.values()[type], h, h.getOwner(), days));
		}
	}


	
}
