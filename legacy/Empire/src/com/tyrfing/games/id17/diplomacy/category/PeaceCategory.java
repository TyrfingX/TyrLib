package com.tyrfing.games.id17.diplomacy.category;

import com.tyrfing.games.id17.ActionCategory;
import com.tyrfing.games.id17.diplomacy.Diplomacy;
import com.tyrfing.games.id17.diplomacy.actions.AdmitDefeat;
import com.tyrfing.games.id17.diplomacy.actions.DictateDemands;
import com.tyrfing.games.id17.diplomacy.actions.DiploAction;
import com.tyrfing.games.id17.diplomacy.actions.WhitePeace;
import com.tyrfing.games.id17.houses.House;

public class PeaceCategory extends ActionCategory {

	public static final int DICTATE_ID = 0;
	public static final int WHITE_ID = 1;
	public static final int ADMIT_ID = 2;
	
	public PeaceCategory() {
		super("Peace");
		
		addAction(new DictateDemands());
		addAction(new WhitePeace());
		addAction(new AdmitDefeat());
	}
	
	public void addAction(DiploAction action) {
		super.addAction(action);
		Diplomacy.actions.put(action.id, action);
	}

	@Override
	public boolean isEnabled(House sender, House receiver) {
		return (receiver.isEnemy(sender) != null);
	}

}
