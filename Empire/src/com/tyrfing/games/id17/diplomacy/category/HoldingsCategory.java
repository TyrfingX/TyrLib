package com.tyrfing.games.id17.diplomacy.category;

import com.tyrfing.games.id17.ActionCategory;
import com.tyrfing.games.id17.diplomacy.Diplomacy;
import com.tyrfing.games.id17.diplomacy.actions.DiploAction;
import com.tyrfing.games.id17.diplomacy.actions.GrantHolding;
import com.tyrfing.games.id17.diplomacy.actions.RevokeHolding;
import com.tyrfing.games.id17.houses.House;

public class HoldingsCategory extends ActionCategory {

	public static final int GRANT = 0;
	public static final int REVOKE = 1;
	
	public HoldingsCategory() {
		super("Holdings");
		
		addAction(new GrantHolding());
		addAction(new RevokeHolding());
	}
	
	public void addAction(DiploAction action) {
		super.addAction(action);
		Diplomacy.actions.put(action.id, action);
	}

	@Override
	public boolean isEnabled(House sender, House receiver) {
		return receiver.isSubjectOf(sender);
	}

}
