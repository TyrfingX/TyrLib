package com.tyrfing.games.id17.networking;

import com.tyrfing.games.id17.ChatListener;
import com.tyrfing.games.id17.EmpireFrameListener;
import com.tyrfing.games.id17.diplomacy.Message;
import com.tyrfing.games.id17.holdings.Holding;
import com.tyrfing.games.id17.houses.HouseController;
import com.tyrlib2.networking.Connection;
import com.tyrlib2.networking.Network;
import com.tyrlib2.util.Color;

public class NetworkController extends HouseController {

	/**
	 * 
	 */
	private static final long serialVersionUID = -8083960709990515770L;
	
	private Network network;
	
	public byte displayRequest = RequestDisplayData.NONE;
	public short displayParam;
	
	private Connection connection;
	
	public NetworkController(Connection connection) {
		network = EmpireFrameListener.MAIN_FRAME.getNetwork();
		this.connection = connection;
	}

	@Override
	public void informMessage(Message message) {
		network.send(new NetworkAction(message), connection);
	}

	@Override
	public void informNewHolding(Holding holding) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void informLostHolding(Holding holding) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void destroy() {
		// TODO Auto-generated method stub
		
	}
	
	public Connection getConnection() {
		return connection;
	}
	
	@Override
	public Color getStrategicColor() {
		return ChatListener.chatColors[playerID];
	}

}
