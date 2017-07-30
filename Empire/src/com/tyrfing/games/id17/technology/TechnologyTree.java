package com.tyrfing.games.id17.technology;

import java.io.Serializable;

public class TechnologyTree implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 103107723382904741L;
	public final String name;
	public final Technology[] techs;
	
	public TechnologyTree(String name, Technology[] techs) {
		this.name = name;
		this.techs = techs;
	}
}
