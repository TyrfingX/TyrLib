package com.tyrfing.games.id18.edit.network;

import com.tyrfing.games.id18.edit.battle.BattleDomain;
import com.tyrfing.games.id18.edit.faction.AFactionActionProvider;
import com.tyrfing.games.id18.model.battle.Battle;
import com.tyrfing.games.id18.model.network.NetworkMessage;
import com.tyrfing.games.tyrlib3.edit.action.IAction;
import com.tyrfing.games.tyrlib3.edit.action.IActionRequester;
import com.tyrfing.games.tyrlib3.networking.Connection;
import com.tyrfing.games.tyrlib3.networking.INetworkListener;
import com.tyrfing.games.tyrlib3.networking.Network;

public class Client implements INetworkListener, IActionRequester {

	private BattleDomain battleDomain;
	private Network network;
	private Connection connection;
	private ActionSerializer actionSerializer;
	
	public Client() {
		network = new Network();
		network.addListener(this);
	}
	
	public Network getNetwork() {
		return network;
	}
	
	public BattleDomain getBattleDomain() {
		return battleDomain;
	}
	
	public AFactionActionProvider getActionProvider() {
		return battleDomain.getFactionActionProviders().get(0);
	}
	
	public void setActionProvider(AFactionActionProvider actionProvider) {
		battleDomain.getFactionActionProviders().clear();
		battleDomain.getFactionActionProviders().add(actionProvider);
	}
	
	@Override
	public void onNewConnection(Connection c) {
		this.connection = c;
	}

	@Override
	public void onConnectionLost(Connection c) {
		this.connection = null;
	}

	@Override
	public void onReceivedData(Connection c, Object o) {
		if (o instanceof Battle) {
			Battle battle = (Battle) o;
			battleDomain = new BattleDomain(battle);
			actionSerializer = new ActionSerializer(battleDomain.getBattle());
		} else if (o instanceof IAction) {
			IAction action = (IAction) o;
			battleDomain.getActionStack().execute(action);
		} else if (o instanceof NetworkMessage) {
			NetworkMessage networkMessage = (NetworkMessage) o;
			if (networkMessage.getMessage() == NetworkMessage.MESSAGE_REQUEST_ACTION) {
				getActionProvider().requestAction(this);
			}
		}
	}

	@Override
	public void onProvideRequest(IAction action) {
		NetworkMessage networkMessage = actionSerializer.toNetworkMessage(action);
		network.send(networkMessage, connection);
	}
	
}
