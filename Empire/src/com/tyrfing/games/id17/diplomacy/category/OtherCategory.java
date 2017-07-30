package com.tyrfing.games.id17.diplomacy.category;

import com.tyrfing.games.id17.ActionCategory;
import com.tyrfing.games.id17.diplomacy.Diplomacy;
import com.tyrfing.games.id17.diplomacy.actions.DiploAction;
import com.tyrfing.games.id17.diplomacy.actions.HandleRevolt;
import com.tyrfing.games.id17.diplomacy.actions.HonorDefensivePact;
import com.tyrfing.games.id17.diplomacy.actions.InviteToIntrigue;
import com.tyrfing.games.id17.diplomacy.actions.PayLoan;
import com.tyrfing.games.id17.diplomacy.actions.RevealPlot;
import com.tyrfing.games.id17.diplomacy.actions.SupportRevolt;
import com.tyrfing.games.id17.houses.House;

public class OtherCategory extends ActionCategory {
	
	public static final int INVITE_TO_INTRIGUE_ID = 0;
	
	public OtherCategory() {
		super("Other");
		
		addAction(new InviteToIntrigue());
		addAction(new HonorDefensivePact());
		addAction(new PayLoan());
		addAction(new HandleRevolt(0));
		addAction(new SupportRevolt(null));
		addAction(new RevealPlot());
	}

	public void addAction(DiploAction action) {
		super.addAction(action);
		Diplomacy.actions.put(action.id, action);
	}
	
	@Override
	public boolean isEnabled(House sender, House receiver) {
		return false;
	}

}
