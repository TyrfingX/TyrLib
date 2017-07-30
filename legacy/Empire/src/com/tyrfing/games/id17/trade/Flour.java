package com.tyrfing.games.id17.trade;

import gnu.trove.list.array.TIntArrayList;

import java.util.List;

import com.tyrfing.games.id17.Util;
import com.tyrfing.games.id17.holdings.Holding;

public class Flour extends Good {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 5546608157532718537L;
	public static final String NAME 		= "Flour";
	public static final float  BASE_VALUE  	= 11;
	
	public final static int ID = 0;
	
	private float change = 0;
	
	public Flour() {
		super(NAME, BASE_VALUE, ID);
	}
	
	public Flour(int quantity, Holding producer) {
		super(NAME, BASE_VALUE, quantity, producer, ID);
	}

	public Flour(int quantity, TIntArrayList quantities, List<Holding> producers) {
		super(NAME, BASE_VALUE, quantity, producers, quantities, ID);
	}
	
	@Override
	public void onAddSupply(Holding holding) {
		super.onAddSupply(holding);
		change = holding.holdingData.baseSupplies * (0.3f + 0.02f * quantity) * getMult(holding, ID);
		holding.holdingData.tradeSupplies += change;
	}

	@Override
	public void onRemoveSupply(Holding holding) {
		super.onRemoveSupply(holding);
		holding.holdingData.tradeSupplies -= change;
	}


	@Override
	public Good copy() {
		return new Flour(quantity, quantities, producers);
	}
	
	@Override
	public Good create() {
		return new Flour();
	}

	@Override
	public String getTooltip(Holding holding) {
		float mult = 1 + (0.25f + 0.05f * quantity) * getMult(holding, ID) - 1;
		int intMult = (int)(mult * 100);
		return Util.GREEN_TEXT + " +" + intMult + "%\\# Supplies \n Value " + getValue(holding) + " Gold";
	}
	
}
