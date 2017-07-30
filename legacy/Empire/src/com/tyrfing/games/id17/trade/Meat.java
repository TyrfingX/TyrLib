package com.tyrfing.games.id17.trade;

import gnu.trove.list.array.TIntArrayList;

import java.util.List;

import com.tyrfing.games.id17.Util;
import com.tyrfing.games.id17.holdings.Holding;

public class Meat extends Good {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = -2567910523140537742L;
	public static final String NAME 		= "Meat";
	public static final float  BASE_VALUE  	= 5;
	
	public final static int ID = 4;
	
	private float change = 0;
	
	public Meat() {
		super(NAME, BASE_VALUE, ID);
	}
	
	public Meat(int quantity, Holding producer) {
		super(NAME, BASE_VALUE, quantity, producer, ID);
	}
	
	public Meat(int quantity, TIntArrayList quantities, List<Holding> producers) {
		super(NAME, BASE_VALUE, quantity, producers, quantities, ID);
	}

	@Override
	public void onAddSupply(Holding holding) {
		super.onAddSupply(holding);
		change = holding.holdingData.baseSupplies *  (0.2f + 0.02f * quantity) * getMult(holding, ID) ;
		holding.holdingData.tradeSupplies += change;
	}

	@Override
	public void onRemoveSupply(Holding holding) {
		super.onRemoveSupply(holding);
		holding.holdingData.tradeSupplies -= change;
	}

	@Override
	public Good copy() {
		return new Meat(quantity, quantities, producers);
	}
	
	@Override
	public Good create() {
		return new Meat();
	}
	
	@Override
	public String getTooltip(Holding holding) {
		float mult = 1 + (0.1f + 0.02f * quantity) * getMult(holding, ID) - 1;
		int intMult = (int)(mult * 100);
		return Util.GREEN_TEXT + " +" + intMult + "%\\# Supplies \n Value " + getValue(holding) + " Gold";
	}
	
}
