package com.tyrfing.games.id17.trade;

import gnu.trove.list.array.TIntArrayList;

import java.util.List;

import com.tyrfing.games.id17.Util;
import com.tyrfing.games.id17.holdings.Holding;
import com.tyrfing.games.id17.world.World;

public class Bread extends Good {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = -6898059131799924584L;
	public static final String NAME 		= "Bread";
	public static final float  BASE_VALUE  	= 21;
	
	public final static int ID = 6;
	
	private float change = 0;
	
	public Bread() {
		super(NAME, BASE_VALUE, ID);
	}
	
	public Bread(int quantity, Holding producer) {
		super(NAME, BASE_VALUE, quantity, producer, ID);
	}

	public Bread(int quantity, TIntArrayList quantities, List<Holding> producers) {
		super(NAME, BASE_VALUE, quantity, producers, quantities, ID);
	}
	
	@Override
	public void onAddSupply(Holding holding) {
		super.onAddSupply(holding);
		change = holding.holdingData.baseSupplies * (0.6f + 0.05f * quantity) * getMult(holding, ID);
		holding.holdingData.tradeSupplies += change;
	}

	@Override
	public void onRemoveSupply(Holding holding) {
		super.onRemoveSupply(holding);
		holding.holdingData.tradeSupplies -= change;
	}


	@Override
	public Good copy() {
		return new Bread(quantity, quantities, producers);
	}

	@Override
	public Good create() {
		return new Bread();
	}
	
	@Override
	public String getTooltip(Holding holding) {
		float mult = 1 + (0.4f + 0.05f * quantity * World.getInstance().getGoodMult(ID)) * getMult(holding, ID) - 1;
		int intMult = (int)(mult * 100);
		return Util.GREEN_TEXT + " +" + intMult + "%\\# Supplies \n Value " + getValue(holding) + " Gold";
	}


	
}
