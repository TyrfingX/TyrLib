package com.tyrfing.games.id17.trade;

import gnu.trove.list.array.TIntArrayList;

import java.util.List;

import com.tyrfing.games.id17.Util;
import com.tyrfing.games.id17.holdings.Holding;

public class Stone extends Good {

	/**
	 * 
	 */
	private static final long serialVersionUID = -4722782116076282686L;
	public static final String NAME 		= "Stone";
	public static final float  BASE_VALUE  	= 15;
	
	public final static int ID = 8;
	
	public Stone() {
		super(NAME, BASE_VALUE, ID);
	}
	
	public Stone(int quantity, Holding producer) {
		super(NAME, BASE_VALUE, quantity, producer, ID);
	}
	
	public Stone(int quantity, TIntArrayList quantities, List<Holding> producers) {
		super(NAME, BASE_VALUE, quantity, producers, quantities, ID);
	}

	@Override
	public void onAddSupply(Holding holding) {
		super.onAddSupply(holding);
		holding.holdingData.prodMult += (0.3f + quantity * 0.05f) * getMult(holding, ID);
	}

	@Override
	public void onRemoveSupply(Holding holding) {
		super.onRemoveSupply(holding);
		holding.holdingData.prodMult -= (0.3f + quantity * 0.05f) * getMult(holding, ID);
	}
	
	@Override
	public Good copy() {
		return new Stone(quantity, quantities, producers);
	}
	
	@Override
	public Good create() {
		return new Stone();
	}
	
	@Override
	public String getTooltip(Holding holding) {
		float mult = (0.3f + quantity * 0.05f) * getMult(holding, ID);
		int intMult = (int)(mult * 100);
		return Util.GREEN_TEXT + " +" + intMult + "%\\# Productivity \n Value " + getValue(holding) + " Gold";
	}

}
