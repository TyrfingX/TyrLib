package com.tyrfing.games.id17.holdings;

import com.tyrfing.games.id17.trade.GoodProduction;
import com.tyrfing.games.id17.trade.Grain;
import com.tyrfing.games.id17.trade.Meat;

public class Pasture extends Holding {

	/**
	 * 
	 */
	private static final long serialVersionUID = -7044718675635749628L;
	public static final float[] stats = new float[HoldingTypes.COUNT_STATS];
	
	static {
		stats[HoldingTypes.GROWTH] = 0.0000004f;
		stats[HoldingTypes.INCOME] = 0.1f;
		stats[HoldingTypes.PEASANTS_ATTRACTIVITY] = 50;
	}
	
	
	public Pasture(HoldingData holdingData, float[] stats) {
		super(holdingData, stats);
		
		GoodProduction woodProduction = new GoodProduction();
		woodProduction.addOutputGood(new Meat(5, this));
		productions.add(woodProduction);
		
		this.addDemand(Grain.ID, 3);
	}

}
