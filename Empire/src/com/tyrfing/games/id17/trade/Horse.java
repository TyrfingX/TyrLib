package com.tyrfing.games.id17.trade;

import gnu.trove.list.array.TIntArrayList;

import java.util.List;

import com.tyrfing.games.id17.Util;
import com.tyrfing.games.id17.holdings.Holding;
import com.tyrfing.games.id17.war.UnitType;

public class Horse extends Good {

	/**
	 * 
	 */
	private static final long serialVersionUID = -235265544617947011L;
	public static final String NAME 		= "Horse";
	public static final float  BASE_VALUE  	= 13;
	
	public final static int ID = 2;
	
	public Horse() {
		super(NAME, BASE_VALUE, ID);
	}
	
	public Horse( int quantity, Holding producer) {
		super(NAME, BASE_VALUE, quantity, producer, ID);
	}
	
	public Horse(int quantity, TIntArrayList quantities, List<Holding> producers) {
		super(NAME, BASE_VALUE, quantity, producers, quantities, ID);
	}
	
	@Override
	public void onAddSupply(Holding holding) {
		super.onAddSupply(holding);
		holding.holdingData.typeMult[UnitType.Cavalry.ordinal()] += (0.2f + 0.05f * quantity)  * getMult(holding, ID);
	}

	@Override
	public void onRemoveSupply(Holding holding) {
		super.onRemoveSupply(holding);
		holding.holdingData.typeMult[UnitType.Cavalry.ordinal()] -= (0.2f + 0.05f * quantity)  * getMult(holding, ID);
	}

	@Override
	public Good copy() {
		return new Horse(quantity, quantities, producers);
	}
	
	@Override
	public Good create() {
		return new Horse();
	}
	
	@Override
	public String getTooltip(Holding holding) {
		float mult = (0.1f + 0.05f * quantity) * getMult(holding, ID);
		int intMult = (int)(mult * 100);
		return Util.GREEN_TEXT + " +" + intMult + "%\\# Power (Cavalry) \n Value " + getValue(holding) + " Gold";
	}


}
