package com.tyrfing.games.id17.trade;

import gnu.trove.list.array.TIntArrayList;

import java.util.List;

import com.tyrfing.games.id17.Util;
import com.tyrfing.games.id17.holdings.Holding;

public class Grain extends Good {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = -1209202714553356631L;
	public static final String NAME 		= "Grain";
	public static final float  BASE_VALUE  	= 1;
	
	public final static int ID = 1;
	
	private float change = 0;
	
	public Grain() {
		super(NAME, BASE_VALUE, ID);
	}
	
	public Grain(int quantity, Holding producer) {
		super(NAME, BASE_VALUE, quantity, producer, ID);
	}
	
	public Grain(int quantity, TIntArrayList quantities, List<Holding> producers) {
		super(NAME, BASE_VALUE, quantity, producers, quantities, ID);
	}

	@Override
	public void onAddSupply(Holding holding) {
		super.onAddSupply(holding);
		change = holding.holdingData.baseSupplies * (0.2f + 0.02f * quantity) * getMult(holding, ID);
		holding.holdingData.tradeSupplies += change;
		holding.holdingData.storeGrain += quantity;
	}

	@Override
	public void onRemoveSupply(Holding holding) {
		super.onRemoveSupply(holding);
		holding.holdingData.tradeSupplies -= change;
		holding.holdingData.storeGrain -= quantity;
	}

	@Override
	public Good copy() {
		return new Grain(quantity, quantities, producers);
	}
	
	@Override
	public Good create() {
		return new Grain();
	}
	
	@Override
	public String getTooltip(Holding holding) {
		float mult = 1 + (0.1f + 0.02f * quantity) * getMult(holding, ID) - 1;
		int intMult = (int)(mult * 100);
		return Util.GREEN_TEXT + " +" + intMult + "%\\# Supplies \n Value " + getValue(holding) + " Gold";
	}
	
}
