package com.tyrfing.games.tyrlib3.test.network;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.io.IOException;
import java.net.UnknownHostException;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import com.tyrfing.games.tyrlib3.networking.Connection;
import com.tyrfing.games.tyrlib3.networking.INetworkListener;
import com.tyrfing.games.tyrlib3.networking.Network;

public class NetworkTest {
	
	public static final int PORT = 666;
	
	private Network hostNetwork;
	
	private class TestNetworkListemer implements INetworkListener {
		
		protected Connection receivedConnection;
		protected boolean lostConnection;
		protected String receivedData;
		
		@Override
		public void onReceivedData(Connection c, Object o) {
			receivedData = (String) o;
		}
		
		@Override
		public void onNewConnection(Connection c) {
			receivedConnection = c;
		}
		
		@Override
		public void onConnectionLost(Connection c) {
			lostConnection = true;
		}
	};
	
	@Before
	public void setup() {
		hostNetwork = new Network();
		hostNetwork.setLog(true);
		hostNetwork.host(PORT);
	}
	
	@After
	public void tearDown() {
		hostNetwork.close();
		assertFalse("Host Network is no longer a host network", hostNetwork.isHost());
	}
	
	@Test
	public void testHost() {
		assertTrue("Host Network is now a host network", hostNetwork.isHost());
		assertFalse("Host Network is not a client network", hostNetwork.isClient());
		assertEquals("Server now has our passed port", PORT, hostNetwork.getServer().getPort());
	}
	
	@Test
	public void testConnect() throws UnknownHostException, IOException, InterruptedException {
		TestNetworkListemer listener = new TestNetworkListemer();
		hostNetwork.addListener(listener);
		
		Network clientNetwork = new Network();
		clientNetwork.setLog(true);
		clientNetwork.addListener(listener);
		
		String serverName = hostNetwork.getServer().getServerName();
		clientNetwork.connectTo(serverName, PORT);
		
		Thread.sleep(1000);
		
		assertNotNull("1 Client is now connected", listener.receivedConnection);
		assertEquals("1 Client is now connected", 1 , hostNetwork.getClientCount());
		
		final String TEST_DATA = "test";
		clientNetwork.send(TEST_DATA, listener.receivedConnection);
		
		Thread.sleep(1000);
		
		assertEquals("Received correct data", TEST_DATA , listener.receivedData);
		
		clientNetwork.close();
		
		Thread.sleep(1000);
		
		assertTrue("1 Client is now connected", listener.lostConnection);
		assertEquals("Client is now disconnected", 0 , hostNetwork.getClientCount());
	}
	
}
