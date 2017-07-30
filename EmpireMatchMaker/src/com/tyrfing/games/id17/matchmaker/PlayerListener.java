package com.tyrfing.games.id17.matchmaker;

import com.tyrlib2.networking.Connection;
import com.tyrlib2.networking.INetworkListener;

public class PlayerListener implements INetworkListener {

	private MatchMaker mm;
	
	public PlayerListener(MatchMaker mm) {
		this.mm = mm;
	}
	
	@Override
	public void onNewConnection(Connection c) {
		c.openToBroadcasts = true;
		mm.onPlayerConnected(c);
	}

	@Override
	public void onConnectionLost(Connection c) {
		mm.onPlayerDisconnected(c);
	}

	@Override
	public void onReceivedData(Connection c, Object o) {
		
	}

}
