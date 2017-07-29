package com.tyrfing.games.id18.model.unit;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class Faction implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 770478376872198589L;
	private int moral;
	private List<Faction> superFactions;
	
	public Faction() {
		superFactions = new ArrayList<Faction>();
		superFactions.add(this);
	}
	
	public int getMoral() {
		return moral;
	}
	
	public void setMoral(int moral) {
		this.moral = moral;
	}
	
	public List<Faction> getSuperFactions() {
		return superFactions;
	}
}
