package com.tyrfing.games.id17.holdings;

import java.io.Serializable;
import java.util.HashMap;

public class HoldingTypes implements Serializable {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 3695135130915358138L;

	public static final HashMap<String, float[]> holdingStats = new HashMap<String, float[]>();

	public static final int INHABITANTS = 0;
	public static final int GROWTH = 1;
	public static final int INCOME = 2;
	
	public static final int POPULATION = 3;
	public static final int MERCHANTS = 3;
	public static final int SCHOLARS = 4;
	public static final int WORKERS = 5;
	public static final int PEASANTS = 6;
	
	public static final int ATTRACTIVITY = 7;
	public static final int MERCHANT_ATTRACTIVITY = 7;
	public static final int SCHOLAR_ATTRACTIVITY = 8;
	public static final int WORKERS_ATTRACTIVITY = 9;
	public static final int PEASANTS_ATTRACTIVITY = 10;
	public static final int MERCHANT_TAX_BONUS = 11;
	public static final int SCHOLAR_TAX_BONUS = 12;
	public static final int WORKERS_TAX_BONUS = 13;
	public static final int PEASANTS_TAX_BONUS = 14;
	public static final int UNIT_PROD_FACTOR = 15;
	
	public static final int COUNT_STATS = 16;

	public static final int TAX_BONUS = MERCHANT_TAX_BONUS;
	
	public HoldingTypes() {
		
		holdingStats.put("Castle", Barony.stats);
		holdingStats.put("Forest", Forest.stats);
		holdingStats.put("Village", Village.stats);
		holdingStats.put("Mine", Mine.stats);
		holdingStats.put("Windmill", Windmill.stats);
		holdingStats.put("Farm", Farm.stats);
		holdingStats.put("Ranch", Ranch.stats);
		holdingStats.put("Great Forest", GreatForest.stats);
		holdingStats.put("Pasture", Pasture.stats);
		holdingStats.put("Quarry", Quarry.stats);
		
		
		for (float[] stats : holdingStats.values()) {
			float inhabitants = stats[MERCHANTS] + stats[SCHOLARS] + stats[WORKERS] + stats[PEASANTS];
			stats[INHABITANTS] = inhabitants;
		}
	}
	
	public float[] getStats(String type) {
		return holdingStats.get(type);
	}
	
	public Holding createHolding(HoldingData data) {
		if (data.typeName.equals("Forest")) {
			return new Forest(data, holdingStats.get(data.typeName));
		} else if (data.typeName.equals("Village")) {
			return new Village(data, holdingStats.get(data.typeName));
		} else if (data.typeName.equals("Mine")) {
			return new Mine(data, holdingStats.get(data.typeName));
		} else if (data.typeName.equals("Windmill")) {
			return new Windmill(data, holdingStats.get(data.typeName));
		} else if (data.typeName.equals("Farm")) {
			return new Farm(data, holdingStats.get(data.typeName));
		} else if (data.typeName.equals("Ranch")) {
			return new Ranch(data, holdingStats.get(data.typeName));
		} else if (data.typeName.equals("Great Forest")) {
			return new GreatForest(data, holdingStats.get(data.typeName));
		} else if (data.typeName.equals("Pasture")) {
			return new Pasture(data, holdingStats.get(data.typeName));
		} else if (data.typeName.equals("Quarry")) {
			return new Quarry(data, holdingStats.get(data.typeName));
		}
		
		return null;
	}
}
