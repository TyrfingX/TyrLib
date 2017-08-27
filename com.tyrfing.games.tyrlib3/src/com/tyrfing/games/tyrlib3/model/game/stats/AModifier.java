package com.tyrfing.games.tyrlib3.model.game.stats;

import java.io.Serializable;

public abstract class AModifier implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 5836893121592936614L;
	private String name;
	
	public AModifier(String name) {
		this.name = name;
	}
	
	public String getName() {
		return name;
	}
}
