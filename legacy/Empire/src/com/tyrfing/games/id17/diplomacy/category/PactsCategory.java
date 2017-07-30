package com.tyrfing.games.id17.diplomacy.category;

import com.tyrfing.games.id17.ActionCategory;
import com.tyrfing.games.id17.diplomacy.Diplomacy;
import com.tyrfing.games.id17.diplomacy.actions.DefensivePact;
import com.tyrfing.games.id17.diplomacy.actions.DiploAction;
import com.tyrfing.games.id17.diplomacy.actions.Protect;
import com.tyrfing.games.id17.diplomacy.actions.SendDiplomat;
import com.tyrfing.games.id17.diplomacy.actions.TradeAgreement;
import com.tyrfing.games.id17.diplomacy.actions.Vassalize;
import com.tyrfing.games.id17.houses.House;

public class PactsCategory extends ActionCategory {

	public static final int SEND_DIPLOMAT = 0;
	public static final int TRADE_AGREEMENT = 1;
	public static final int PROTECT = 2;
	public static final int DEFENSIVE_PACT = 3;
	public static final int VASSALIZE = 4;
	
	public PactsCategory() {
		super("Pacts");

		addAction(new SendDiplomat());
		addAction(new TradeAgreement());
		addAction(new Protect());
		addAction(new DefensivePact());
		addAction(new Vassalize());
	}
	
	public void addAction(DiploAction action) {
		super.addAction(action);
		Diplomacy.actions.put(action.id, action);
	}

	@Override
	public boolean isEnabled(House sender, House receiver) {
		return receiver.isIndependend() && sender.isIndependend() && sender.isRealmNeighbour(receiver);
	}

}
