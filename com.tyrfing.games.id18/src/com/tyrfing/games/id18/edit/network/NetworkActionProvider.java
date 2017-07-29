package com.tyrfing.games.id18.edit.network;

import com.tyrfing.games.id18.edit.faction.AFactionActionProvider;
import com.tyrfing.games.id18.model.unit.Faction;
import com.tyrfing.games.tyrlib3.edit.action.IAction;
import com.tyrfing.games.tyrlib3.edit.action.IActionRequester;
import com.tyrfing.games.tyrlib3.networking.Connection;
import com.tyrfing.games.tyrlib3.networking.INetworkListener;

public class NetworkActionProvider extends AFactionActionProvider implements INetworkListener {

	private Connection connection;
	private IActionRequester lastActionRequester;
	
	public NetworkActionProvider(Faction faction, Connection connection) {
		super(faction);
		
		this.connection = connection;
		connection.getNetwork().addListener(this);
	}

	@Override
	public void requestAction(IActionRequester actionRequester) {
		this.lastActionRequester = actionRequester;
		connection.send(NetworkMessage.REQUEST_ACTION);
	}

	@Override
	public void onNewConnection(Connection c) {
	}

	@Override
	public void onConnectionLost(Connection c) {
	}

	@Override
	public void onReceivedData(Connection c, Object o) {
		if (o instanceof IAction) {
			IAction action = (IAction) o;
			lastActionRequester.onProvideRequest(action);
		}
	}
	
}
