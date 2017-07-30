package com.tyrfing.games.id17.diplomacy.category;

import com.tyrfing.games.id17.ActionCategory;
import com.tyrfing.games.id17.diplomacy.Diplomacy;
import com.tyrfing.games.id17.diplomacy.actions.DiploAction;
import com.tyrfing.games.id17.diplomacy.actions.RequestLoan;
import com.tyrfing.games.id17.houses.House;

public class LoansCategory extends ActionCategory {

	public LoansCategory() {
		super("Loans");
		
		addAction(new RequestLoan());
	}

	public void addAction(DiploAction action) {
		super.addAction(action);
		Diplomacy.actions.put(action.id, action);
	}
	
	@Override
	public boolean isEnabled(House sender, House receiver) {
		return sender.isEnemy(receiver) == null;
	}

}
