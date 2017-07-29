package com.tyrfing.games.id18.edit.network;

import com.tyrfing.games.id18.edit.faction.AFactionActionProvider;
import com.tyrfing.games.id18.model.network.NetworkActionMessage;
import com.tyrfing.games.id18.model.network.NetworkMessage;
import com.tyrfing.games.id18.model.unit.Faction;
import com.tyrfing.games.tyrlib3.edit.action.IAction;
import com.tyrfing.games.tyrlib3.edit.action.IActionRequester;
import com.tyrfing.games.tyrlib3.model.networking.Connection;
import com.tyrfing.games.tyrlib3.model.networking.INetworkListener;

public class NetworkActionProvider extends AFactionActionProvider implements INetworkListener {

	private Connection connection;
	private IActionRequester lastActionRequester;
	private ActionSerializer actionSerializer;
	
	public NetworkActionProvider(ActionSerializer actionSerializer, Faction faction, Connection connection) {
		super(faction);
		
		this.connection = connection;
		this.actionSerializer = actionSerializer;
		connection.getNetwork().addListener(this);
	}

	public Connection getConnection() {
		return connection;
	}
	
	@Override
	public void requestAction(IActionRequester actionRequester) {
		this.lastActionRequester = actionRequester;
		connection.send(NetworkMessage.IConstantMessages.REQUEST_ACTION);
	}

	@Override
	public void onNewConnection(Connection c) {
	}

	@Override
	public void onConnectionLost(Connection c) {
	}

	@Override
	public void onReceivedData(Connection c, Object o) {
		if (o instanceof NetworkActionMessage) {
			NetworkActionMessage actionMessage = (NetworkActionMessage) o;
			IAction action = actionSerializer.toAction(actionMessage);
			lastActionRequester.onProvideRequest(action);
		}
	}
	
}
