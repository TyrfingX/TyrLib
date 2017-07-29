package com.tyrfing.games.id18.model.unit;

import java.util.ArrayList;
import java.util.List;

public class Faction {
	private int moral;
	private Faction superFaction;
	private List<Faction> subFactions;
	
	public Faction() {
		subFactions = new ArrayList<Faction>();
	}
	
	public int getMoral() {
		return moral;
	}
	
	public void setMoral(int moral) {
		this.moral = moral;
	}
	
	public Faction getSuperFaction() {
		return superFaction;
	}
	public void setSuperFaction(Faction superFaction) {
		this.superFaction = superFaction;
		superFaction.getSubFactions().add(this);
	}
	
	public List<Faction> getSubFactions() {
		return subFactions;
	}
	
	public List<Faction> getAllSuperFactionsInclusive() {
		List<Faction> allSuperFactions = new ArrayList<Faction>();
		Faction superFaction = this;
		while (superFaction != null) {
			allSuperFactions.add(superFaction);
			superFaction = this.getSuperFaction();
		}
		return allSuperFactions;
	}
}
