package com.tyrfing.games.id17;

import java.util.ArrayList;
import java.util.List;

import com.tyrfing.games.id17.houses.House;

public abstract class ActionCategory {
	protected List<Action> actions = new ArrayList<Action>();
	private String name;
	
	public ActionCategory(String name) {
		this.name = name;
	}
	
	public String getName() {
		return name;
	}
	
	public int getCountActions() {
		return actions.size();
	}
	
	public Action getAction(int i) {
		return actions.get(i);
	}
	
	public void addAction(Action action) {
		actions.add(action);
	}
	
	public abstract boolean isEnabled(House sender, House receiver);
}
