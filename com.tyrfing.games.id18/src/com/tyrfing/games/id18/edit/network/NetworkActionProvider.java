package com.tyrfing.games.id18.edit.network;

import com.tyrfing.games.id18.edit.faction.AFactionActionProvider;
import com.tyrfing.games.id18.model.unit.Faction;
import com.tyrfing.games.tyrlib3.edit.action.IActionRequester;
import com.tyrfing.games.tyrlib3.networking.Network;

public class NetworkActionProvider extends AFactionActionProvider {

	public NetworkActionProvider(Faction faction, Network network) {
		super(faction);
	}

	@Override
	public void requestAction(IActionRequester actionRequester) {
		
	}
	
}
