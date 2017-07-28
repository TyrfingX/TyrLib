package com.tyrfing.games.id18.edit.faction;

import com.tyrfing.games.id18.model.unit.Faction;
import com.tyrfing.games.tyrlib3.edit.action.IActionProvider;

public abstract class AFactionActionProvider implements IActionProvider {
	private Faction faction;
	
	public AFactionActionProvider(Faction faction) {
		this.faction = faction;
	}
	
	public Faction getFaction() {
		return faction;
	}
}
