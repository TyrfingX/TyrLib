package com.tyrfing.games.id17.effects;

import com.tyrfing.games.id17.houses.House;

public class HoldingStatEffect implements IEffect {

	public final String stat;
	public final float value;
	
	public HoldingStatEffect(String stat, float value) {
		this.stat = stat;
		this.value = value;
	}
	
	@Override
	public void apply(House house) {
		for (int i = 0; i < house.getHoldings().size(); ++i) {
			if (stat.equals("researchMult")) {
				house.getHoldings().get(i).holdingData.researchMult += value;
			}
		}
	}

	@Override
	public void unapply(House house) {
		for (int i = 0; i < house.getHoldings().size(); ++i) {
			if (stat.equals("researchMult")) {
				house.getHoldings().get(i).holdingData.researchMult -= value;
			}
		}
	}

}
