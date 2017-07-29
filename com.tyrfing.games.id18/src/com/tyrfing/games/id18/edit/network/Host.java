package com.tyrfing.games.id18.edit.network;

import com.tyrfing.games.tyrlib3.networking.Connection;
import com.tyrfing.games.tyrlib3.networking.INetworkListener;
import com.tyrfing.games.tyrlib3.networking.Network;

public class Host implements INetworkListener {
	private Network network;
	
	public Host(int port) {
		network = new Network();
		network.addListener(this);
		network.host(port);
	}
	
	public Network getNetwork() {
		return network;
	}

	@Override
	public void onNewConnection(Connection c) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onConnectionLost(Connection c) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onReceivedData(Connection c, Object o) {
		// TODO Auto-generated method stub
		
	}
}
