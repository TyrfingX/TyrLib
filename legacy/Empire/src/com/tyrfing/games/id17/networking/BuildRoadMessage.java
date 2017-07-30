package com.tyrfing.games.id17.networking;

import com.tyrfing.games.id17.EmpireFrameListener;
import com.tyrfing.games.id17.holdings.Holding;
import com.tyrfing.games.id17.holdings.projects.RoadProject;
import com.tyrfing.games.id17.world.World;
import com.tyrlib2.networking.Connection;

public class BuildRoadMessage extends NetworkMessage {

	private static final long serialVersionUID = -9169213749524553379L;
	
	public final short fromID;
	public final short toID;
	
	public BuildRoadMessage(int fromID, int toID) {
		this.fromID = (short) fromID;
		this.toID = (short) toID;
	}
	
	@Override
	public void process(Connection c) {
		Holding from = World.getInstance().getHolding(fromID);
		Holding to = World.getInstance().getHolding(toID);
		
		if (	!EmpireFrameListener.MAIN_FRAME.getNetwork().isHost()
			||  RoadProject.canBuild(from, to)) {
			from.startProject(new RoadProject(from, to));
		}
	}
}
