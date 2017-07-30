package com.tyrfing.games.id17.intrigue.category;

import com.tyrfing.games.id17.ActionCategory;
import com.tyrfing.games.id17.houses.House;
import com.tyrfing.games.id17.intrigue.Intrigue;
import com.tyrfing.games.id17.intrigue.actions.FabricateClaim;
import com.tyrfing.games.id17.intrigue.actions.IntrigueAction;

public class HoldingsCategory extends ActionCategory {

	public HoldingsCategory() {
		super("Holdings");
		
		addAction(new FabricateClaim());
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
