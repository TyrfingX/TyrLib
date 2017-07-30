package com.tyrfing.games.id17.diplomacy.category;

import com.tyrfing.games.id17.ActionCategory;
import com.tyrfing.games.id17.diplomacy.Diplomacy;
import com.tyrfing.games.id17.diplomacy.actions.DeclareWar;
import com.tyrfing.games.id17.diplomacy.actions.DiploAction;
import com.tyrfing.games.id17.diplomacy.actions.Marriage;
import com.tyrfing.games.id17.diplomacy.actions.Rivalize;
import com.tyrfing.games.id17.diplomacy.actions.SendGift;
import com.tyrfing.games.id17.houses.House;

public class RelationsCategory extends ActionCategory {

	public static final int DECLARE_WAR = 0;
	public static final int MARRIAGE = 1;
	public static final int SEND_GIFT = 2;
	public static final int RIVALIZE = 2;
	
	public RelationsCategory() {
		super("Relations");
		
		addAction(new DeclareWar());
		addAction(new Marriage());
		addAction(new SendGift());
		addAction(new Rivalize());
	}
	
	public void addAction(DiploAction action) {
		super.addAction(action);
		Diplomacy.actions.put(action.id, action);
	}

	@Override
	public boolean isEnabled(House sender, House receiver) {
		return (receiver.isEnemy(sender) == null);
	}

}
