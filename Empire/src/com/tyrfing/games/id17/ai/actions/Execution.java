package com.tyrfing.games.id17.ai.actions;

import java.io.Serializable;

import com.tyrfing.games.id17.houses.House;

public class Execution implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = -7930176417902171345L;
	
	public final AIAction action;
	public final int[] options;
	public final House house;
	
	public Execution(AIAction action, House house, int[] options) {
		this.action = action;
		this.house = house;
		this.options = options;
	}
}
