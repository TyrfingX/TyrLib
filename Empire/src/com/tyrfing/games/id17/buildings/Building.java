package com.tyrfing.games.id17.buildings;

import java.io.Serializable;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import com.tyrfing.games.id17.gui.mails.HeaderedMail;
import com.tyrfing.games.id17.gui.mails.Mail;
import com.tyrfing.games.id17.holdings.Barony;
import com.tyrfing.games.id17.holdings.Holding;
import com.tyrfing.games.id17.holdings.HoldingTypes;
import com.tyrfing.games.id17.houses.House;
import com.tyrfing.games.id17.trade.Good;
import com.tyrfing.games.id17.trade.GoodProduction;
import com.tyrfing.games.id17.trade.Grain;
import com.tyrfing.games.id17.trade.Horse;
import com.tyrfing.games.id17.trade.Iron;
import com.tyrfing.games.id17.trade.Meat;
import com.tyrfing.games.id17.trade.Stone;
import com.tyrfing.games.id17.trade.Weaponry;
import com.tyrfing.games.id17.trade.Wood;
import com.tyrfing.games.id17.war.UnitType;
import com.tyrfing.games.id17.world.World;
import com.tyrlib2.game.ILink;
import com.tyrlib2.game.LinkManager;
import com.tyrlib2.game.Stats;

