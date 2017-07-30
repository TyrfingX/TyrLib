package com.tyrfing.games.id17.intrigue.category;

import com.tyrfing.games.id17.ActionCategory;
import com.tyrfing.games.id17.houses.House;
import com.tyrfing.games.id17.intrigue.Intrigue;
import com.tyrfing.games.id17.intrigue.actions.CreateIncident;
import com.tyrfing.games.id17.intrigue.actions.Destabilize;
import com.tyrfing.games.id17.intrigue.actions.IntrigueAction;
import com.tyrfing.games.id17.intrigue.actions.Maraude;

public class Sabotage extends ActionCategory {

	public Sabotage() {
		super("Sabotage");
	
		addAction(new CreateIncident());
		addAction(new Destabilize());
		addAction(new Maraude());
	}
	
	public void addAction(IntrigueAction a) {
		actions.add(a);
		Intrigue.actions.add(a);
	}
	
	@Override
	public boolean isEnabled(House sender, House receiver) {
		return sender.isRealmNeighbour(receiver);
	}

}
