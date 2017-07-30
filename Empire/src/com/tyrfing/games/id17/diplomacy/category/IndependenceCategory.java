package com.tyrfing.games.id17.diplomacy.category;

import com.tyrfing.games.id17.ActionCategory;
import com.tyrfing.games.id17.diplomacy.Diplomacy;
import com.tyrfing.games.id17.diplomacy.actions.DeclareIndependence;
import com.tyrfing.games.id17.diplomacy.actions.DiploAction;
import com.tyrfing.games.id17.houses.House;

public class IndependenceCategory extends ActionCategory {

	public static final int DECLARE = 0;
	
	public IndependenceCategory() {
		super("Independence");
		
		addAction(new DeclareIndependence());
	}

	public void addAction(DiploAction action) {
		super.addAction(action);
		Diplomacy.actions.put(action.id, action);
	}
	
	@Override
	public boolean isEnabled(House sender, House receiver) {
		return (sender.getOverlord() == receiver || sender.getSupremeOverlord() == receiver) && sender.getBaronies().size() > 0;
	}

}
