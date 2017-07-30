package com.tyrfing.games.id17.networking;

import com.tyrfing.games.id17.EmpireFrameListener;
import com.tyrfing.games.id17.EmpireFrameListener.GameState;
import com.tyrfing.games.id17.gui.PlayerController;
import com.tyrfing.games.id17.world.World;
import com.tyrlib2.graphics.scene.SceneManager;
import com.tyrlib2.networking.Connection;

public class NewPlayer extends NetworkMessage {
	
	
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 2771967678553426511L;

	public final int houseID;
	public final int playerID;
	public final boolean hasJoined;
	
	public NewPlayer(int houseID,  int playerID, boolean hasJoined) {
		this.houseID = houseID;
		this.playerID = playerID;
		this.hasJoined = hasJoined;
	}
	
	@Override
	public void process(Connection c) {
		PlayerController playerController = World.getInstance().getPlayerController();
		
		if (	!SceneManager.getInstance().getRenderer().isInServerMode() 
				&& 	houseID == playerController.getHouse().id) {
				playerController.playerID = playerID;
				World.getInstance().players.add(World.getInstance().getPlayerController());
			} else {
				NetworkController nc = new NetworkController(c);
				nc.control(World.getInstance().getHouses().get(houseID));
				nc.playerID = playerID;
				World.getInstance().getHouses().get(houseID).setHouseController(nc);
				World.getInstance().players.add(nc);
				
				if (EmpireFrameListener.state == GameState.SELECT) {
					nc.markControlledHouse();
				}
			}
	}
}
