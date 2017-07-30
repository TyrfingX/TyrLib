package com.tyrfing.games.id17.holdings;

import com.tyrfing.games.id17.trade.Flour;
import com.tyrfing.games.id17.trade.GoodProduction;
import com.tyrfing.games.id17.trade.Grain;

public class Windmill extends Holding {

	/**
	 * 
	 */
	private static final long serialVersionUID = 5963967430530182947L;
	public static final float[] stats = new float[HoldingTypes.COUNT_STATS];
	
	static {
		stats[HoldingTypes.SCHOLAR_ATTRACTIVITY] = -100;
		stats[HoldingTypes.GROWTH] = 0.004f;
		stats[HoldingTypes.INCOME] = 15f;
		stats[HoldingTypes.WORKERS_ATTRACTIVITY] = 30;
	}
	
	
	public Windmill(HoldingData holdingData, float[] stats) {
		super(holdingData, stats);
		
		this.addDemand(Grain.ID, 1);
		
		GoodProduction flourProduction = new GoodProduction();
		flourProduction.addInputGood(new Grain(1, this));
		flourProduction.addOutputGood(new Flour(5, this));
		productions.add(flourProduction);
	}

}
