package com.tyrfing.games.id18.test.edit.network;

import static org.junit.Assert.assertTrue;

import java.io.IOException;
import java.net.UnknownHostException;

import org.junit.Test;

import com.tyrfing.games.id18.edit.battle.action.EndTurnAction;
import com.tyrfing.games.id18.edit.network.ActionSerializer;
import com.tyrfing.games.id18.edit.network.NetworkActionProvider;
import com.tyrfing.games.id18.model.network.NetworkActionMessage;
import com.tyrfing.games.id18.model.unit.Faction;
import com.tyrfing.games.tyrlib3.edit.action.IAction;
import com.tyrfing.games.tyrlib3.edit.action.IActionRequester;
import com.tyrfing.games.tyrlib3.model.networking.Connection;
import com.tyrfing.games.tyrlib3.model.networking.INetworkListener;
import com.tyrfing.games.tyrlib3.model.networking.Network;

public class NetworkActionProviderTest {

	public static final int SLEEP_TIME = 500;

	private class TestActionRequester implements IActionRequester {

		protected IAction requestedAction;
		
		@Override
		public void onProvideRequest(IAction action) {
			this.requestedAction = action;
		}
	}
	
	@Test
	public void testRequestAction() throws UnknownHostException, IOException, InterruptedException {
		
		final int PORT = 666;
		
		Faction faction = new Faction();
		Network hostNetwork = new Network();
		Network clientNetwork = new Network();
		hostNetwork.host(PORT);
		
		INetworkListener clientListener = new INetworkListener() {
			@Override
			public void onReceivedData(Connection c, Object o) {
				c.send(NetworkActionMessage.IConstantMessages.END_TURN_ACTION);
			}
			
			@Override
			public void onNewConnection(Connection c) {
			}
			
			@Override
			public void onConnectionLost(Connection c) {
			}
		};
		
		clientNetwork.addListener(clientListener);
		clientNetwork.connectTo(hostNetwork.getServer().getServerName(), PORT);
		
		Thread.sleep(SLEEP_TIME);
		
		Connection connection = hostNetwork.getConnection(0);
		
		NetworkActionProvider networkActionProvider = new NetworkActionProvider(new ActionSerializer(null), faction, connection);
		TestActionRequester requester = new TestActionRequester();
		networkActionProvider.requestAction(requester);
		
		Thread.sleep(SLEEP_TIME);
		
		assertTrue("Received requested action", requester.requestedAction instanceof EndTurnAction);
		
		hostNetwork.close();
		clientNetwork.close();
		
	}

}
