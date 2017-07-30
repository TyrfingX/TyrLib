package com.tyrfing.games.id17.intrigue.actions;

import com.tyrfing.games.id17.holdings.Holding;
import com.tyrfing.games.id17.holdings.UnrestSource;
import com.tyrfing.games.id17.houses.House;
import com.tyrfing.games.id17.houses.TaxUnrest;

public class IncidentUnrest extends UnrestSource {
	/**
	 * 
	 */
	private static final long serialVersionUID = -8904560329356990529L;
	
	public final static String HEADLINE = "Different Ruler";
	public final static String TEXT = "The people would rather be ruled\nby House ";
	
	public final Holding holding;
	public final House preferredRuler;
	
	public IncidentUnrest(House preferredRuler, Holding holding) {
		super("Different Ruler", 0.03f, 0.3f, HEADLINE, TEXT + preferredRuler.getName());
		this.preferredRuler = preferredRuler;
		this.holding = holding;
	}
	
	@Override
	public void comply(House owner) {
		House.transferHolding(preferredRuler, holding, true);
	}

	@Override
	public UnrestSource copy() {
		return new TaxUnrest(probability);
	}
}
