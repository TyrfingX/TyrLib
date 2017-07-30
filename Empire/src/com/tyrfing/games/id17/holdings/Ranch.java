package com.tyrfing.games.id17.holdings;

import com.tyrfing.games.id17.trade.GoodProduction;
import com.tyrfing.games.id17.trade.Grain;
import com.tyrfing.games.id17.trade.Horse;

public class Ranch extends Holding {

	/**
	 * 
	 */
	private static final long serialVersionUID = -2434187522775789445L;
	public static final float[] stats = new float[HoldingTypes.COUNT_STATS];
	
	static {
		stats[HoldingTypes.SCHOLAR_ATTRACTIVITY] = -100;
		stats[HoldingTypes.GROWTH] = 0.004f;
		stats[HoldingTypes.INCOME] = 0.1f;
		stats[HoldingTypes.WORKERS_ATTRACTIVITY] = 50;
	}
	
	
	public Ranch(HoldingData holdingData, float[] stats) {
		super(holdingData, stats);
		
		GoodProduction woodProduction = new GoodProduction();
		woodProduction.addOutputGood(new Horse(3, this));
		productions.add(woodProduction);
		
		this.addDemand(Grain.ID, 5);
	}

}
