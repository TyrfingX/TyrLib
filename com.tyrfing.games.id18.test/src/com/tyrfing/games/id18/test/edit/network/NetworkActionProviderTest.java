package com.tyrfing.games.id18.test.edit.network;

import org.junit.Test;

import com.tyrfing.games.id18.edit.network.NetworkActionProvider;
import com.tyrfing.games.id18.model.unit.Faction;
import com.tyrfing.games.tyrlib3.networking.Network;

public class NetworkActionProviderTest {

	@Test
	public void testRequestAction() {
		
		final int PORT = 666;
		Faction faction = new Faction();
		Network network = new Network();
		
		network.host(PORT);
		
		NetworkActionProvider networkActionProvider = new NetworkActionProvider(faction, network);
		
	}

}
