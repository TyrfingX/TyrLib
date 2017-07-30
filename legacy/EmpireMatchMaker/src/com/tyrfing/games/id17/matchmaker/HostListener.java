package com.tyrfing.games.id17.matchmaker;

import com.tyrfing.games.id17.startmenu.HostedGameUpdate;
import com.tyrlib2.networking.Connection;
import com.tyrlib2.networking.INetworkListener;

public class HostListener implements INetworkListener {

	private MatchMaker mm;
	
	public HostListener(MatchMaker mm) {
		this.mm = mm;
	}
	
	@Override
	public void onNewConnection(Connection c) {
		HostedGame game = new HostedGame();
		game.connection = c;
		c.openToBroadcasts = true;
		mm.onGameOpened(game);
	}

	@Override
	public void onConnectionLost(Connection c) {
		mm.onGameClosed(mm.getGame(c));
	}

	@Override
	public void onReceivedData(Connection c, Object o) {
		if (o instanceof HostedGameUpdate) {
			HostedGameUpdate u = (HostedGameUpdate) o;
			mm.updatePlayers(mm.getGame(c), u.players, u.maxPlayers, u.port);
		}
	}

}
