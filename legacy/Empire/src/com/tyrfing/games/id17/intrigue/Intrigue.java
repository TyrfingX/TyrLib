package com.tyrfing.games.id17.intrigue;

import java.util.ArrayList;
import java.util.List;

import com.tyrfing.games.id17.ActionCategory;
import com.tyrfing.games.id17.intrigue.actions.IntrigueAction;
import com.tyrfing.games.id17.intrigue.category.EspionageCategory;
import com.tyrfing.games.id17.intrigue.category.HoldingsCategory;
import com.tyrfing.games.id17.intrigue.category.Sabotage;

public class Intrigue {
	public final List<ActionCategory> categories = new ArrayList<ActionCategory>();
	public static List<IntrigueAction> actions = new ArrayList<IntrigueAction>();
	
	private static Intrigue instance;
	
	public static final int ESPIONAGE_CATRGORY_ID = 0;
	public static final int HOLDINGS_CATRGORY_ID = 1;
	public static final int REVOLTS_CATRGORY_ID = 2;
	
	public Intrigue() {
		categories.add(new EspionageCategory());
		categories.add(new HoldingsCategory());
		categories.add(new Sabotage());
	}
	
	public static Intrigue getInstance() {
		if (instance == null) {
			instance = new Intrigue();
		} 
		
		return instance;
	}

	public List<IntrigueAction> getActions() {
		return actions;
	}
}
