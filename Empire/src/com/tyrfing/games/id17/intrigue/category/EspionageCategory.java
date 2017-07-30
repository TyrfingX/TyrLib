package com.tyrfing.games.id17.intrigue.category;

import com.tyrfing.games.id17.ActionCategory;
import com.tyrfing.games.id17.houses.House;
import com.tyrfing.games.id17.intrigue.Intrigue;
import com.tyrfing.games.id17.intrigue.actions.Assassinate;
import com.tyrfing.games.id17.intrigue.actions.Infiltrate;
import com.tyrfing.games.id17.intrigue.actions.IntrigueAction;

public class EspionageCategory extends ActionCategory {

	public static final int ESPIONAGE_ACTION_ID = 0;
	
	public EspionageCategory() {
		super("Espionage");
		
		addAction(new Infiltrate());
		addAction(new Assassinate());
	}
	
	public void addAction(IntrigueAction a) {
		actions.add(a);
		Intrigue.actions.add(a);
	}

	@Override
	public boolean isEnabled(House sender, House receiver) {
		return true;
	}

}
