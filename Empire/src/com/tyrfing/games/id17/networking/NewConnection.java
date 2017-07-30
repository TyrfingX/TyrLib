package com.tyrfing.games.id17.networking;

import com.tyrfing.games.id17.EmpireFrameListener;
import com.tyrfing.games.id17.world.World;
import com.tyrlib2.networking.Connection;

public class NewConnection extends NetworkMessage {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1447879630793233865L;

	public final short houseID;
	public final byte playerID;
	public final boolean randomJoin;
	
	public NewConnection(int houseID, int playerID) {
		this.houseID = (short) houseID;
		this.playerID = (byte) playerID;
		this.randomJoin = EmpireFrameListener.MAIN_FRAME.randomJoin;
	}
	
	@Override
	public void process(Connection c) {
		EmpireFrameListener.MAIN_FRAME.randomJoin = randomJoin;
		EmpireFrameListener.MAIN_FRAME.startGame(playerID, houseID);
		World.getInstance().getMainGUI().display();
		EmpireFrameListener.MAIN_FRAME.camController.focus(World.getInstance().getPlayerController().getHouse().getHoldings().get(0).holdingData.worldEntity.getParent());
		
		if (randomJoin) {
			World.getInstance().players.add(World.getInstance().getPlayerController());
			EmpireFrameListener.MAIN_FRAME.joinGame();
		}
	}
}
