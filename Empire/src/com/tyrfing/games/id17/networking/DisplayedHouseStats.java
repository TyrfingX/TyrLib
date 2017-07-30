package com.tyrfing.games.id17.networking;

import com.tyrfing.games.id17.houses.House;
import com.tyrfing.games.id17.world.World;
import com.tyrlib2.networking.Connection;

public class DisplayedHouseStats extends NetworkMessage {

	/**
	 * 
	 */
	private static final long serialVersionUID = 6675471240896382897L;

	public final short id;
	public final float gold;
	public final float income;
	public final float tradeIncome;
	public final float mercCosts;
	public final float taxIncome;
	public final float vassalIncome;
	public final float buildingMaint;
	public final float fertility;
	public final float accGrowth;
	public final float research;
	public final float armyMaint;
	public final int interest;
	
	public final short honor;
	
	public final byte males;
	public final byte females;
	
	public final float opinions[] = new float[World.getInstance().getHouses().size()];
	public final float favor;
	
	public final float stats[] = new float[House.COUNT_STATS];
	public final float modifierValues[];
	
	public DisplayedHouseStats(House house, House forHouse) {	
		this.id = house.id;
		this.gold = house.getGold();
		this.income = house.finalIncome;
		this.tradeIncome = house.tradeIncome;
		this.mercCosts = house.mercCosts;
		this.armyMaint = house.armyMaint;
		this.taxIncome = house.taxIncome;
		this.vassalIncome = house.vassalIncome;
		this.buildingMaint = house.buildingMaint;
		this.interest = house.interest;
		this.fertility = house.getFertility();
		this.accGrowth = house.accGrowth;
		this.honor = (short) house.getHonor();
		this.males = (byte) house.getMales();
		this.females = (byte) house.getFemales();
		this.research = house.getResearch();
		if (forHouse != null) {
			this.favor = house.getHouseStat(forHouse, House.FAVOR_STAT);
		} else {
			this.favor = 0;
		}
		
		for (int i = 0; i < World.getInstance().getHouses().size(); ++i) {
			opinions[i] = World.getInstance().getHouses().get(i).getHouseStat(house, House.RELATION_STAT);
		}
		
		for (int i = 0; i < stats.length; ++i) {
			stats[i] = house.stats[i];
		}
		
		modifierValues = new float[house.statModifiers.size()];
		for (int i = 0; i < modifierValues.length; ++i) {
			modifierValues[i] = house.statModifiers.get(i).value;
		}
	}
	
	@Override
	public void process(Connection c) {
		House h = World.getInstance().getMainGUI().houseGUI.getDisplayed();
		if (h == null) {
			h = World.getInstance().getPlayerController().getHouse();
		}
		
		if (h != null && h.id == id	) {
			h.gold.value = gold;
			h.finalIncome = income;
			h.tradeIncome = tradeIncome;
			h.mercCosts = mercCosts;
			h.armyMaint = armyMaint;
			h.taxIncome = taxIncome;
			h.interest = interest;
			h.vassalIncome = vassalIncome;
			h.buildingMaint = buildingMaint;
			h.fertility = fertility;
			h.accGrowth = accGrowth;
			h.honor = honor;
			h.males = males;
			h.females = females;
			h.setResearch(research);
			
			for (int i = 0; i < World.getInstance().getHouses().size(); ++i) {
				h.setHouseStat(World.getInstance().getHouses().get(i), House.RELATION_STAT, opinions[i]);
				World.getInstance().getHouses().get(i).setHouseStat(h, House.RELATION_STAT, opinions[i]);
			}
			
			h.setHouseStat(World.getInstance().getPlayerController().getHouse(), House.FAVOR_STAT, favor);
		
			for (int i = 0; i < stats.length; ++i) {
				h.stats[i] = stats[i];
			}
			
			for (int i = 0; i < modifierValues.length && i < h.getCountStatModifiers(); ++i) {
				h.statModifiers.get(i).value = modifierValues[i];
			}
		}
	}
	
}
