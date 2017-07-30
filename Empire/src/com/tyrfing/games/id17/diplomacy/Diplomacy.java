package com.tyrfing.games.id17.diplomacy;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.tyrfing.games.id17.ActionCategory;
import com.tyrfing.games.id17.diplomacy.actions.DiploAction;
import com.tyrfing.games.id17.diplomacy.category.HoldingsCategory;
import com.tyrfing.games.id17.diplomacy.category.IndependenceCategory;
import com.tyrfing.games.id17.diplomacy.category.LoansCategory;
import com.tyrfing.games.id17.diplomacy.category.OtherCategory;
import com.tyrfing.games.id17.diplomacy.category.PactsCategory;
import com.tyrfing.games.id17.diplomacy.category.PeaceCategory;
import com.tyrfing.games.id17.diplomacy.category.RelationsCategory;
import com.tyrfing.games.id17.diplomacy.category.RequestCategory;

public class Diplomacy {
	
	public static final int RELATIONS_ID = 0;
	public static final int REACE_ID = 1;
	public static final int PACTS_ID = 2;
	public static final int HOLDINGS_ID = 3;
	public static final int LOANS_ID = 4;
	public static final int REQUEST_ID = 5;
	public static final int INDEPENDENCE_ID = 6;
	public static final int OTHER = 7;
	
	private static Diplomacy instance;
	public final List<ActionCategory> categories = new ArrayList<ActionCategory>();
	public final static Map<Integer, DiploAction> actions = new HashMap<Integer, DiploAction>();
	
	public Diplomacy() {
		categories.add(new RelationsCategory());
		categories.add(new PeaceCategory());
		categories.add(new PactsCategory());
		categories.add(new HoldingsCategory());
		categories.add(new LoansCategory());
		categories.add(new RequestCategory());
		categories.add(new IndependenceCategory());
		categories.add(new OtherCategory());
	}
	
	public static Diplomacy getInstance() {
		if (instance == null) {
			instance = new Diplomacy();
		}
		return instance;
	}
	
	public ActionCategory getCategory(int index) {
		return categories.get(index);
	}
	
	public int getCountCategories() {
		return categories.size();
	}
	
	public int getCategoryIndex(ActionCategory category) {
		return categories.indexOf(category);
	}
	
	public DiploAction getAction(int categoryID, int actionID) {
		return (DiploAction) categories.get(categoryID).getAction(actionID);
	}
}