public class Building implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 6819419924836203875L;
	
	public static final int COUNT_STATS = 26;
	public static final int PRODUCTION = 2;
	public static final int PRICE = 3;
	public static final int MERCHANT_ATTRACTIVITY_BONUS = 4;
	public static final int WORKER_ATTRACTIVITY_BONUS = 5;
	public static final int PEASANT_ATTRACTIVITY_BONUS = 6;
	public static final int SCHOLAR_ATTRACTIVITY_BONUS = 7;
	public static final int INCOME_BONUS = 8;
	public static final int PRODUCTION_BONUS = 9;
	public static final int PRICE_INC = 10;
	public static final int PROD_INC = 11;
	public static final int RESEARCH_BONUS = 12;
	public static final int TRADE_BONUS = 13;
	public static final int GOOD_PROD_OUT = 14;
	public static final int MERCHANT_ATTRACTIVITY_FACTOR = 15;
	public static final int WORKER_ATTRACTIVITY_FACTOR = 16;
	public static final int PEASANT_ATTRACTIVITY_FACTOR = 17;
	public static final int SCHOLAR_ATTRACTIVITY_FACTOR = 18;
	public static final int MERCHANT_TAX_BONUS = 19;
	public static final int WORKER_TAX_BONUS = 20;
	public static final int PEASANT_TAX_BONUS = 21;
	public static final int SCHOLAR_TAX_BONUS = 22;
	public static final int EDUCATION_BONUS = 23;
	public static final int RESEARCH_MULT_BONUS = 24;
	public static final int MAINTENANCE = 25;
	public static final int GOOD_PROD_IN = 26;
	public static final int GOOD_DEMAND_1_ID = 27;
	public static final int GOOD_DEMAND_1_AMOUNT = 28;
	public static final int GOOD_DEMAND_2_ID = 29;
	public static final int GOOD_DEMAND_2_AMOUNT = 30;
	public static final int GOOD_REQUIRE_ID = 31;
	public static final int BUILDING_REQUIRE_ID = 32;
	public static final int BUILDING_REQUIRE_LVL = 33;
	public static final int GOOD_EFFICIENCY_ID = 34;
	public static final int GOOD_EFFICIENCY_VALUE = 35;
	public static final int UNIT_DEF_ID = 36;
	public static final int UNIT_DEF_VALUE = 37;
	public static final int UNIT_PROD_FACTOR = 38;
	public static final int PROD_PER_GOOD_ID = 39;
	public static final int PROD_PER_GOOD_VALUE = 40;
	
	public enum TYPE {
		Market, Forge, Library, Granary, Guild, Cottage, 
		Shed, Field, Hunter, Tradepost, Lumberjack,
		Bazaar, Blastfurnace, University,
		Stables, Toolmaker, Barracks, Keep, Butcher;
	}
	
	public static final TYPE[] GREAT_BUILDINGS = { TYPE.Bazaar, TYPE.Blastfurnace, TYPE.University };
	public static final TYPE[] BASE_BUILDINGS = { TYPE.Market, TYPE.Forge, TYPE.Library };
	public static final Holding[] BUILT_GREAT_BUILDINGS = new Holding[GREAT_BUILDINGS.length];
	public static final TYPE[] VALUES = TYPE.values();
	
	public static final String[] DESC = {
		"Income: +20\n\nHolding becomes more attractive\nfor <link=Trader>traders\\l.\n\n" +
		"<link=Great Building>Great Building:\\l <link=Bazaar>Bazaar\\l",
		
		"+100<img MAIN_GUI BUILD_ICON_BIG>\n+5 Wood<img GOODS Wood> and +1 Iron<img GOODS Iron> Demand\n\nHolding becomes more attractive\nfor <link=Workers>workers\\l.\n\n" + 
		"<link=Great Building>Great Building:\\l <link=Blastfurnace>Blastfurnace\\l",
		
		"+1<img MAIN_GUI TECH_ICON_BIG>\n+1 Wood<img GOODS Wood> Demand\n\nHolding becomes more attractive\nfor <link=Scholar>scholars\\l.\n\n" + 
		"<link=Great Building>Great Building:\\l <link=University>University\\l",
		
		"+1 Grain<img GOODS Grain> Demand\n\nStores up grain up to the size\nof the granary and provides stored\n<link=Grain>Grain\\l<img GOODS Grain> when needed.",
		
		"A guild can be established here.",
		
		"Establish nearby settlements.\nHolding becomes more attractive\nfor <link=Worker>workers\\l and <link=Peasant>peasants\\l.",
		
		"+10<img MAIN_GUI BUILD_ICON_BIG>\n\nStore house for tools.",
		
		"+1 <link=Grain>Grain\\l<img GOODS Grain>\n+1 <link=Horse>Horse\\l<img GOODS Horse> Demand\n\nEstablish a new farming field.",
		
		"+1 Meat<img GOODS Meat>\n\nHunt for game in the woods.",
		
		"Trade Income: +20%\n\nEstablish hub for local trading.\nIncreases <link=Trader>traders\\l attractivity.",
		
		"+1 Wood<img GOODS Wood>\n\nEmploy more lumberjacks.",
		
		"+100% Trader Attractivity\n+100% Trade Income\n+10<img MAIN_GUI GOLD_ICON> per <link=Trader>Trader\\l",
		
		"+40 Wood<img GOODS Wood> and +20 Iron<img GOODS Iron> Demand\n+1 <link=Weaponry>Weaponry<img GOODS Weaponry>\\l if <link=Iron>Iron<img GOODS Iron>\\l available\n+300% <link=Workers>Worker\\l Attractivity", 

		"+100 <link=Education>Education\\l\n+100%<img MAIN_GUI TECH_ICON_BIG>\n+200% <link=Scholar>Scholar\\l Attractivity", 
	
		"+10% <link=Horse>Horse\\l<img GOODS Horse> Efficiency\n+5 Horse<img GOODS Horse> Demand\n\nConstruct resting places for horses.\nRequires available Horse<img GOODS Horse>",
		
		"+25 <img MAIN_GUI BUILD_ICON_BIG> per available <link=Iron>Iron\\l<img GOODS Iron>\n+5 Iron<img GOODS Iron> Demand\n\nConstruct facilities to create tools.\nRequires <link=Forge>Forge\\l 1 and available Iron<img GOODS Iron>",
		
		"+30% Unit Production Speed\n\nExpand space for unit training.\nRequires <link=Forge>Forge\\l 3",
		
		"+20% <link=Walls>Wall\\l Defense\n+5 <link=Stone>Stone\\l<img GOODS Stone> Demand\n\nStrengthen our local Defenses.\nRequires <link=Barracks>Barracks\\l 3 and available Stone<img GOODS Stone>",
		
		"+20% Efficiency of <link=Meat>Meat\\l<img GOODS Meat>\n+5 Meat<img GOODS Meat> Demand\n\nEmploy butchers for higher quality food.\nRequires <link=Toolmaker>Toolmaker\\l 3 and available Meat<img GOODS Meat>",
	};
	
	
	public static final Map<TYPE, Stats> STATS = new HashMap<TYPE, Stats>();

	public static final float GREAT_BUILDING_HONOR = 50;
	public static final float GREAT_BUILDING_SCORE_MULT = 0.2f;
	public static final float GREAT_BUILDING_HOUSE_POINTS = 100f/(World.DAYS_PER_SEASON*World.SECONDS_PER_DAY);
	public static final int GREAT_BUILDING_MIN_LEVEL = 10;
	
	static {
		Stats stats = new Stats();
		stats.setStat(INCOME_BONUS, 20f);
		stats.setStat(PRODUCTION, 2000f);
		stats.setStat(PRICE, 50f);
		stats.setStat(PROD_INC, 4000f);
		stats.setStat(PRICE_INC, 100f);
		stats.setStat(MERCHANT_ATTRACTIVITY_BONUS, 300f);
		STATS.put(TYPE.Market, stats);
		
		stats = new Stats();
		stats.setStat(PRODUCTION_BONUS, 100f);
		stats.setStat(PRODUCTION, 2500f);
		stats.setStat(PRICE, 100f);
		stats.setStat(PROD_INC, 7500f);
		stats.setStat(PRICE_INC, 150f);
		stats.setStat(WORKER_ATTRACTIVITY_BONUS, 300f);
		stats.setStat(GOOD_DEMAND_1_ID, Wood.ID+1);
		stats.setStat(GOOD_DEMAND_1_AMOUNT, 5);
		stats.setStat(GOOD_DEMAND_2_ID, Iron.ID+1);
		stats.setStat(GOOD_DEMAND_2_AMOUNT, 1);
		stats.setStat(MAINTENANCE, 0.5f);
		STATS.put(TYPE.Forge, stats);
		
		stats = new Stats();
		stats.setStat(RESEARCH_BONUS, 1f);
		stats.setStat(PRODUCTION, 5000f);
		stats.setStat(PRICE, 150f);
		stats.setStat(PROD_INC, 10000f);
		stats.setStat(PRICE_INC, 200f);
		stats.setStat(SCHOLAR_ATTRACTIVITY_BONUS, 300f);
		stats.setStat(GOOD_DEMAND_1_ID, Wood.ID+1);
		stats.setStat(GOOD_DEMAND_1_AMOUNT, 1);
		stats.setStat(MAINTENANCE, 0.5f);
		STATS.put(TYPE.Library, stats);
		
		stats = new Stats();
		stats.setStat(PRODUCTION, 10000f);
		stats.setStat(PRICE, 1000f);
		stats.setStat(PROD_INC, 20000f);
		stats.setStat(PRICE_INC, 1000f);
		stats.setStat(GOOD_DEMAND_1_ID, Grain.ID+1);
		stats.setStat(GOOD_DEMAND_1_AMOUNT, 1);
		stats.setStat(MAINTENANCE, 5);
		STATS.put(TYPE.Granary, stats);
		
		stats = new Stats();
		stats.setStat(PRODUCTION, 50000f);
		stats.setStat(PRICE, 2000f);
		stats.setStat(PRICE_INC, -1000f);
		stats.setStat(MERCHANT_ATTRACTIVITY_BONUS, 1500f);
		stats.setStat(MAINTENANCE, 50);
		STATS.put(TYPE.Guild, stats);
		
		stats = new Stats();
		stats.setStat(PRODUCTION, 300f);
		stats.setStat(PRICE, 50f);
		stats.setStat(PROD_INC, 900f);
		stats.setStat(PRICE_INC, 50f);
		stats.setStat(WORKER_ATTRACTIVITY_BONUS, 5f);
		stats.setStat(PEASANT_ATTRACTIVITY_BONUS, 10f);
		STATS.put(TYPE.Cottage, stats);
		
		stats = new Stats();
		stats.setStat(PRODUCTION, 150f);
		stats.setStat(PRODUCTION_BONUS, 10f);
		stats.setStat(PRICE, 25f);
		stats.setStat(PROD_INC, 1000f);
		stats.setStat(PRICE_INC, 50f);
		stats.setStat(MAINTENANCE, 0.2f);
		STATS.put(TYPE.Shed, stats);
		
		stats = new Stats();
		stats.setStat(PRODUCTION, 10000f);
		stats.setStat(PRICE, 200f);
		stats.setStat(PROD_INC, 3000f);
		stats.setStat(PRICE_INC, 200f);
		stats.setStat(GOOD_PROD_OUT, (float) Grain.ID); 
		stats.setStat(GOOD_DEMAND_1_ID, Horse.ID+1);
		stats.setStat(GOOD_DEMAND_1_AMOUNT, 2);
		stats.setStat(MAINTENANCE, 0.2f);
		STATS.put(TYPE.Field, stats);
		
		stats = new Stats();
		stats.setStat(PRODUCTION, 5000f);
		stats.setStat(PRICE, 100f);
		stats.setStat(PROD_INC, 5000f);
		stats.setStat(PRICE_INC, 100f);
		stats.setStat(GOOD_PROD_OUT, (float) Meat.ID); 
		stats.setStat(MAINTENANCE, 0.2f);
		STATS.put(TYPE.Hunter, stats);
		
		stats = new Stats();
		stats.setStat(PRODUCTION, 2000f);
		stats.setStat(PRICE, 100f);
		stats.setStat(PROD_INC, 2000f);
		stats.setStat(PRICE_INC, 100f);
		stats.setStat(MERCHANT_ATTRACTIVITY_BONUS, 25f);
		stats.setStat(TRADE_BONUS, 0.4f);
		STATS.put(TYPE.Tradepost, stats);
		
		stats = new Stats();
		stats.setStat(PRODUCTION, 5000f);
		stats.setStat(PRICE, 100f);
		stats.setStat(PROD_INC, 5000f);
		stats.setStat(PRICE_INC, 100f);
		stats.setStat(WORKER_ATTRACTIVITY_BONUS, 50f);
		stats.setStat(GOOD_PROD_OUT, (float) Wood.ID); 
		stats.setStat(MAINTENANCE, 0.2f);
		STATS.put(TYPE.Lumberjack, stats);
		
		stats = new Stats();
		stats.setStat(PRODUCTION, 1000000f);
		stats.setStat(PRICE, 15000f);
		stats.setStat(TRADE_BONUS, 1);
		stats.setStat(MERCHANT_ATTRACTIVITY_FACTOR, 1f);
		stats.setStat(MERCHANT_TAX_BONUS, 10f);
		STATS.put(TYPE.Bazaar, stats);
		
		stats = new Stats();
		stats.setStat(PRODUCTION, 1000000f);
		stats.setStat(PRICE, 15000f);
		stats.setStat(GOOD_DEMAND_1_ID, Wood.ID+1);
		stats.setStat(GOOD_DEMAND_1_AMOUNT, 40);
		stats.setStat(GOOD_DEMAND_2_ID, Iron.ID+1);
		stats.setStat(GOOD_DEMAND_2_AMOUNT, 20);
		stats.setStat(WORKER_ATTRACTIVITY_FACTOR, 3f);
		stats.setStat(GOOD_PROD_OUT, Weaponry.ID);
		stats.setStat(GOOD_PROD_IN, Iron.ID);
		STATS.put(TYPE.Blastfurnace, stats);
		
		stats = new Stats();
		stats.setStat(PRODUCTION, 1000000f);
		stats.setStat(PRICE, 15000f);
		stats.setStat(EDUCATION_BONUS, 100);
		stats.setStat(SCHOLAR_ATTRACTIVITY_FACTOR, 2f);
		stats.setStat(RESEARCH_MULT_BONUS, 1f);
		STATS.put(TYPE.University, stats);
		
		stats = new Stats();
		stats.setStat(PRODUCTION, 4000f);
		stats.setStat(PRICE, 100f);
		stats.setStat(PROD_INC, 4000f);
		stats.setStat(PRICE_INC, 200f);
		stats.setStat(MAINTENANCE, 0.2f);
		stats.setStat(GOOD_REQUIRE_ID, Horse.ID+1);
		stats.setStat(GOOD_DEMAND_1_ID, Horse.ID+1);
		stats.setStat(GOOD_DEMAND_1_AMOUNT, 5);
		stats.setStat(GOOD_EFFICIENCY_ID, Horse.ID+1);
		stats.setStat(GOOD_EFFICIENCY_VALUE, 0.2f);
		STATS.put(TYPE.Stables, stats);
		
		stats = new Stats();
		stats.setStat(PRODUCTION, 3000f);
		stats.setStat(PRICE, 100);
		stats.setStat(PROD_INC, 3000f);
		stats.setStat(PRICE_INC, 200f);
		stats.setStat(MAINTENANCE, 0.5f);
		stats.setStat(GOOD_REQUIRE_ID, Iron.ID+1);
		stats.setStat(BUILDING_REQUIRE_ID, TYPE.Forge.ordinal()+1);
		stats.setStat(BUILDING_REQUIRE_LVL, 1);
		stats.setStat(GOOD_DEMAND_1_ID, Iron.ID+1);
		stats.setStat(GOOD_DEMAND_1_AMOUNT, 5);
		stats.setStat(PROD_PER_GOOD_ID, Iron.ID+1);
		stats.setStat(PROD_PER_GOOD_VALUE, 25);
		STATS.put(TYPE.Toolmaker, stats);
		
		stats = new Stats();
		stats.setStat(PRODUCTION, 8000f);
		stats.setStat(PRICE, 300);
		stats.setStat(PROD_INC, 8000f);
		stats.setStat(PRICE_INC, 500f);
		stats.setStat(MAINTENANCE, 10f);
		stats.setStat(BUILDING_REQUIRE_ID, TYPE.Forge.ordinal()+1);
		stats.setStat(BUILDING_REQUIRE_LVL, 3);
		stats.setStat(UNIT_PROD_FACTOR, 0.3f);
		STATS.put(TYPE.Barracks, stats);
		
		stats = new Stats();
		stats.setStat(PRODUCTION, 16000f);
		stats.setStat(PRICE, 500);
		stats.setStat(PROD_INC, 16000f);
		stats.setStat(PRICE_INC, 500f);
		stats.setStat(MAINTENANCE, 0.2f);
		stats.setStat(GOOD_REQUIRE_ID, Stone.ID+1);
		stats.setStat(GOOD_DEMAND_1_ID, Stone.ID+1);
		stats.setStat(GOOD_DEMAND_1_AMOUNT, 5);
		stats.setStat(BUILDING_REQUIRE_ID, TYPE.Barracks.ordinal()+1);
		stats.setStat(BUILDING_REQUIRE_LVL, 3);
		stats.setStat(UNIT_DEF_ID, UnitType.Walls.ordinal()+1);
		stats.setStat(UNIT_DEF_VALUE, 0.2f);
		STATS.put(TYPE.Keep, stats);
		
		stats = new Stats();
		stats.setStat(PRODUCTION, 8000f);
		stats.setStat(PRICE, 100);
		stats.setStat(PROD_INC, 8000f);
		stats.setStat(PRICE_INC, 100f);
		stats.setStat(MAINTENANCE, 0.5f);
		stats.setStat(GOOD_DEMAND_1_ID, Meat.ID+1);
		stats.setStat(GOOD_DEMAND_1_AMOUNT, 5);
		stats.setStat(BUILDING_REQUIRE_ID, TYPE.Toolmaker.ordinal()+1);
		stats.setStat(BUILDING_REQUIRE_LVL, 3);
		stats.setStat(GOOD_REQUIRE_ID, Meat.ID+1);
		stats.setStat(GOOD_EFFICIENCY_ID, Meat.ID+1);
		stats.setStat(GOOD_EFFICIENCY_VALUE, 0.2f);
		STATS.put(TYPE.Butcher, stats);
		
		
		for (final TYPE type : TYPE.values()) {
			LinkManager.getInstance().registerLink(new ILink() {
				@Override
				public void onCall() {
					Mail back = World.getInstance().getMainGUI().mailboxGUI.getCurrentMail();
					Mail mail = createBuildMail(type);
					if (back != null) {
						mail.setBackMail(back);
					}
				}
			}, type.toString());
		}
	}
	
	public static float getPrice(TYPE type, Holding holding) {
		Stats stats = STATS.get(type);
		return stats.getStat(PRICE) + stats.getStat(PRICE_INC) * holding.getBuildingLevel(type) * holding.getBuildingLevel(type);
	}
	
	public static float getProd(TYPE type, Holding holding) {
		Stats stats = STATS.get(type);
		return (stats.getStat(PRODUCTION) + stats.getStat(PROD_INC) * holding.getBuildingLevel(type) * holding.getBuildingLevel(type)) * holding.getOwner().stats[House.PROD_COST_MULT] * (holding instanceof Barony ? 6 : 1);
	}
	
	public static Building create(TYPE type, int level) {
		if (type.equals(Building.TYPE.Guild)) {
			return new Guild(level);
		}
		
		return new Building(type, STATS.get(type), level);
	}
	
	public static boolean isBuildableInHolding(TYPE type, Holding holding) {
		int greatHoldingIndex = Arrays.binarySearch(GREAT_BUILDINGS, type);
		if (greatHoldingIndex >= 0) {
			if (BUILT_GREAT_BUILDINGS[greatHoldingIndex] != holding && BUILT_GREAT_BUILDINGS[greatHoldingIndex] != null) return false;
			if (holding.isBuilt(BASE_BUILDINGS[greatHoldingIndex]) == null || holding.isBuilt(BASE_BUILDINGS[greatHoldingIndex]).getLevel() < GREAT_BUILDING_MIN_LEVEL) return false;
			return true;
		}
		Stats stats = STATS.get(type);
		int requiredGoodID = (int) stats.getStat(GOOD_REQUIRE_ID);
		if (requiredGoodID != 0) {
			requiredGoodID--;
			if (holding.getGood(requiredGoodID) == null) return false;
		}
		
		int requiredBuildingID = (int) stats.getStat(BUILDING_REQUIRE_ID);
		if (requiredBuildingID != 0) {
			requiredBuildingID--;
			int buildingLvl = (int) stats.getStat(BUILDING_REQUIRE_LVL);
			Building b = holding.isBuilt(VALUES[requiredBuildingID]);
			if (b == null || b.getLevel() < buildingLvl) return false;
		}
		
		return holding.getOwner().isBuildingEnabled(holding.holdingData.typeName, type.ordinal());
	}
	
	private final Stats stats;
	private int level;
	private final TYPE type;
	
	public Building(TYPE type, Stats stats, int level) {
		this.stats = stats;
		this.level = level;
		this.type = type;
	}
	
	public Stats getStats() {
		return stats;
	}
	
	public TYPE getType() {
		return type;
	}
	
	public int getLevel() {
		return level;
	}
	
	public void changeLevel(int levelChange) {
		level += levelChange;
	}
	
	public void applyEffects(Holding holding, int levelInc) {
		float[] mults = holding.getOwner().buildingMult[type.ordinal()];
		holding.holdingData.incomeBonus += stats.getStat(Building.INCOME_BONUS) * (mults[Building.INCOME_BONUS]+1) * levelInc;
		holding.holdingData.prodBuildings += stats.getStat(Building.PRODUCTION_BONUS) * (mults[Building.PRODUCTION_BONUS]+1) * levelInc;
		holding.holdingData.researchBonus += stats.getStat(Building.RESEARCH_BONUS) * (mults[Building.RESEARCH_BONUS]+1) * levelInc;
		holding.holdingData.researchMult +=  stats.getStat(Building.RESEARCH_MULT_BONUS) * levelInc;
		holding.holdingData.tradeBonus += stats.getStat(Building.TRADE_BONUS) * levelInc;
		holding.holdingData.education += stats.getStat(Building.EDUCATION_BONUS) * levelInc;
		holding.getStats()[HoldingTypes.MERCHANT_ATTRACTIVITY] += stats.getStat(Building.MERCHANT_ATTRACTIVITY_BONUS) * (mults[Building.MERCHANT_ATTRACTIVITY_BONUS]+1+stats.getStat(Building.MERCHANT_ATTRACTIVITY_FACTOR)) * levelInc;
		holding.getStats()[HoldingTypes.PEASANTS_ATTRACTIVITY] += stats.getStat(Building.PEASANT_ATTRACTIVITY_BONUS) * (mults[Building.PEASANT_ATTRACTIVITY_BONUS]+1+stats.getStat(Building.PEASANT_ATTRACTIVITY_FACTOR)) * levelInc;
		holding.getStats()[HoldingTypes.WORKERS_ATTRACTIVITY] += stats.getStat(Building.WORKER_ATTRACTIVITY_BONUS) * (mults[Building.WORKER_ATTRACTIVITY_BONUS]+1+stats.getStat(Building.WORKER_ATTRACTIVITY_FACTOR)) * levelInc;
		holding.getStats()[HoldingTypes.SCHOLAR_ATTRACTIVITY] += stats.getStat(Building.SCHOLAR_ATTRACTIVITY_BONUS) * (mults[Building.SCHOLAR_ATTRACTIVITY_BONUS]+1+stats.getStat(Building.SCHOLAR_ATTRACTIVITY_FACTOR)) * levelInc;
		holding.getStats()[HoldingTypes.MERCHANT_TAX_BONUS] += stats.getStat(Building.MERCHANT_TAX_BONUS)  * levelInc;
		holding.getStats()[HoldingTypes.PEASANTS_TAX_BONUS] += stats.getStat(Building.PEASANT_TAX_BONUS) * levelInc;
		holding.getStats()[HoldingTypes.WORKERS_TAX_BONUS] += stats.getStat(Building.WORKER_TAX_BONUS) * levelInc;
		holding.getStats()[HoldingTypes.SCHOLAR_TAX_BONUS] += stats.getStat(Building.SCHOLAR_TAX_BONUS) * levelInc;
		holding.getStats()[HoldingTypes.UNIT_PROD_FACTOR] += stats.getStat(Building.UNIT_PROD_FACTOR) * levelInc;
		
		int goodProdOut = (int) stats.getStat(Building.GOOD_PROD_OUT);
		
		if (goodProdOut != 0) {
			GoodProduction prod = new GoodProduction();
			prod.addOutputGood(Good.createGood(goodProdOut, levelInc, holding));
			int goodProdIn = (int) stats.getStat(Building.GOOD_PROD_IN);
			if (goodProdIn != 0) {
				prod.addInputGood(Good.createGood(goodProdIn, levelInc, holding));
			}
			holding.addProductionAdditive(prod);
		}
		
	
		if (type == Building.TYPE.Granary) {
			holding.holdingData.storeGrainMax += levelInc;
		}
		
		int goodID = (int) stats.getStat(GOOD_DEMAND_1_ID);
		if (goodID != 0) {
			goodID--;
			
			holding.addDemand(goodID, levelInc*stats.getStat(GOOD_DEMAND_1_AMOUNT));
		}
		
		goodID = (int) stats.getStat(GOOD_DEMAND_2_ID);
		if (goodID != 0) {
			goodID--;
			
			holding.addDemand(goodID, levelInc*stats.getStat(GOOD_DEMAND_2_AMOUNT));
		}
		
		goodID = (int) stats.getStat(GOOD_EFFICIENCY_ID);
		if (goodID != 0) {
			goodID--;
			holding.holdingData.goodsMult[goodID] += levelInc*stats.getStat(GOOD_EFFICIENCY_VALUE);
		}
		
		goodID = (int) stats.getStat(PROD_PER_GOOD_ID);
		if (goodID != 0) {
			goodID--;
			holding.holdingData.prodPerGood[goodID] += levelInc*stats.getStat(PROD_PER_GOOD_VALUE);
		}
		
		int unitID = (int) stats.getStat(UNIT_DEF_ID);
		if (unitID != 0) {
			unitID--;
			holding.holdingData.typeMult[unitID] += levelInc*stats.getStat(UNIT_DEF_VALUE);
		}
		
		int greatBuildingIndex = Arrays.binarySearch(GREAT_BUILDINGS, type);
		if (greatBuildingIndex >= 0) {
			holding.getOwner().changeHonor(GREAT_BUILDING_HONOR);
			holding.getOwner().stats[House.SCORE_MULT] += GREAT_BUILDING_SCORE_MULT;
			holding.getOwner().stats[House.HOUSE_POINTS] += GREAT_BUILDING_HOUSE_POINTS;
			BUILT_GREAT_BUILDINGS[greatBuildingIndex] = holding;
		}
	}
	
	public static void createBuildMail(Building.TYPE type, Holding h) {
		Building b = h.isBuilt(type);
		if (b == null) {
			HeaderedMail m = createBuildMail0(type, h);
			if (m != null) m.addMainLabel(getDesc(type, h));
		} else {
			b.createBuildMail(h);
		}
	}
	
	public void createBuildMail(Holding h) {
		HeaderedMail m = createBuildMail0(type, h);
		if (m != null) {
			m.addMainLabel(getDesc(type, h));
		}
	}
	
	public static String getDesc(TYPE type, Holding h) {
		String desc = DESC[type.ordinal()];
		
		if (h != null) {
			float maint = ((int)(getMaintenance(h, type, 1)*100))/100f;
			if (maint != 0) {
				desc = "Maintenance " +  maint + "<img MAIN_GUI GOLD_ICON>\n" + desc;
			}
		}
		
		int greatBuildingIndex = Arrays.binarySearch(Building.GREAT_BUILDINGS, type);
		if (greatBuildingIndex >= 0) {
			if (Building.BUILT_GREAT_BUILDINGS[greatBuildingIndex] != h && BUILT_GREAT_BUILDINGS[greatBuildingIndex] != null) {
				desc += "\nThis <link=Great Building>Great Building\\l has already been\n" +
						"built in " + Building.BUILT_GREAT_BUILDINGS[greatBuildingIndex].getLinkedName();
			}
		}
		
		return desc;
	}
	
	public static HeaderedMail createBuildMail0(Building.TYPE type, Holding h) {
		
		String identity = h.toString();
		if (World.getInstance().getMainGUI().mailboxGUI.showIdentity(identity))
				return null;
		
		HeaderedMail mail = new BuildingMail(type, h);
		mail.setIdentity(identity);
		World.getInstance().getMainGUI().mailboxGUI.addMail(mail, true);
		return mail;
	}
	
	public static HeaderedMail createBuildMail(Building.TYPE type) {
		HeaderedMail mail = new BuildingMail(type);
		mail.addMainLabel(getDesc(type, null));
		World.getInstance().getMainGUI().mailboxGUI.addMail(mail, true);
		return mail;
	}
	
	public static float getMaintenance(Holding h, Building.TYPE type, int level) {
		Stats stats = STATS.get(type);
		float demandFactor = 1;
		int goodID = (int) stats.getStat(GOOD_DEMAND_1_ID);
		if (goodID != 0) {
			goodID--;
			float demand = stats.getStat(GOOD_DEMAND_1_AMOUNT);
			demandFactor *= h.getGoodFactor(goodID)*demand;
		}
		
		demandFactor = Math.max(1, demandFactor);
		
		goodID = (int) stats.getStat(GOOD_DEMAND_2_ID);
		if (goodID != 0) {
			goodID--;
			float demand = stats.getStat(GOOD_DEMAND_2_AMOUNT);
			demandFactor *= h.getGoodFactor(goodID)*demand;
		}
		
		demandFactor = Math.max(1, (float)Math.sqrt(demandFactor));
		return stats.getStat(MAINTENANCE) * demandFactor * level;
	}
	
	public static float getMaintenance(Holding h, Building b) {
		return getMaintenance(h, b.type, b.level);
	}
	
}
