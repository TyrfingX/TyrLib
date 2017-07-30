package com.tyrfing.games.id17.holdings;

import java.io.Serializable;

import com.tyrfing.games.id17.houses.House;
import com.tyrfing.games.id17.war.RebelArmy;
import com.tyrfing.games.id17.war.Regiment;
import com.tyrfing.games.id17.war.UnitType;

public abstract class UnrestSource implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 920915601676451703L;
	
	public final String name;
	public float probability;
	public final float strength;
	public final String headline;
	public final String text;
	
	public UnrestSource(String name, float probability, float strength, String headline, String text) {
		this.name = name;
		this.probability = probability;
		this.strength = strength;
		this.headline = headline;
		this.text = text;
	}
	
	public abstract UnrestSource copy();
	public abstract void comply(House house);
	
	public RebelArmy revolt(Holding holding) {
		int revoltees = 0;
		for (int i = 0; i < PopulationType.VALUES.length; ++i) {
			int popRevoltees = (int) (holding.holdingData.population[i] * strength);
			revoltees += (int) popRevoltees;
			holding.holdingData.changePop(i, -popRevoltees);
			holding.holdingData.inhabitants -= popRevoltees;
		}
		return createRebelArmy(holding, strength, revoltees);
	}
	
	public static RebelArmy createRebelArmy(Holding holding, float strength, int revoltees) {
		RebelArmy revoltArmy = new RebelArmy(holding, strength);
		revoltArmy.addRegiment(new Regiment(UnitType.Swordmen, revoltees, revoltees, 0));
		return revoltArmy;
	}
}
