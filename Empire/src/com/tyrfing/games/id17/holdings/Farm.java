package com.tyrfing.games.id17.holdings;

import com.tyrfing.games.id17.trade.GoodProduction;
import com.tyrfing.games.id17.trade.Grain;
import com.tyrfing.games.id17.trade.Horse;

public class Farm extends Holding {

	/**
	 * 
	 */
	private static final long serialVersionUID = 4546219103130230331L;
	public static final float[] stats = new float[HoldingTypes.COUNT_STATS];
	
	static {
		stats[HoldingTypes.SCHOLAR_ATTRACTIVITY] = -100;
		stats[HoldingTypes.GROWTH] = 0.004f;
		stats[HoldingTypes.INCOME] = 20.f;
		stats[HoldingTypes.PEASANTS_ATTRACTIVITY] = 16;
	}
	
	public Farm(HoldingData holdingData, float[] stats) {
		super(holdingData, stats);
		
		this.addDemand(Horse.ID, 2);
		
		GoodProduction grainProduction = new GoodProduction();
		grainProduction.addOutputGood(new Grain(5, this));
		grainProduction.producesInSeason[3] = false;
		productions.add(grainProduction);
	}
}
