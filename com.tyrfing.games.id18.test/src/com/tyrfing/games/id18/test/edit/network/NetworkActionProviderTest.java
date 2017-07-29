package com.tyrfing.games.id18.test.edit.network;

import static org.junit.Assert.assertEquals;

import java.io.IOException;
import java.net.UnknownHostException;

import org.junit.Test;

import com.tyrfing.games.id18.edit.network.NetworkActionProvider;
import com.tyrfing.games.id18.model.unit.Faction;
import com.tyrfing.games.tyrlib3.edit.action.IAction;
import com.tyrfing.games.tyrlib3.edit.action.IActionRequester;
import com.tyrfing.games.tyrlib3.networking.Connection;
import com.tyrfing.games.tyrlib3.networking.INetworkListener;
import com.tyrfing.games.tyrlib3.networking.Network;

public class NetworkActionProviderTest {

	private class TestActionRequester implements IActionRequester {

		protected TestAction requestedAction;
		
		@Override
		public void onProvideRequest(IAction action) {
			this.requestedAction = (TestAction) action;
		}
	}
	
	@Test
	public void testRequestAction() throws UnknownHostException, IOException, InterruptedException {
		
		final int PORT = 666;
		final int TEST_VALUE = 5;
		
		Faction faction = new Faction();
		Network hostNetwork = new Network();
		Network clientNetwork = new Network();
		hostNetwork.host(PORT);
		
		TestAction testAction = new TestAction();
		testAction.testInt = TEST_VALUE;
		INetworkListener clientListener = new INetworkListener() {
			@Override
			public void onReceivedData(Connection c, Object o) {
				c.send(testAction);
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
		
		Thread.sleep(1000);
		
		Connection connection = hostNetwork.getConnection(0);
		
		NetworkActionProvider networkActionProvider = new NetworkActionProvider(faction, connection);
		TestActionRequester requester = new TestActionRequester();
		networkActionProvider.requestAction(requester);
		
		Thread.sleep(1000);
		
		assertEquals("Received requested action", TEST_VALUE, requester.requestedAction.testInt);
		
		hostNetwork.close();
		clientNetwork.close();
		
	}

}
