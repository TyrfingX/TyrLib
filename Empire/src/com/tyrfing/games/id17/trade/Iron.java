package com.tyrfing.games.id17.trade;

import gnu.trove.list.array.TIntArrayList;

import java.util.List;

import com.tyrfing.games.id17.Util;
import com.tyrfing.games.id17.holdings.Holding;

public class Iron extends Good {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1854383004165837643L;
	public static final String NAME 		= "Iron";
	public static final float  BASE_VALUE  	= 16;
	
	public final static int ID = 3;
	
	public Iron() {
		super(NAME, BASE_VALUE, ID);
	}
	
	public Iron(int quantity, Holding producer) {
		super(NAME, BASE_VALUE, quantity, producer, ID);
	}
	
	public Iron(int quantity, TIntArrayList quantities, List<Holding> producers) {
		super(NAME, BASE_VALUE, quantity, producers, quantities, ID);
	}

	@Override
	public void onAddSupply(Holding holding) {
		super.onAddSupply(holding);
		holding.holdingData.defMult += (0.25f + quantity * 0.01f) * getMult(holding, ID);
	}

	@Override
	public void onRemoveSupply(Holding holding) {
		super.onRemoveSupply(holding);
		holding.holdingData.defMult -= (0.25f + quantity * 0.01f) * getMult(holding, ID);
	}

	@Override
	public Good copy() {
		return new Iron(quantity, quantities, producers);
	}
	
	@Override
	public Good create() {
		return new Iron();
	}

	@Override
	public String getTooltip(Holding holding) {
		float mult = (0.2f + quantity * 0.05f) * getMult(holding, ID);
		int intMult = (int)(mult * 100);
		return Util.GREEN_TEXT + " +" + intMult + "%\\# Def (All Units) \n Value " + getValue(holding) + " Gold";
	}
	
}
