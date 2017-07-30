package com.tyrfing.games.id17.networking;

import java.util.List;

import com.tyrfing.games.id17.EmpireFrameListener;
import com.tyrfing.games.id17.EmpireFrameListener.GameState;
import com.tyrfing.games.id17.houses.House;
import com.tyrfing.games.id17.houses.HouseController;
import com.tyrfing.games.id17.world.World;
import com.tyrlib2.graphics.materials.DefaultMaterial3;
import com.tyrlib2.graphics.renderables.Entity;
import com.tyrlib2.networking.Connection;
import com.tyrlib2.util.Color;

public class JoinGame extends NetworkMessage {
	/**
	 * 
	 */
	private static final long serialVersionUID = -4188394310832781662L;

	public final int houseID;
	public final int playerID;
	
	public JoinGame(int houseID, int playerID) {
		this.houseID = houseID;
		this.playerID = playerID;
	}
	
	@Override
	public void process(Connection c) {
		List<House> houses = World.getInstance().getHouses();
		
		
		if (EmpireFrameListener.MAIN_FRAME.getNetwork().isHost()) {
			EmpireFrameListener.MAIN_FRAME.getNetwork().broadcast(this);
		} 
		HouseController hc = World.getInstance().getPlayer(playerID);
		hc.control(World.getInstance().getHouses().get(houseID));
		hc.hasJoined = true;
		houses.get(houseID).setHouseController(hc);
		
		World.getInstance().unpause();
		EmpireFrameListener.state = GameState.MAIN;
		
		for (int i = 0; i < World.getInstance().getHoldings().size(); ++i) {
			Entity e = World.getInstance().getHoldings().get(i).holdingData.worldEntity;
			if (e.getSubEntity(0).getMaterial() instanceof DefaultMaterial3) {
				((DefaultMaterial3)e.getSubEntity(0).getMaterial()).setColor(Color.BLACK);
			}
		}
	}
}
