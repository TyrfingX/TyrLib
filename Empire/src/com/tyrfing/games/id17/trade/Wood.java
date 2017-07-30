package com.tyrfing.games.id17.trade;

import gnu.trove.list.array.TIntArrayList;

import java.util.List;

import com.tyrfing.games.id17.Util;
import com.tyrfing.games.id17.holdings.Holding;

public class Wood extends Good {

	/**
	 * 
	 */
	private static final long serialVersionUID = -4722782116076282686L;
	public static final String NAME 		= "Wood";
	public static final float  BASE_VALUE  	= 3;
	
	public final static int ID = 5;
	
	public Wood() {
		super(NAME, BASE_VALUE, ID);
	}
	
	public Wood(int quantity, Holding producer) {
		super(NAME, BASE_VALUE, quantity, producer, ID);
	}
	
	public Wood(int quantity, TIntArrayList quantities, List<Holding> producers) {
		super(NAME, BASE_VALUE, quantity, producers, quantities, ID);
	}

	@Override
	public void onAddSupply(Holding holding) {
		super.onAddSupply(holding);
		holding.holdingData.prodMult += (0.25f + quantity * 0.05f) * getMult(holding, ID);
	}

	@Override
	public void onRemoveSupply(Holding holding) {
		super.onRemoveSupply(holding);
		holding.holdingData.prodMult -= (0.25f + quantity * 0.05f) * getMult(holding, ID);
	}
	
	@Override
	public Good copy() {
		return new Wood(quantity, quantities, producers);
	}
	
	@Override
	public Good create() {
		return new Wood();
	}
	
	@Override
	public String getTooltip(Holding holding) {
		float mult = (0.2f + quantity * 0.1f) * getMult(holding, ID);
		int intMult = (int)(mult * 100);
		return Util.GREEN_TEXT + " +" + intMult + "%\\# Productivity \n Value " + getValue(holding) + " Gold";
	}

}
