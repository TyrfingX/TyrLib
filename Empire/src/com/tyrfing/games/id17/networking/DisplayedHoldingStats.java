package com.tyrfing.games.id17.networking;

import gnu.trove.map.hash.TIntFloatHashMap;

import com.tyrfing.games.id17.holdings.Holding;
import com.tyrfing.games.id17.holdings.HoldingData;
import com.tyrfing.games.id17.world.World;
import com.tyrlib2.networking.Connection;

public class DisplayedHoldingStats extends NetworkMessage {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 3368112643389990008L;
	
	public final float[] population;
	public final short supplies;
	public final float growth;
	public final float baseSupplies;
	public final float tradeSupplies;
	public final float hunger;
	
	public final float taxes;
	public final float maint;
	public final float buildingMaint;
	public final float trade;
	public final float income;
	public final float incomeMult;
	
	public final float research;
	public final float researchMult;
	public final float researchBuildings;
	public final float researchPop;
	
	public final float prod;
	public final float prodBuildings;
	public final float prodPop;
	public final float prodTrade;
	
	public final float revoltRisk;
	public final float accRevoltRisk;
	public final float troopRevoltStop;
	
	public final float totalWander;
	
	public final TIntFloatHashMap demands;
	
	public DisplayedHoldingStats(Holding holding) {
		HoldingData data = holding.holdingData;
		population = new float[data.population.length];
		for (int i = 0; i < population.length; ++i) {
			population[i] = data.population[i];
		}
		supplies = (short) data.barony.holdingData.supplies;
		growth = holding.growth;
		baseSupplies = data.baseSupplies;
		tradeSupplies = data.tradeSupplies;
		hunger = holding.hunger;
		taxes = data.taxes;
		maint = data.maint;
		buildingMaint = data.buildingMaint;
		trade = data.trade;
		income = data.income;
		incomeMult = data.incomeMult;
		research = data.research;
		researchMult = data.researchMult;
		researchBuildings = data.researchBuildings;
		researchPop = data.researchPop;
		prod = data.prod;
		prodBuildings = data.prodBuildings;
		prodPop = data.prodPop;
		prodTrade = data.prodTrade;
		revoltRisk = data.revoltRisk;
		accRevoltRisk = data.accRevoltRisk;
		troopRevoltStop = data.troopRevoltStop;
		totalWander = holding.totalWander;
		demands = holding.demandMap;
	}
	
	@Override
	public void process(Connection c) {
		Holding h = World.getInstance().getMainGUI().pickerGUI.holdingGUI.getDisplayed();
		if (h != null) {
			
			float pop = 0;
			for (int i = 0; i < h.holdingData.population.length; ++i) {
				h.holdingData.population[i] = population[i];
				pop += h.holdingData.population[i];
			}
			
			h.holdingData.inhabitants = (int) pop;
			h.holdingData.barony.holdingData.supplies = supplies;
			h.holdingData.income = income;
			h.holdingData.research = research;
			h.holdingData.prod = prod;
			h.holdingData.revoltRisk = revoltRisk;
			h.growth = growth;
			h.holdingData.baseSupplies =  baseSupplies;
			h.holdingData.tradeSupplies = tradeSupplies;
			h.hunger = hunger;
			h.holdingData.taxes = taxes;
			h.holdingData.maint =  maint;
			h.holdingData.buildingMaint = buildingMaint;
			h.holdingData.trade = trade;
			h.holdingData.incomeMult = incomeMult;
			h.holdingData.researchMult = researchMult;
			h.holdingData.researchBuildings = researchBuildings;
			h.holdingData.researchPop = researchPop;
			h.holdingData.prodBuildings = prodBuildings;
			h.holdingData.prodTrade = prodTrade;
			h.holdingData.prodPop = prodPop;
			h.holdingData.troopRevoltStop = troopRevoltStop;
			h.totalWander = totalWander;
			h.demandMap = demands;
		}
	}
}
