package com.tyrfing.games.id17.effects;

import com.tyrfing.games.id17.holdings.Holding;
import com.tyrfing.games.id17.houses.House;
import com.tyrfing.games.id17.trade.GoodProduction;

public class AddProductionEffect implements IEffect {

	public final String[] inGoods;
	public final String[] outGoods;
	public final String holding;
	
	public AddProductionEffect(String[] inGoods, String[] outGoods, String holding) {
		this.inGoods = inGoods;
		this.outGoods = outGoods;
		this.holding = holding;
	}
	
	@Override
	public void apply(House house) {
		
		int[] qIn = new int[inGoods.length];
		for (int i = 0; i < qIn.length; ++i) {
			qIn[i] = 1;
		}
		
		int[] qOut = new int[outGoods.length];
		for (int i = 0; i < qOut.length; ++i) {
			qOut[i] = 1;
		}
		
		for (int i = 0; i < house.getHoldings().size(); ++i) {
			Holding h = house.getHoldings().get(i);
			if (h.holdingData.typeName.equals(holding)) {
				GoodProduction p = GoodProduction.createProduction(inGoods, qIn, outGoods, 
																   qOut, h);
				h.addProductionAdditive(p);
			}
		}
	}

	@Override
	public void unapply(House house) {

	}

}
