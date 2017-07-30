package com.tyrfing.games.id17.houses;

import java.io.Serializable;

import com.tyrfing.games.id17.holdings.Holding;

public class Claim implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = -3350515936591469822L;
	
	public final Holding holding;
	
	public Claim(Holding holding) {
		this.holding = holding;
	}
}
