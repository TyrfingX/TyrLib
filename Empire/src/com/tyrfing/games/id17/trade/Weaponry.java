package com.tyrfing.games.id17.trade;

import gnu.trove.list.array.TIntArrayList;

import java.util.List;

import com.tyrfing.games.id17.Util;
import com.tyrfing.games.id17.holdings.Holding;

public class Weaponry extends Good {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = -8470930538888515262L;
	public static final String NAME 		= "Weaponry";
	public static final float  BASE_VALUE  	= 93;
	
	public final static int ID = 7;
	
	public Weaponry() {
		super(NAME, BASE_VALUE, ID);
	}
	
	public Weaponry(int quantity, Holding producer) {
		super(NAME, BASE_VALUE, quantity, producer, ID);
	}
	
	public Weaponry(int quantity, TIntArrayList quantities, List<Holding> producers) {
		super(NAME, BASE_VALUE, quantity, producers, quantities, ID);
	}

	@Override
	public void onAddSupply(Holding holding) {
		super.onAddSupply(holding);
		holding.holdingData.atkMult += (0.35f + quantity * 0.1f) * getMult(holding, ID);
		holding.holdingData.defMult += (0.35f + quantity * 0.1f) * getMult(holding, ID);
	}

	@Override
	public void onRemoveSupply(Holding holding) {
		super.onRemoveSupply(holding);
		holding.holdingData.atkMult -= (0.35f + quantity * 0.1f) * getMult(holding, ID);
		holding.holdingData.defMult -= (0.35f + quantity * 0.1f) * getMult(holding, ID);
	}

	@Override
	public Good copy() {
		return new Weaponry(quantity, quantities, producers);
	}
	
	@Override
	public Good create() {
		return new Weaponry();
	}
	
	@Override
	public String getTooltip(Holding holding) {
		float mult = (0.35f + quantity * 0.1f) * getMult(holding, ID);
		int intMult = (int)(mult * 100);
		return Util.GREEN_TEXT + " +" + intMult + "%\\# Power (All Units) \n Value " + getValue(holding) + " Gold";
	}
	
}
