package com.tyrfing.games.id17.diplomacy.category;

import com.tyrfing.games.id17.ActionCategory;
import com.tyrfing.games.id17.diplomacy.Diplomacy;
import com.tyrfing.games.id17.diplomacy.actions.DiploAction;
import com.tyrfing.games.id17.diplomacy.actions.RequestClaim;
import com.tyrfing.games.id17.diplomacy.actions.RequestResearchFunds;
import com.tyrfing.games.id17.houses.House;

public class RequestCategory extends ActionCategory {

	public final static int CLAIM = 0;
	public final static int RESEARCH = 1;
	
	public RequestCategory() {
		super("Request");
		
		addAction(new RequestClaim());
		addAction(new RequestResearchFunds());
	}
	
	public void addAction(DiploAction action) {
		super.addAction(action);
		Diplomacy.actions.put(action.id, action);
	}
	
	@Override
	public boolean isEnabled(House sender, House receiver) {
		return sender.isSubjectOf(receiver);
	}

}
