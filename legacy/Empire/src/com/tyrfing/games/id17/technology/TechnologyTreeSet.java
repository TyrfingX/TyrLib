package com.tyrfing.games.id17.technology;

import java.io.Serializable;

public class TechnologyTreeSet implements Serializable {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = -9003550130794240628L;
	public TechnologyTree[] trees;
	
	public TechnologyTreeSet() {
		trees = new TechnologyTree[] {
				new XMLTechnologyTreeFactory("technology/earlymedieval.xml").create()
		};
	}
	
	public void update() {
		new XMLTechnologyTreeUpdater("technology/earlymedieval.xml").update(trees[0]);
	}
}
