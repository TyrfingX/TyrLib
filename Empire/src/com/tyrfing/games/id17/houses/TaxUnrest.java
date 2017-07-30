package com.tyrfing.games.id17.houses;

import com.tyrfing.games.id17.holdings.UnrestSource;

public class TaxUnrest extends UnrestSource {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = -4055042197885799109L;
	public final static String HIGH_TAXES_HEADLINE = "Taxes too high";
	public final static String HIGH_TAXES_TEXT = "They claim we steal their money\nand demand lower taxes!";
	public final static float STRENGTH = 0.2f;
	
	public TaxUnrest(float probability) {
		super("High Taxes", probability, STRENGTH, HIGH_TAXES_HEADLINE, HIGH_TAXES_TEXT);
	}
	
	@Override
	public void comply(House house) {
		if (house.getLawSetting(0) > 0) {
			house.setLawSetting(0, house.getLawSetting(0)-1);
		}
		
		if (house.getLawSetting(1) > 0) {
			house.setLawSetting(1, house.getLawSetting(1)-1);
		}
	}

	@Override
	public UnrestSource copy() {
		return new TaxUnrest(probability);
	}
	
}
