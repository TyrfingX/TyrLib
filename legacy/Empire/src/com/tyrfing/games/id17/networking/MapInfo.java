package com.tyrfing.games.id17.networking;

import java.io.Serializable;

public class MapInfo implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 8261437018480073147L;
	
	public String name;
	
	public MapInfo(String name) {
		this.name = name;
	}
}
