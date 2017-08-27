package com.tyrfing.games.id17.houses;

import gnu.trove.list.array.TIntArrayList;
import gnu.trove.list.array.TShortArrayList;
import gnu.trove.map.hash.TIntObjectHashMap;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.tyrfing.games.id17.EmpireFrameListener;
import com.tyrfing.games.id17.EmpireFrameListener.GameState;
import com.tyrfing.games.id17.Util;
import com.tyrfing.games.id17.buildings.Building;
import com.tyrfing.games.id17.diplomacy.Message;
import com.tyrfing.games.id17.diplomacy.actions.Marriage;
import com.tyrfing.games.id17.diplomacy.actions.PayLoan;
import com.tyrfing.games.id17.diplomacy.actions.Rivalize;
import com.tyrfing.games.id17.gui.PlayerController;
import com.tyrfing.games.id17.gui.mails.HeaderedMail;
import com.tyrfing.games.id17.holdings.Barony;
import com.tyrfing.games.id17.holdings.Holding;
import com.tyrfing.games.id17.holdings.HoldingTypes;
import com.tyrfing.games.id17.houses.reputation.Reputation;
import com.tyrfing.games.id17.houses.reputation.ReputationSet;
import com.tyrfing.games.id17.intrigue.IntrigueProject;
import com.tyrfing.games.id17.laws.EconomyCategory;
import com.tyrfing.games.id17.laws.Law;
import com.tyrfing.games.id17.laws.LawSet;
import com.tyrfing.games.id17.networking.AddVisibleBarony;
import com.tyrfing.games.id17.networking.ChangeHoldingOwner;
import com.tyrfing.games.id17.networking.ChangeJustification;
import com.tyrfing.games.id17.networking.HouseStatChange;
import com.tyrfing.games.id17.technology.Technology;
import com.tyrfing.games.id17.technology.TechnologyProject;
import com.tyrfing.games.id17.trade.Good;
import com.tyrfing.games.id17.war.Army;
import com.tyrfing.games.id17.war.UnitType;
import com.tyrfing.games.id17.war.War;
import com.tyrfing.games.id17.war.WarGoal;
import com.tyrfing.games.id17.war.WarJustification;
import com.tyrfing.games.id17.world.Border.Status;
import com.tyrfing.games.id17.world.World;
import com.tyrlib2.game.IUpdateable;
import com.tyrlib2.game.Stats;
import com.tyrlib2.game.Updater;
import com.tyrlib2.graphics.scene.SceneManager;
import com.tyrlib2.util.Valuef;

public class House implements IUpdateable, Serializable {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 394823680541977650L;
	
	/**
	 * STATS FOR DESCRIBING RELATIONS BETWEEN
	 * TWO HOUSES
	 */
	public static final int RELATION_STAT = 0;
	public static final int HAS_DIPLOMAT = 1;
	
	public static final int HAS_MARRIAGE = 3;
	public static final int HAS_DEFENSIVE_PACT = 4;
	public static final int HAS_TRADE_AGREEMENT = 7;
	public static final int HAS_TRUCE = 8;
	public static final int FAVOR_STAT = 9;
	public static final int COURT_POWER = 10;
	public static final int HAS_SPY = 11;
	public static final int OFFERS_PROTECTION = 12;
	public static final int INFLUENCED = 13;
	
	/**
	 * STATS FOR DESCRIBING THE STATE OF A SINGLE HOUSE
	 */
	
	public static final int COUNT_STATS = 23;
	public static final int POP_TAXES = 0;
	public static final int TRADE_TAXES = 1;
	public static final int CONSCRIPTION = 2;
	public static final int MERCENARIES = 3;
	public static final int VASSAL_TAXES = 4;
	public static final int VASSAL_ARMY = 5;
	public static final int WARMONGERER = 6;
	public static final int DIPLOMATIC_REPUTATION = 7;
	public static final int HONORABLE = 8;
	public static final int UNSTABLE = 9;
	public static final int INCOME_MULT = 10;
	public static final int ARMY_MAINT_MULT = 11;
	public static final int PROD_COST_MULT = 12;
	public static final int ARMY_SPEED_MULT = 13;
	public static final int SUPPLY_MULT = 14;
	public static final int IS_PROTECTED = 15;
	public static final int PROGRESS = 16;
	public static final int WEALTH = 17;
	public static final int CONSTRUCTION = 18;
	public static final int TYRANNY = 19;
	public static final int RESEARCH_MULT = 20;
	public static final int SCORE_MULT = 21;
	public static final int RIVAL_ID = 22;
	
	public static final String[] STAT_NAMES = { "Living Taxes", "Trade Taxes", "Conscription", 
												"Mercenary Reinforcemes", "Vassal Taxes",
												"Vassal Army Support", "Warmongering",
												"Diplomatic Reputation", "Honorable Reputation",
												"Instability", "Income", "Maintanence",
												"Production Time", "Movement", "Supply",
												"Protected", "Progress", "Wealth", "Construction", 
												"Tyranny", "Research", "Score", "Rival ID"
	};
	
	
	public static final int RELATION_HIT_TAXES = -20;
	public static final int RELATION_HIT_ARMY = -40;
	
	
	public static final int MAX_RELATIONS = 500;
	public static final int MIN_RELATIONS = -500;
	
	public static final int MAX_HONOR = 300;
	public static final int MIN_HONOR = -MAX_HONOR;
	
	public static final int DEFAULT_GOLD = 500;
	public static final int DEFAULT_HONOR = 200;
	
	public static final int DIPLO_COOLDOWN = (int) ( 10 * World.SECONDS_PER_DAY );
	
	public static final float SPY_COURT_POWER = 20;
	public static final float NEIGHBOUR_COURT_POWER = 10;
	public static final float SAME_DYNASTY_COURT_POWER = 10;
	
	public static final float LAW_TIMER = World.SECONDS_PER_DAY * World.DAYS_PER_YEAR;
	public static final float FERTILITY_PER_PAIR = 0.01f;
	public static final float FERTILITY_PER_MARRIAGE = 0.05f;
	
	public static final int INTELLECTUAL_EXPLORATION = 7;
	public static final float INTELLECTUAL_EXPLORATION_DECAY = -0.5f / World.DAYS_PER_SEASON;
	
	public Valuef gold = new Valuef(DEFAULT_GOLD);
	public float honor = 0;
	public float income = 0;
	private float newIncome = 0;
	public float fertility = 0;
	public float accGrowth;
	private float research;
	private float newResearch;
	
	public float passedLawTime;
	
	public int males = 5;
	public int females = 5;
	
	private String name;
	
	private TIntObjectHashMap<Stats> houseRelations = new TIntObjectHashMap<Stats>();
	public float[] stats = new float[COUNT_STATS];
	public transient List<House> subHouses = new ArrayList<House>();
	public transient List<Holding> holdings = new ArrayList<Holding>();
	public transient List<Barony>  baronies = new ArrayList<Barony>();
	
	public List<Integer> holdingIDs = new ArrayList<Integer>();

	private transient House overlord;
	
	public transient List<War> wars = new ArrayList<War>();
	
	public transient HouseController controller;
	
	private boolean isOnCooldown;
	
	private float passedCooldown;
	private boolean updateReputation;
	
	public List<StatModifier> statModifiers = new ArrayList<StatModifier>();
	private Updater statUpdater = new Updater();
	
	public List<Loan> loans = new ArrayList<Loan>();
	public TShortArrayList armies = new TShortArrayList();
	
	public House supremeOverlord = this;

	public short id;
	
	public IntrigueProject intrigueProject;
	public TechnologyProject techProject;
	
	public byte[] lawSettings;
	
	private int maxHoldings;
	
	public static final int MAX_REPUTATIONS = 3;
	public Reputation[] activeReputations = new Reputation[MAX_REPUTATIONS];
	
	public transient Set<Barony> neighbours = new LinkedHashSet<Barony>();
	public List<House> houseNeighbours = new ArrayList<House>();
	
	public int rank;
	public float points;
	public float holdingPoints;
	public float troopPoints;
	public float incomePoints;
	public float honorPoints;
	public float techPoints;
	
	public static final int REALM_POINTS = 0;
	public static final int ECONOMY_POINTS = 1;
	public static final int MILITARY_POINTS = 2;
	public static final int DIPLOMACY_POINTS = 3;
	public static final int HOUSE_POINTS = 4;
	public static final int EXPLORATION_POINTS = 5;
	
	public static final float POINTS_PER_HOLDING = 0.03f;
	public static final float POINTS_PER_VASSAL_HOLDING = 0.015f;
	
	public static final float HOLDINGS_PER_MEMBER = 0.5f;
	public static final float HOLDING_BOOST_CASTLE = 1.2f;
	
	public static final int LAW_TYRANNY = 10;
	
	public static final float INFLUENCE_PER_HOLDING = 5;
	public static final float INFLUENCE_PER_SPECIAL = 5;
	public static final float INFLUENCE_PER_FAVOR = 0.2f;
	public static final int INFLUENCE_PROTECT = 200;
	public static final float BASE_SELF_INFLUENCE = 20f;
	
	public static final String[] pointCategories = new String[] {
			"Realm", "Economy", "Military", "Diplomacy", "House", "Exploration"
	};
	
	public float[] pointsInc = new float[pointCategories.length];
	
	public boolean[] researched;
	public boolean[] discovered;
	
	private Technology[] techs;
	private int countTechs;
	
	private int exploredBaronies;
	public int totalTroops;
	
	private Map<String, boolean[]> enabledBuildings;
	private boolean[] enabledUnits = new boolean[UnitType.values().length];
	public float[] unitTypeMult = new float[UnitType.values().length];
	public float[][] buildingMult = new float[Building.TYPE.values().length][Building.COUNT_STATS];
	public float[][] goodsMult = new float[Good.COUNT_GOODS][2];
	
	public static String REBEL_FACTION_NAME = "Rebels";
	public static String MARAUDER_FACTION_NAME = "Marauders";
	
	public static final int INITIAL_DIPLO_POINTS = 10;

	private static final float EXPLORATION_HONOR = 2;

	
	
	public TIntArrayList visibleBaronies = new TIntArrayList();
	private TIntArrayList visibleHouses = new TIntArrayList();
	
	public float finalIncome;
	public float tradeIncome;
	public float taxIncome;
	public float armyMaint;
	public float buildingMaint;
	public float mercCosts;
	public float holdingMaint;
	public int countMarriages;
	public float vassalIncome;
	public int interest;

	private float greatBuildingPoints;
	
	private boolean npcFaction;
	
	private List<WarJustification> justifications = new ArrayList<WarJustification>();
	private House hegemon = this;
	
	public House(String name, HouseController controller, short id) {
		this.id = id;
		this.name = name;
		this.controller = controller;
		controller.control(this);
		
		enabledBuildings = new HashMap<String, boolean[]>();
		for (String holdingType : HoldingTypes.holdingStats.keySet()) {
			enabledBuildings.put(holdingType, new boolean[Building.TYPE.values().length]);
		}
		
		lawSettings = new byte[Law.COUNT_LAWS];
		
		if (EmpireFrameListener.MAIN_FRAME.getNetwork() == null || EmpireFrameListener.MAIN_FRAME.getNetwork().isHost()) {
			if (!isNPCFaction()) {
				for (int i = 0; i < LawSet.categories.length; ++i) {
					for (int j = 0; j < LawSet.categories[i].laws.length; ++j) {
						LawSet.categories[i].laws[j].selectOption(LawSet.categories[i].laws[j].defaultOption, this);
						passedLawTime = 0;
					}
				}
			}
		}
		
		maxHoldings = (int) ((males + females) * HOLDINGS_PER_MEMBER);
		
		enableBuilding("Castle", Building.TYPE.Bazaar.ordinal());
		enableBuilding("Castle", Building.TYPE.Blastfurnace.ordinal());
		enableBuilding("Castle", Building.TYPE.University.ordinal());
		
		enableBuilding("Castle", Building.TYPE.Market.ordinal());
		enableBuilding("Village", Building.TYPE.Market.ordinal());
		enableBuilding("Farm", Building.TYPE.Cottage.ordinal());
		enableBuilding("Farm", Building.TYPE.Shed.ordinal());
		enableBuilding("Farm", Building.TYPE.Field.ordinal());
		enableBuilding("Great Forest", Building.TYPE.Hunter.ordinal());
		enableBuilding("Great Forest", Building.TYPE.Tradepost.ordinal());
		enableBuilding("Great Forest", Building.TYPE.Lumberjack.ordinal());
		enableBuilding("Forest", Building.TYPE.Tradepost.ordinal());
		enableBuilding("Forest", Building.TYPE.Lumberjack.ordinal());
		enableBuilding("Ranch", Building.TYPE.Shed.ordinal());
		enableBuilding("Pasture", Building.TYPE.Shed.ordinal());
		enableBuilding("Mine", Building.TYPE.Shed.ordinal());
		enableBuilding("Windmill", Building.TYPE.Shed.ordinal());
		enableBuilding("Quarry", Building.TYPE.Shed.ordinal());
		enableBuilding("Forest", Building.TYPE.Shed.ordinal());
		enableBuilding("Great Forest", Building.TYPE.Shed.ordinal());
		enableBuilding("Ranch", Building.TYPE.Cottage.ordinal());
		enableBuilding("Pasture", Building.TYPE.Cottage.ordinal());
		enableBuilding("Mine", Building.TYPE.Cottage.ordinal());
		enableBuilding("Windmill", Building.TYPE.Cottage.ordinal());
		enableBuilding("Quarry", Building.TYPE.Cottage.ordinal());
		
		enableUnit(UnitType.Swordmen);
		enableUnit(UnitType.Axemen);
		enableUnit(UnitType.Archers);
		enableUnit(UnitType.Pikemen);
		enableUnit(UnitType.Guardians);
		
		changeHonor(DEFAULT_HONOR);
		
		researched = new boolean[Technology.COUNT_TECHS];
		discovered = new boolean[Technology.COUNT_TECHS];
		techs = new Technology[Technology.COUNT_TECHS];
		
		for (int i = 0; i < unitTypeMult.length; ++i) {
			unitTypeMult[i] = 1;
		}
		
		for (int i = 0; i < goodsMult.length; ++i) {
			goodsMult[i][0] = 1;
			goodsMult[i][1] = 1;
		}
		
		stats[INCOME_MULT] = 1;
		stats[ARMY_MAINT_MULT] = 1;
		stats[PROD_COST_MULT] = 1;
		stats[ARMY_SPEED_MULT] = 1;
		stats[SUPPLY_MULT] = 1;
		stats[RESEARCH_MULT] = 1;
		stats[SCORE_MULT] = 1;
		
		passedLawTime = 0;
	}
	
	public void findVisibleBaronies() {
		World world = World.getInstance();
		
		for (int i = 0; i < world.getCountBaronies(); ++i) {
			Barony b = world.getBarony(i);
			
			float minDist = com.tyrfing.games.id17.world.WorldMap.HUGE_DISTANCE;
			
			for (int j = 0; j < getHoldings().size(); ++j) {
				Holding h = getHoldings().get(j);
				minDist = Math.min(minDist, world.getMap().getDistance(h.getHoldingID(), b.getHoldingID()));
			}
			
			if (minDist < 1) {
				b.explore(this, false);
			}
			
			if (world.getPlayerController().getHouse() == this) {
				if (minDist >= 1) {
					b.setExplored(false);
				}
				
				world.getMap().getFogMap().updateFog(b);
			}
		}
	}
	
	public boolean isVisible(int baronyIndex) {
		return visibleBaronies.contains(baronyIndex);
	}
	
	public void addVisibleBarony(Barony b, boolean discovery) {
		
		if (EmpireFrameListener.MAIN_FRAME.getNetwork() != null) {
			if (EmpireFrameListener.MAIN_FRAME.getNetwork().isHost()) {
				EmpireFrameListener.MAIN_FRAME.getNetwork().broadcast(new AddVisibleBarony(
						(short) id, (short) b.getIndex(), discovery
				));
			}
		}
		
		if (discovery) {
			exploredBaronies++;
			honor += EXPLORATION_HONOR;
			
			addStatModifier(new VaryingStatModifier("Exploration", House.PROGRESS, this, -1, INTELLECTUAL_EXPLORATION, INTELLECTUAL_EXPLORATION_DECAY, 0));
		}
		
		this.getSupremeOverlord().propagateVisibleBarony(b);
		if (this.getBaronies().size() > 0) {
			b.getOwner().getSupremeOverlord().propagateVisibleBarony(this.getBaronies().get(0));
			b.getOwner().getSupremeOverlord().propagateVisibleBarony(this.getSupremeOverlord().getBaronies().get(0));
		}
	}
	
	private void propagateVisibleBarony(Barony b) {
		boolean change = false;
		
		if (visibleBaronies != null && !visibleBaronies.contains(b.getIndex())) {
			visibleBaronies.add(b.getIndex());
			change = true;
		}
		
		if (b.getOwner() != null && !visibleHouses.contains(b.getOwner().id)) {
			visibleHouses.add(b.getOwner().id);
			change = true;
		}
		
		if (change) {
			updateNeighbours();
		}
		
		if (World.getInstance().getPlayerController().getHouse() == this) {
			b.setExplored(true);
			World.getInstance().getMap().getFogMap().updateFog(b);
			
			if (!World.getInstance().areDetailsVisible()) {
				for (int j = 0; j < b.getWorldChunk().blockTypes.length; ++j) {
					b.getWorldChunk().blockTypes[j].mat.setStrategic(true, b.getOwner().getController().getStrategicColor());
				}	
			}
		}
		
		for (int i = 0; i < subHouses.size(); ++i) {
			subHouses.get(i).propagateVisibleBarony(b);
		}
		
		for (int i = 0; i < subHouses.size(); ++i) {
			subHouses.get(i).propagateVisibleBarony(b);
		}
		
		if (this != controller.house) {
			if (!controller.house.visibleBaronies.contains(b.getIndex())) {
				controller.house.propagateVisibleBarony(b);
			}
		}
		
		for (int i = 0; i < controller.subFactions.size(); ++i) {
			if (controller.subFactions.get(i) != this) {
				if (!controller.subFactions.get(i).visibleBaronies.contains(b.getIndex())) {
					controller.subFactions.get(i).propagateVisibleBarony(b);
				}
			}
		}
	}
	
	public int getCountVisibleBaronies() {
		return visibleBaronies.size();
	}
	
	public int getVisibleBarony(int index) {
		return visibleBaronies.getQuick(index);
	}
	
	public void enableUnit(UnitType type) {
		enabledUnits[type.ordinal()] = true;
	}
	
	public void disableUnit(UnitType type) {
		enabledUnits[type.ordinal()] = false;
	}
	
	public boolean isUnitEnabled(UnitType type) {
		return enabledUnits[type.ordinal()];
	}
	
	public void enableBuilding(String holdingType, int buildingID) {
		enabledBuildings.get(holdingType)[buildingID] = true;
	}
	
	public void disableBuilding(String holdingType, int buildingID) {
		enabledBuildings.get(holdingType)[buildingID] = false;
	}
	
	public boolean isBuildingEnabled(String holdingType, int buildingID) {
		return enabledBuildings.get(holdingType)[buildingID];
	}
	
	public int getLawSetting(int lawID) {
		return lawSettings[lawID];
	}
	
	public void setLawSetting(int lawID, int setting) {
		
		if (lawSettings[lawID] == setting) return;
		
		Law law = LawSet.getLaw(lawID);
		if (law.options[lawSettings[lawID]].isTyrannical) {
			stats[TYRANNY] -= LAW_TYRANNY;
		}
		
		lawSettings[lawID] = (byte) setting;
		
		if (law.options[lawSettings[lawID]].isTyrannical) {
			stats[TYRANNY] += LAW_TYRANNY;
		}
		
		if (lawID == LawSet.categories[0].laws[0].ID || lawID == LawSet.categories[0].laws[1].ID) {
			for (int i = 0; i < holdings.size(); ++i) {
				holdings.get(i).removeUnrestSource(EconomyCategory.HIGH_TAXES[0].name);
			}
			
			for (int i = 0; i < holdings.size(); ++i) {
				if (lawSettings[0] != 0) {
					holdings.get(i).addUnrestSource(EconomyCategory.HIGH_TAXES[lawSettings[0]-1].copy());
				}
				if (lawSettings[1] != 0) {
					holdings.get(i).addUnrestSource(EconomyCategory.HIGH_TAXES[lawSettings[1]-1].copy());
				}
			}
			
		}
		
		passedLawTime = LAW_TIMER;
	}
	
	public void createBorders() {

	}
	
	public List<House> getSubHouses() {
		return subHouses;
	}
	
	public void addSubHouse(House house) {
		
		if (this.isSubjectOf(house)) {
			house.removeSubject(this);
		}
		
		if (house.isSubjectOf(this) && !subHouses.contains(house)) {
			this.removeSubject(house);
		}
		
		if (!this.subHouses.contains(house)) {
		
			if (house.getOverlord() != null) {
				house.getOverlord().removeSubHouse(house);
			}
			
			this.subHouses.add(house);
			
			house.setOverlord(this);
			house.updateSupremeOverlord(supremeOverlord);
			
			controller.informAddAlly(house);
			
			House supOverlord = this.getSupremeOverlord();
			for (int i = 0; i < getCountVisibleBaronies(); ++i) {
				supOverlord.addVisibleBarony(World.getInstance().getBarony(getVisibleBarony(i)), false);
			}
			
			for (int i = 0; i < supOverlord.getCountVisibleBaronies(); ++i) {
				this.addVisibleBarony(World.getInstance().getBarony(supOverlord.getVisibleBarony(i)), false);
			}
			
			if (EmpireFrameListener.state == GameState.MAIN) {
				if (controller == World.getInstance().getPlayerController()) {
					HeaderedMail mail = new HeaderedMail(
						"New Vassal",
						"House of " + house.getLinkedName() + " has joined our\n" + 
						"dynasty and are now one of our\n" + 
						"vassal branch families.",
						this, house
					);
					mail.setIconName("Dynasty");
					World.getInstance().getMainGUI().mailboxGUI.addMail(mail, false);
				} else if (house.controller == World.getInstance().getPlayerController()) {
					HeaderedMail mail = new HeaderedMail(
							"New Overlord",
							"House of " + house.getLinkedName() + " has become\n" + 
							"our dynasty head as we can no longer\n" + 
							"be independent.",
							this, house
						);
						mail.setIconName("Dynasty");
						World.getInstance().getMainGUI().mailboxGUI.addMail(mail, false);
				}
			}
		
		}
	}
	
	public void removeSubject(House house) {
		for (int i = 0; i < subHouses.size(); ++i) {
			if (subHouses.get(i) == house) {
				subHouses.remove(i);
				return;
			} else {
				subHouses.get(i).removeSubject(house);
			}
		}
	}
	
	private void updateSupremeOverlord(House supremeOverlord) {
		this.supremeOverlord = supremeOverlord;
		for (int i = 0; i < subHouses.size(); ++i) {
			subHouses.get(i).updateSupremeOverlord(supremeOverlord);
		}
	}
	
	public void removeSubHouse(House house) {
		house.setOverlord(null);
		
		for (int i = 0; i < house.subHouses.size(); ++i) {
			house.subHouses.get(i).updateSupremeOverlord(house);
		}
		
		this.subHouses.remove(house);
		controller.informRemoveAlly(house);
	}
	
	public void updateBorders() {
		if (!SceneManager.getInstance().getRenderer().isInServerMode()) {
			List<Barony> baronies = this.getAllBaronies();
			
			House house = World.getInstance().getPlayerController().getHouse();
			
			if (house == null) return;
			
			for (int i = 0; i < baronies.size(); ++i) {
				Barony barony = baronies.get(i);
				
				if (house.haveSameOverlordWith(this)) {
					barony.getWorldChunk().getBorder().setStatus(Status.PLAYER);
				} else {
					if (house.getController().isEnemy(barony.getOwner()) != null) {
						barony.getWorldChunk().getBorder().setStatus(Status.ENEMY);
					} else if (house.getHouseStat(barony.getOwner().getSupremeOverlord(), House.HAS_DEFENSIVE_PACT) == 1) {
						barony.getWorldChunk().getBorder().setStatus(Status.ALLY);
					} else if (barony.getOwner().getSupremeOverlord().getHegemon() == house.getSupremeOverlord()){
						barony.getWorldChunk().getBorder().setStatus(Status.SUBORDINATE);
					} else if (house.getSupremeOverlord() == barony.getOwner().getSupremeOverlord().getHegemon()) {
						barony.getWorldChunk().getBorder().setStatus(Status.HEGEMON);
					} else {
						barony.getWorldChunk().getBorder().setStatus(Status.NEUTRAL);
					}
				}
				
				barony.getWorldChunk().getBorder().setMainCoords(World.getInstance().getMap().getVisibleBorders(barony));
				barony.getWorldChunk().getBorder().rebuild();
				

			}
		}
	}
	
	public List<Barony> getBaronies() {
		return baronies;
	}
	
	public List<Barony> getAllBaronies() {
		List<Barony> baronies = new ArrayList<Barony>();
		baronies.addAll(this.getBaronies());
		for (int i = 0, countSubHouses = subHouses.size(); i < countSubHouses; ++i) {
			baronies.addAll(subHouses.get(i).getAllBaronies());
		}
		return baronies;
	}
	
	public List<Holding> getAllHoldings() {
		List<Holding> holdings = new ArrayList<Holding>();
		holdings.addAll(this.holdings);
		for (int i = 0; i < subHouses.size(); ++i) {
			holdings.addAll(subHouses.get(i).getAllHoldings());
		}
		return holdings;
	}
	
	public int getWeighedTotalTroopCount() {
		int countBaronies = baronies.size();
		
		if (countBaronies == 0) {
			return 0;
		}
		
		float moral = 0;
		
		for (int i = 0; i < countBaronies; ++i) {
			Barony b = baronies.get(i);
			moral += b.getLevy().getMoral();
		}
		
		int weighedTroops = (int)(totalTroops * moral / countBaronies);
	
		
		for (int i = 0, countSubHouses = subHouses.size(); i < countSubHouses; ++i) {
			weighedTroops += subHouses.get(i).getWeighedTotalTroopCount() / 2;
		}
		
		return weighedTroops;
	}
	
	public List<Holding> getHoldings() {
		return holdings;
	}
	
	public void addHolding(Holding holding) {
		
		holdings.add(holding);
		holdingIDs.add(Integer.valueOf(holding.getHoldingID()));
		holding.controleBy(this);
		controller.informNewHolding(holding);
		
		if (holding instanceof Barony) {
			Barony barony = (Barony) holding;
			baronies.add(barony);
			barony.getLevy().setOwner(this);
			barony.getGarrison().setOwner(this);
			
			maxHoldings = (int) ((males + females) * HOLDINGS_PER_MEMBER * HOLDING_BOOST_CASTLE);
			
			armies.add(barony.getLevy().id);
			armies.add(barony.getGarrison().id);
		}
		
		if (World.getInstance().getMap().isBuilt()) {
			updateNeighbours();
		}
		
		if (lawSettings[0] != 0) {
			holding.addUnrestSource(EconomyCategory.HIGH_TAXES[lawSettings[0]-1].copy());
		}
		if (lawSettings[1] != 0) {
			holding.addUnrestSource(EconomyCategory.HIGH_TAXES[lawSettings[1]-1].copy());
		}
		
	}
	
	public static void transferHolding(House dest, Holding h, boolean updateFamily) {
		
		House src = h.getOwner();
		
		if (src == dest) return;
		
		src.addJustification(new WarJustification(h, src));
		
		src.removeHolding(h);
		dest.addHolding(h);
		
		if (h instanceof Barony && updateFamily) {
		
			Barony barony = (Barony) h;
			World.getInstance().getMap().changeOwner(barony);
			
			if (src.getBaronies().size() == 0 && !src.isSubjectOf(dest)) {
				dest.addSubHouse(src);
				
				if (dest.isSubjectOf(src)) {
					src.removeSubHouse(dest);
				}
			}
			
			src.updateBorders();
			dest.updateBorders();
			
			if (!World.getInstance().areDetailsVisible()&& barony.isExplored()){
				for (int j = 0; j < barony.getWorldChunk().blockTypes.length; ++j) {
					barony.getWorldChunk().blockTypes[j].mat.setStrategic(true, barony.getOwner().getController().getStrategicColor());
				}	
			}
		
		}
		
		if (updateFamily) {
			src.getSupremeOverlord().updateFamily();
			dest.getSupremeOverlord().updateFamily();
		}
		
		if (EmpireFrameListener.MAIN_FRAME.getNetwork().isHost()) {
			EmpireFrameListener.MAIN_FRAME.getNetwork().broadcast(new ChangeHoldingOwner(h.getHoldingID(), dest.id));
		}
	}
	
	public void notifyOverlordVassalHoldingLost(Holding holding) {
		holdingPoints -= POINTS_PER_VASSAL_HOLDING;
		if (getOverlord() != null) {
			getOverlord().notifyOverlordVassalHoldingLost(holding);
		}
	}
	
	public void removeHolding(Holding holding) {
		holdings.remove(holding);
		holdingIDs.remove(Integer.valueOf(holding.getHoldingID()));
		controller.informLostHolding(holding);
		holding.clearUnrest();
		
		for (int i = 0; i < wars.size(); ++i) {
			War war = wars.get(i);
			if (war.goal.goalHolding == holding || (holding instanceof Barony && ((Barony)holding).hasSubHolding(holding) )) {
				if (EmpireFrameListener.MAIN_FRAME.getNetwork().isHost()) {
					war.end();
				}
			}
		}
		
		if (holding instanceof Barony) {
			baronies.remove((Barony)holding);
			if (this.getBaronies().size() == 0) {
				maxHoldings = (int) ((males + females) * HOLDINGS_PER_MEMBER);
			}
			
			armies.remove(Short.valueOf(((Barony) holding).getLevy().id));
			armies.remove(Short.valueOf(((Barony) holding).getGarrison().id));
		}
		
		updateNeighbours();
	
		holdingPoints -= POINTS_PER_HOLDING;
		if (getOverlord() != null) {
			getOverlord().notifyOverlordVassalHoldingLost(holding);
		}
		
	}
	
	public void updateNeighbours() {
		neighbours.clear();
		houseNeighbours.clear();
		List<Barony> allBaronies = this.getAllBaronies();
		for (int i = 0; i < allBaronies.size(); ++i) {
			Barony[] baronies = World.getInstance().getMap().getNeighbours(allBaronies.get(i));
			for (int j = 0; j < baronies.length; ++j) {
				if (baronies[j].getOwner() != this && isVisible(baronies[j].getIndex())) {
					neighbours.add(baronies[j]);
					
					for (int k = 0; k < baronies[j].getCountSubHoldings(); ++k) {
						House n = baronies[j].getSubHolding(k).getOwner();
						if (n != this && !houseNeighbours.contains(n)) {
							houseNeighbours.add(n);
							if (!houseNeighbours.contains(n.getSupremeOverlord()) && visibleHouses.contains(n.getSupremeOverlord().id)) {
								houseNeighbours.add(n.getSupremeOverlord());
							}
						}
					}
				}
			}
		}
		
		if (getOverlord() != null && getOverlord() != this) {
			getOverlord().updateNeighbours();
		} 
	}
	
	public boolean isRealmNeighbour(House house) {
		
		if (house.haveSameOverlordWith(this)) return false;
		
		Iterator<Barony> itr = this.neighbours.iterator();

		while(itr.hasNext()) {
			Barony barony = itr.next();
			for (int i = 0; i < barony.getCountSubHoldings(); ++i) {
				if (barony.getSubHolding(i).getOwner() == house) {
					return true;
				}
			}
		}
		
		return false;
	}
	
	public int getMaxHoldings() {
		return maxHoldings;
	}
	
	public float getTaxFactor() {
		int overlimit = holdings.size() - maxHoldings;
		if (overlimit < 0) overlimit = 0;
		return 1 / (overlimit+1.f);
	}
	
	public void updateFamily() {
		if (this.getBaronies().size() == 0 && subHouses.size() > 0) {
			for (int i = 0; i < subHouses.size(); ++i) {
				House house = subHouses.get(i);
				this.removeSubHouse(house);
				if (this.getOverlord() != null && !this.getOverlord().hasOverlord(house)) {
					this.getOverlord().addSubHouse(house);
				}
			}
			
			if (this.getSupremeOverlord() != null) {
				this.getSupremeOverlord().updateBorders();
			}
		}
		
		for (int i = 0; i < subHouses.size(); ++i) {
			House house = subHouses.get(i);
			house.updateFamily();
		}
		
		this.updateBorders();
	}
	
	public boolean hasOverlord(House house) {
		if (overlord == house) return true;
		if (overlord == null) return false;
		return overlord.hasOverlord(house);
 	}
	
	public void addIncome(float gold) {
		newIncome += gold;
	}
	
	public void addResearch(float research) {
		newResearch += research;
	}
	
	public float getIncome() {
		return income;
	}
	
	public float getResearch() {
		return research;
	}
	
	public int getMales() {
		return males;
	}

	public int getFemales() {
		return females;
	}
	
	public String getSigilName() {
		if (name.equals(MARAUDER_FACTION_NAME)) {
			return REBEL_FACTION_NAME;
		} else {
			return name;
		}
	}
	
	public void changeMales(int males) {
		
		if (this.males + males < 0) {
			throw new RuntimeException("Error negative amount of males in House " + this.getName());
		}
		
		this.males += males; 
		if (this.getBaronies().size() > 0) {
			maxHoldings = (int) ((this.males + this.females) * HOLDINGS_PER_MEMBER * HOLDING_BOOST_CASTLE);
		} else {
			maxHoldings = (int) ((this.males + this.females) * HOLDINGS_PER_MEMBER);
		}
	}
	
	public void changeFemales(int females) {
		if (this.females + females < 0) {
			throw new RuntimeException("Error negative amount of females in House " + this.getName());
		}
		
		this.females += females;
		if (this.getBaronies().size() > 0) {
			maxHoldings = (int) ((this.males + this.females) * HOLDINGS_PER_MEMBER * HOLDING_BOOST_CASTLE);
		} else {
			maxHoldings = (int) ((this.males + this.females) * HOLDINGS_PER_MEMBER);
		}
	}
	
	private void updateFertility() {
		fertility = 	Math.min(males, females) * FERTILITY_PER_PAIR 
					+ 	countMarriages * FERTILITY_PER_MARRIAGE
					+ 	0.01f;
	}
	
	public boolean canPassLaws() {
		return passedLawTime == 0;
	}
	
	@Override
	public void onUpdate(float time) {
		
		totalTroops = 0;
		World world = World.getInstance();
		for (int i = 0, countArmies = armies.size(); i < countArmies; ++i) {
			Army army = world.getArmy(armies.getQuick(i));
			totalTroops += army.getTotalTroops();
		}
		
		totalTroops = this.getTotalTroops();
		
		time *= World.getInstance().getPlaySpeed();
		
		if (EmpireFrameListener.MAIN_FRAME.getNetwork().isHost()) {
			if (passedLawTime > 0) {
				passedLawTime -= time;
				
				if (passedLawTime <= 0) {
					passedLawTime = 0;
				}
			}
			
			updateFertility();
			accGrowth += time * fertility / (World.DAYS_PER_SEASON*World.SECONDS_PER_DAY);
			
			if (accGrowth >= 1) {
				if (Math.random() >= 0.5f) {
					changeMales(1);
				} else {
					changeFemales(1);
				}
				accGrowth--;
			}
			
			for (int i = 0; i < loans.size(); ++i) {
				Loan loan = loans.get(i);
				if (loan.taker == this && !loan.ended) {
					float interest = time * loan.interest / (World.DAYS_PER_SEASON * World.SECONDS_PER_DAY);
					this.changeGold(-interest);
					loan.giver.changeGold(interest);
					if (loan.endDate <= World.getInstance().getWorldTime()) {
						endLoan(loan);
					}
				}
			}
		
			statUpdater.onFrameRendered(time);
		}
		
		if (EmpireFrameListener.MAIN_FRAME.getNetwork().isHost()) {
			income = newIncome;
			newIncome = 0;
		
			tradeIncome = 0;
			taxIncome = 0;
			armyMaint = 0;
			mercCosts = 0;
			holdingMaint = 0;
			finalIncome = 0;
			vassalIncome = 0;
			interest = 0;
			buildingMaint = 0;
			
			for (int i = 0, countHoldings = holdings.size(); i < countHoldings; ++i) {
				Holding h = holdings.get(i);
				taxIncome += h.holdingData.taxes;
				tradeIncome += h.holdingData.trade;
				holdingMaint -= h.holdingData.maint;
				buildingMaint -= h.holdingData.buildingMaint;
				
				if (h instanceof Barony) {
					Barony b = (Barony) h;
					Army levy = b.getLevy();
					Army garrison = b.getGarrison();
					armyMaint -= levy.maint;
					armyMaint -= garrison.maint;
					mercCosts += b.mercCosts;
				}
				
			}
			
			for (int i = 0, countLoans = loans.size(); i < countLoans; ++i) {
				Loan loan = loans.get(i);
				if (!loan.ended) {
					if (loan.giver == this) {
						interest += loans.get(i).interest;
					} else {
						interest -= loans.get(i).interest;
					}
				}
			}
			
			for (int i = 0; i < subHouses.size(); ++i) {
				House subHouse = subHouses.get(i);
				vassalIncome += subHouse.finalIncome * stats[VASSAL_TAXES];
			}
			
			finalIncome = taxIncome + tradeIncome + armyMaint + holdingMaint + mercCosts + vassalIncome + interest + buildingMaint;
		
			research = newResearch;
			newResearch = 0.1f;
		} 		

		
		if (passedCooldown > 0 ) {
			passedCooldown -= time;
		
			if (passedCooldown <= 0) {
				this.isOnCooldown = false;
			}
		}
		
		if (techProject != null) {
			techProject.onUpdate(time);
		}
		
		if (updateReputation) {
			
			for (int i = 0; i < MAX_REPUTATIONS; ++i) {
				if (activeReputations[i] != null) {
					activeReputations[i].OnLoss(this);
					activeReputations[i] = null;
				}
			}
			
			Reputation[] reputations = ReputationSet.reputations;
			
			for (int i = 0; i < MAX_REPUTATIONS; ++i) {
				
				float bestValue = -1;
				int bestIndex = -1;
				
				for (int j = 0; j < reputations.length; ++j) {
					if (stats[reputations[j].houseStatNeed] > Reputation.MIN_POINTS  && !isActive(reputations[j])) {
						if (bestValue < stats[reputations[j].houseStatNeed]) {
							bestValue = stats[reputations[j].houseStatNeed];
							bestIndex = j;
						} 
					}
				}
				
				if (bestIndex != -1) {
					activeReputations[i] = reputations[bestIndex];
					activeReputations[i].onGain(this);
				}

			}
			
		}
		
		if (!isNPCFaction()) {
			updatePointInc();
			
			points += getTotalPointInc() * stats[House.SCORE_MULT] * time / 10;
		}
		
		updateHegemon();
		
	}
	
	public boolean isRebel() {
		return getName().equals(House.REBEL_FACTION_NAME);
	}
	
	public boolean isMarauder() {
		return getName().equals(House.MARAUDER_FACTION_NAME);
	}
	
	public float getTotalPointInc() {
		float inc = 0;
		for (int i = 0; i < pointsInc.length; ++i) {
			inc += pointsInc[i];
		}
		return inc;
	}
	
	public void updatePointInc() {
		troopPoints = this.getTotalTroops() / 5000.f;
		incomePoints = this.getIncome() / 1000f;
		honorPoints = honor / 1000;
		holdingPoints = getHoldingPoints();
		
		
		techPoints = 0;
		for (int i = 0; i < discovered.length; ++i) {
			if (discovered[i]) {
				techPoints += 0.02f * Math.log(World.getInstance().techTreeSet.trees[0].techs[i].scienceMax);
			}
		}
		
		greatBuildingPoints = 0;
		for (int i = 0; i < holdings.size(); ++i) {
			Holding h = holdings.get(i);
			for (int j = 0; j < Building.GREAT_BUILDINGS.length; ++j) {
				if (h.isBuilt(Building.GREAT_BUILDINGS[j]) != null) {
					greatBuildingPoints += Building.GREAT_BUILDING_HOUSE_POINTS;
				}
			}
		}
		
		pointsInc[REALM_POINTS] = holdingPoints;
		
		if (this.isIndependend()) {
			pointsInc[REALM_POINTS] *= 2;
		}
		
		pointsInc[MILITARY_POINTS] = troopPoints;
		pointsInc[ECONOMY_POINTS] = incomePoints;
		pointsInc[HOUSE_POINTS] = honorPoints + techPoints + greatBuildingPoints;
		pointsInc[EXPLORATION_POINTS] = exploredBaronies / (World.DAYS_PER_SEASON*World.SECONDS_PER_DAY);
	}
	
	public float getHoldingPoints() {
		float res = this.holdings.size()*House.POINTS_PER_HOLDING;
		for (int i = 0, countSubHouses = subHouses.size(); i < countSubHouses; ++i) {
			res += subHouses.get(i).getVassalHoldingPoints() ;
		}
		return res;
	}
	
	public float getVassalHoldingPoints() {
		float res = this.holdings.size()*House.POINTS_PER_VASSAL_HOLDING;
		for (int i = 0, countSubHouses = subHouses.size(); i < countSubHouses; ++i) {
			res += subHouses.get(i).getVassalHoldingPoints() ;
		}
		return res;
	}

	@Override
	public boolean isFinished() {
		// TODO Auto-generated method stub
		return false;
	}
	
	public String getName() {
		return name;
	}
	
	public String getLinkedName() {
		if (this.isMarauder()) {
			return name;
		} else {
			return "<link=" +  name + ">" + name + "\\l<img SIGILS1 " + name + ">";
		}
	}
	
	public HouseController getController() {
		return controller;
	}
	
	public void setHouseController(HouseController houseController) {
		
		if (controller != null) {
			controller.destroy();
		}
		
		this.controller = houseController;
		controller.control(this);
		
		if (houseController instanceof PlayerController) {
			makePlayerBorders();
			for (int i = 0; i < subHouses.size(); ++i) {
				subHouses.get(i).makePlayerBorders();
			}
			
			if (getOverlord() != null) {
				getOverlord().makePlayerBorders();
			}
		}
	}
	
	private void makePlayerBorders() {
		for (int i = 0; i < holdings.size(); ++i) {
			if (holdings.get(i) instanceof Barony) {
				((Barony)holdings.get(i)).getWorldChunk().getBorder().setStatus(Status.PLAYER);
			}
		}
	}
	
	public void addWar(War war) {
		if (!wars.contains(war)) {
			controller.informWarStart(war);
			wars.add(war);
		}
	}
	
	public void removeWar(War war) {
		controller.informWarEnd(war);
		wars.remove(war);
	}
	
	public War getWar(int id) {
		return wars.get(id);
	}
	
	public War getWarByAttackerID(int id) {
		for (int i = 0; i < wars.size(); ++i) {
			if (wars.get(i).attacker.id == id) {
				return wars.get(i);
			}
		}
		
		return null;
	}
	
	public int getCountWars() {
		return wars.size();
	}
	
	public War isEnemy(House house) {
		
		int depth = 0;
		if (depth > 100) {
			System.out.println("Overflow incoming");
		}
		
		for (int i = 0; i < wars.size(); ++i) {
			if (wars.get(i).areEnemies(this, house)) {
				return wars.get(i);
			}
		}
		
		if (getOverlord() != null) {
			return getOverlord().isEnemy(house, depth+1);
		}
		
		if (house.getOverlord() != null) {
			return house.getOverlord().isEnemy(this);
		}
		
		return null;
	}
	
	public War isEnemy(House house, int depth) {
		
		if (depth > 100) {
			System.out.println("Overflow incoming");
		}
		
		for (int i = 0; i < wars.size(); ++i) {
			if (wars.get(i).areEnemies(this, house)) {
				return wars.get(i);
			}
		}
		
		if (getOverlord() != null) {
			return getOverlord().isEnemy(house,depth+1);
		}
		
		if (house.getOverlord() != null) {
			return house.getOverlord().isEnemy(this);
		}
		
		return null;
	}
	
	public void makeEnemy(House house) {
		house.addStatModifier(new StatModifier("At War", House.RELATION_STAT, house, this, -1, War.RELATION_AT_WAR));
		this.addStatModifier(new StatModifier("At War", House.RELATION_STAT, this,  house, -1, War.RELATION_AT_WAR));
		house.endPacts(this);
		this.endPacts(house);
	}
	
	public void endPacts(House house) {
		
		if (this.getHouseStat(house, House.HAS_DIPLOMAT) == 1) {
			this.changeHouseStat(house, House.COURT_POWER, -DiplomatStatModifier.COURT_POWER);
		}
		
		if (house.getHouseStat(this, House.HAS_DIPLOMAT) == 1) {
			house.changeHouseStat(this, House.COURT_POWER, -DiplomatStatModifier.COURT_POWER);
		}
		
		house.removeStatModfifier("Diplomat", this);
		this.removeStatModfifier("Diplomat", house);
		house.removeStatModfifier("Truce", this);
		this.removeStatModfifier("Truce", house);
		house.removeStatModfifier("Trade Agreement", this);
		this.removeStatModfifier("Trade Agreement", house);
		
		if (this.getHouseStat(house, House.OFFERS_PROTECTION) == 1) {
			this.removeStatModfifier("PROTECTS", house);
			house.removeStatModfifier("PROTECTED", house);
			
		} else if (house.getHouseStat(this, House.OFFERS_PROTECTION) == 1){
			house.removeStatModfifier("PROTECTS", this);
			this.removeStatModfifier("PROTECTED", this);
		}
		
		controller.informRemoveAlly(house);
		
		house.removeStatModfifier("HAS_TRADE_AGREEMENT", this);
		this.removeStatModfifier("HAS_TRADE_AGREEMENT", house);
		house.removeStatModfifier("HAS_DEFENSIVE_PACT", this);
		this.removeStatModfifier("HAS_DEFENSIVE_PACT", house);
	}
	
	public int getTotalTroops() {
		return totalTroops;
	}
	
	public boolean isSubjectOf(House other) {
		if (getOverlord() == other) return true;
		if (other == this) return true;
		if (getOverlord() == null) return false;
		return getOverlord().isSubjectOf(other);
	}
	
	public boolean isIndependend() {
		return getOverlord() == null && this.getBaronies().size() > 0;
	}
	
	public House getSupremeOverlord() {
		return supremeOverlord;
	}
	
	public House getOverlord() {
		return overlord;
	}
	
	public boolean haveSameOverlordWith(House other) {
		House supOverlord1 = this.getSupremeOverlord();
		House supOverlord2 = other.getSupremeOverlord();
		
		return (supOverlord1 == supOverlord2);
	}
	
	public void changeGold(float gold) {
		this.gold.value += gold;
	}
	
	public float getGold() {
		return gold.value;
	}
	
	public float getHonor() {
		return honor;
	}
	
	public void changeHonor(float honor) {
		if (this.honor + Math.min(honor, this.honor) <= 0) {
			stats[TYRANNY] += -Math.min(honor, this.honor) / 12.f;
		}
		this.honor += honor;
		stats[HONORABLE] += honor / 3.f;
		updateReputation = true;
	}
	
	public float getRelation(House house) {
		float res = getHouseStat(house, RELATION_STAT);
		if (getOverlord() == house) {
			res += getLawRelation();
		}
		res += stats[House.DIPLOMATIC_REPUTATION];
		res += house.stats[House.DIPLOMATIC_REPUTATION];
		
		if (getRival() == house) {
			res += Rivalize.RELATION_HIT;
		}
		
		if (house.getRival() == this) {
			res += Rivalize.RELATION_HIT;
		}
		
		return res;
	}
	
	public float getLawRelation() {
		float res = getOverlord().lawSettings[LawSet.categories[1].laws[0].ID] * House.RELATION_HIT_TAXES;
		if (getOverlord().getBaronies().size() > 0) {
			res += getOverlord().lawSettings[LawSet.categories[1].laws[1].ID] * House.RELATION_HIT_ARMY;
		}
		
		return res;
	}
	

	public void changeRelation(House house, float value) {
		changeHouseStat(house, RELATION_STAT, value);
	}
	
	public void setHouseStat(House house, int stat, float value) {
		Stats stats = houseRelations.get(house.id);
		
		if (stats == null) {
			stats = new Stats();
			houseRelations.put(house.id, stats);
		}
		
		stats.setStat(stat, value);
	}
	
	public float getHouseStat(House house, int stat) {
		Stats stats = houseRelations.get(house.id);
		if (stats != null) return stats.getStat(stat);

		return 0;
	}
	
	public void changeHouseStat(House house, int stat, float value) {
		
		Stats stats = houseRelations.get(house.id);
		if (stats == null) {
			stats = new Stats();
			houseRelations.put(house.id, stats);
		}
		
		stats.setStat(stat, value + stats.getStat(stat));
	}
	
	public void changeFavor(House house, float value) {
		changeHouseStat(house, House.FAVOR_STAT, value);
		house.changeHouseStat(this, House.FAVOR_STAT, -value);
	}
	
	public boolean isOnCooldown() {
		return isOnCooldown;
	}
	
	public void isOnCooldown(boolean state) {
		this.isOnCooldown = state;
		
		if (state) {
			passedCooldown = DIPLO_COOLDOWN;
		}
	}
	
	public int hasWarReason(Holding holding) {
		for (int i = 0; i < justifications.size(); ++i) {
			if (justifications.get(i).getHolding() == holding) {
				return id;
			}
		} 
		
		for (int i = 0; i < subHouses.size(); ++i) {
			int reason = subHouses.get(i).hasWarReason(holding);
			if (reason != WarGoal.NO_REASON) {
				return reason;
			}
		}
		
		if (holding.getOwner().getSupremeOverlord().hasReputation("Tyrant")) {
			return WarGoal.LIBERATION;
		}
		
		return WarGoal.NO_REASON;
	}
	
	public boolean hasClaim(Holding holding) {
		for (int i = 0; i < justifications.size(); ++i) {
			if (justifications.get(i).getHolding() == holding) {
				return true;
			}
		} 
		
		return false;
	}
	
	public int getCountStatModifiers() {
		return statModifiers.size();
	}
	
	public StatModifier getStatModifier(int i) {
		return statModifiers.get(i);
	}
	
	public void addStatModifier(StatModifier modifier) {
		if (	World.getInstance().getHouses().size() <= modifier.target
			||  World.getInstance().getHouses().size() <= modifier.affected) return;
		if (	World.getInstance().getHouses().get(modifier.target).isNPCFaction()
			||  World.getInstance().getHouses().get(modifier.affected).isNPCFaction()) return;
		
		
		if (EmpireFrameListener.MAIN_FRAME.getNetwork().isHost()) {
			EmpireFrameListener.MAIN_FRAME.getNetwork().broadcast(new HouseStatChange(modifier, HouseStatChange.ADD));
		}
		
		for (int i = 0; i < statModifiers.size(); ++i) {
			StatModifier m = statModifiers.get(i);
			if (m.name.equals(modifier.name) && m.target == modifier.target) {
				m.timestampStart = World.getInstance().getWorldTime();
				m.finished = false;
				m.unapply();
				m.value += modifier.value;
				m.apply();
				return;
			}
		}
		
		statModifiers.add(modifier);
		statUpdater.addItem(modifier);
		modifier.apply();
	}
	
	public void removeStatModfifier(String name, House target) {
		if (	target.id >= World.getInstance().getHouses().size()
			||  id >= World.getInstance().getHouses().size()) return;
		if (	target.isNPCFaction()
			|| 	isNPCFaction()) return;
			
		
		
		for (int i = 0; i < statModifiers.size(); ++i) {
			StatModifier m = statModifiers.get(i);
			if (m.name.equals(name) && m.target == target.id) {
				
				if (EmpireFrameListener.MAIN_FRAME.getNetwork().isHost()) {
					EmpireFrameListener.MAIN_FRAME.getNetwork().broadcast(new HouseStatChange(m, HouseStatChange.REMOVE));
				}
				
				statModifiers.remove(i);
				statUpdater.removeItem(m);
				m.unapply();
			}
		}
	}
	
	public float getModifierValue(String name, House target) {
		float res = 0;
		for (int i = 0; i < statModifiers.size(); ++i) {
			if (statModifiers.get(i).name.equals(name) && statModifiers.get(i).target == target.id) {
				res += statModifiers.get(i).value;
			}
		}
		return res;
	}
	
	public StatModifier getStatModifier(String name) {
		for (int i = 0; i < statModifiers.size(); ++i) {
			if (statModifiers.get(i).name.equals(name) ) {
				return statModifiers.get(i);
			}
		}
		return null;
	}
	
	public StatModifier getStatModifier(String name, House target) {
		for (int i = 0; i < statModifiers.size(); ++i) {
			if (statModifiers.get(i).name.equals(name) && statModifiers.get(i).target == target.id ) {
				return statModifiers.get(i);
			}
		}
		return null;
	}
	
	public void addLoan(Loan loan) {
		loans.add(loan);
	}
	
	public void removeLoan(Loan loan) {
		for (int i = 0; i < loans.size(); ++i) {
			if (loans.get(i).taker == loan.taker && loans.get(i).giver == loan.giver) {
				loans.remove(i);
			}
		}
	}
	
	public Loan getLoan(House house) {
		for (int i = 0; i < loans.size(); ++i) {
			if (loans.get(i).taker == house || loans.get(i).giver == house) {
				return loans.get(i);
			}
		}
		
		return null;
	}
	
	public void endLoan(Loan loan) {
		loan.ended = true;
		
		Message message = new Message(new PayLoan(), this, loan.giver, null);
		message.response = -2;
		controller.informMessage(message);
	}
	
	public void addSpy(House target) {
		this.setHouseStat(target, House.HAS_SPY, 1);
		this.changeHouseStat(target, House.COURT_POWER, SPY_COURT_POWER);
	}
	
	public void removeSpy(House target) {
		this.setHouseStat(target, House.HAS_SPY, 0);
		this.changeHouseStat(target, House.COURT_POWER, -SPY_COURT_POWER);
	}
	
	public boolean hasSpy(House target) {
		return this.getHouseStat(target, House.HAS_SPY) != 0;
	}
	
	public int getCourtPower(House target) {
		float courtPower = this.getHouseStat(target, House.COURT_POWER);
		
		int countTargetHoldings = target.holdings.size();
		
		neighbours: for (int i = 0; i < holdings.size(); ++i) {
			Holding h = holdings.get(i);
			Barony[] baronies = World.getInstance().getMap().getNeighbours(h.holdingData.barony);
			
			for (int j = 0; j < countTargetHoldings; ++j) {
				Holding other = target.holdings.get(j);
				if (other.holdingData.barony == h.holdingData.barony) {
					courtPower += NEIGHBOUR_COURT_POWER;
					break neighbours;
				}
				for (int k = 0; k < baronies.length; ++k) {
					if (other.holdingData.barony == baronies[k]) {
						courtPower += NEIGHBOUR_COURT_POWER;
						break neighbours;
					}
				}
			}
		}
		
		if (this.haveSameOverlordWith(target)) {
			courtPower += SAME_DYNASTY_COURT_POWER;
		}
		
		return (int) courtPower;
	}
	
	public void addTaxedGold(float gold) {
		if (overlord != null) {
			float taxes = gold * overlord.stats[VASSAL_TAXES];
			changeGold(gold - taxes);
			overlord.changeGold(taxes);
		} else {
			changeGold(gold);
		}
	}
	
	public void changeReputation(int reputationStat, float value) {
		
		stats[reputationStat] += value;
		if (stats[reputationStat] < 0) {
			stats[reputationStat] = 0;
		} else if (stats[reputationStat] > Reputation.MAX_POINTS) {
			stats[reputationStat] = Reputation.MAX_POINTS;
		}
		
		updateReputation = true;
	}
	
	public boolean isActive(Reputation reputation) {
		for (int i = 0; i < activeReputations.length; ++i) {
			if (activeReputations[i] == reputation) {
				return true;
			}
		}
		
		return false;
	}
	
	public boolean isActive(String reputationName) {
		for (int i = 0; i < activeReputations.length; ++i) {
			if (activeReputations[i] != null && activeReputations[i].name.equals(reputationName)) {
				return true;
			}
		}
		
		return false;
	}
	
	public void startTechnologyProject(TechnologyProject p) {
		this.techProject = p;
	}
	
	public boolean hasResearched(Technology t) {
		return researched[t.ID];
	}
	
	public boolean canResearch(Technology t) {
		
		if (researched[t.ID]) return false;
		if (t.pre == null) return true;
		
		for (int i = 0; i < t.pre.length; ++i) {
			if (!researched[t.pre[i].ID]) {
				return false;
			}
		}
		
		return true;
	}
	
	public void rebuild() {
		neighbours = new LinkedHashSet<Barony>();
		updateBorders();
		controller.control(this);
	}

	public void setOverlord(House overlord) {
		if(this == overlord) {
			throw new RuntimeException("Error House::setOverlord: House " + overlord.getName() + " cannot be its own overlord!");
		}
		this.overlord = overlord;
	}
	
    private void writeObject(java.io.ObjectOutputStream stream) throws IOException {
    	stream.writeObject(gold);
    	stream.writeFloat(honor);
    	stream.writeFloat(fertility);
    	stream.writeFloat(accGrowth);
    	stream.writeFloat(passedLawTime);
    	stream.writeInt(males);
    	stream.writeInt(females);
    	stream.writeObject(name);
    	
    	stream.writeInt(houseRelations.size());
    	for (int i = 0; i < World.getInstance().getHouses().size(); ++i) {
    		if (houseRelations.containsKey(i)) {
    			stream.writeInt(i);
    			
    			Stats s = houseRelations.get(i);
    			int countStats = s.stats.size();
            	stream.writeInt(countStats);
        		for (int key : s.stats.keys()) {
        			stream.writeInt(key);
        			stream.writeFloat(s.stats.get(key));
        		}
    			
    		}
    	}
    	
    	stream.writeObject(stats);
    	stream.writeInt(holdingIDs.size());
    	for (int i = 0; i < holdingIDs.size(); ++i) {
    		stream.writeInt(holdingIDs.get(i));
    	}
    	
    	stream.writeObject(visibleBaronies);
    	stream.writeObject(visibleHouses);
    	
    	stream.writeBoolean(isOnCooldown);
    	stream.writeFloat(passedCooldown);
    	stream.writeBoolean(updateReputation);
    	
    	stream.writeInt(armies.size());
    	for (int i = 0; i < armies.size(); ++i) {
    		stream.writeShort(armies.get(i));
    	}
    	
    	stream.writeShort(id);

    	stream.writeObject(lawSettings);
    	stream.writeInt(maxHoldings);
 
    	stream.writeObject(pointsInc);
    	stream.writeObject(researched);
    	stream.writeInt(countTechs);
    	stream.writeObject(discovered);
    	stream.writeObject(enabledBuildings);
    	stream.writeObject(enabledUnits);
    	stream.writeObject(unitTypeMult);
    	stream.writeObject(buildingMult);
    	stream.writeObject(goodsMult);
    	stream.writeObject(visibleBaronies);
    	stream.writeObject(visibleHouses);
    }

    @SuppressWarnings("unchecked")
	private void readObject(java.io.ObjectInputStream stream) throws IOException, ClassNotFoundException {
    	
    	gold = (Valuef) stream.readObject();
    	honor = stream.readFloat();
    	fertility = stream.readFloat();
    	accGrowth = stream.readFloat();
    	passedLawTime = stream.readFloat();
    	males = stream.readInt();
    	females = stream.readInt();
    	name = (String) stream.readObject();
    	
    	houseRelations = new TIntObjectHashMap<Stats>();
    	int countHouseRelations = stream.readInt();
    	for (int i = 0; i < countHouseRelations; ++i) {
    		int houseID = stream.readInt();
    		Stats s = new Stats();
    		int countStats = stream.readInt();
    		for (int j = 0; j < countStats; ++j) {
    			int stat = stream.readInt();
    			float value = stream.readFloat();
    			s.stats.put(stat, value);
    		}
    		
    		houseRelations.put(houseID,  s);
    	}
    	
    	stats = (float[]) stream.readObject();
    	int countHoldingIDs = stream.readInt();
    	holdingIDs = new ArrayList<Integer>();
    	for (int i = 0; i < countHoldingIDs; ++i) {
    		holdingIDs.add(stream.readInt());
    	}
    	
    	visibleBaronies = (TIntArrayList) stream.readObject();
    	visibleHouses = (TIntArrayList) stream.readObject();
    	
    	isOnCooldown = stream.readBoolean();
    	passedCooldown = stream.readFloat();
    	updateReputation = stream.readBoolean();
    	
    	int countArmies = stream.readInt();
    	armies = new TShortArrayList();
    	for (int i = 0; i < countArmies; ++i) {
    		armies.add(stream.readShort());
    	}
    	
    	id = stream.readShort();

    	lawSettings = (byte[]) stream.readObject();
    	maxHoldings = stream.readInt();
    	
    	pointsInc = (float[]) stream.readObject();
    	researched = (boolean[]) stream.readObject();
    	countTechs = stream.readInt();
    	discovered = (boolean[]) stream.readObject();
    	enabledBuildings = (Map<String, boolean[]>) stream.readObject();
    	enabledUnits = (boolean[]) stream.readObject();
    	unitTypeMult = (float[]) stream.readObject();
    	buildingMult = (float[][]) stream.readObject();
    	goodsMult = (float[][]) stream.readObject();
    	visibleBaronies = (TIntArrayList) stream.readObject();
    	visibleHouses = (TIntArrayList) stream.readObject();
    }
    
	public void writeInternalsToStream(ObjectOutputStream stream) throws IOException {
    	stream.writeInt(justifications.size());
    	for (int i = 0; i < justifications.size(); ++i) {
    		stream.writeObject(justifications.get(i));
    	}
    	
    	stream.writeInt(statModifiers.size());
    	for (int i = 0; i < statModifiers.size(); ++i) {
    		stream.writeObject(statModifiers.get(i));
    	}
    	
    	stream.writeObject(statUpdater);
    	
    	stream.writeInt(loans.size());
    	for (int i = 0; i < loans.size(); ++i) {
    		stream.writeObject(loans.get(i));
    	}
    	
    	stream.writeObject(activeReputations);
    	stream.writeObject(intrigueProject);
    	stream.writeObject(techProject);
    	stream.writeObject(techs);
	}

	public void readInternalsFromStream(ObjectInputStream stream) throws IOException, ClassNotFoundException {
    	int countClaims = stream.readInt();
    	justifications = new ArrayList<WarJustification>();
    	for (int i = 0; i < countClaims; ++i) {
    		justifications.add((WarJustification)stream.readObject());
    	}
    	
    	int countStatModifiers = stream.readInt();
    	statModifiers = new ArrayList<StatModifier>();
    	for (int i = 0; i < countStatModifiers; ++i) {
    		statModifiers.add((StatModifier) stream.readObject());
    	}
    	
    	statUpdater = (Updater) stream.readObject();
    	
    	int countLoans = stream.readInt();
    	loans = new ArrayList<Loan>();
    	for (int i = 0; i < countLoans; ++i) {
    		loans.add((Loan) stream.readObject());
    	}
    	
    	activeReputations = (Reputation[]) stream.readObject();
    	intrigueProject = (IntrigueProject) stream.readObject();
    	techProject = (TechnologyProject) stream.readObject();
    	techs = (Technology[]) stream.readObject();
	}

	public boolean hasMarriage(House other) {
		return this.getHouseStat(other, House.HAS_MARRIAGE) != 0 ;
	}

	public boolean hasDiplomaticRelation(House other) {
		return this.getHouseStat(other, House.HAS_DIPLOMAT) != 0 || other.getHouseStat(this, House.HAS_DIPLOMAT) != 0 ;
	}

	public boolean hasDefensivePact(House other) {
		return this.getHouseStat(other, House.HAS_DEFENSIVE_PACT) != 0;
	}

	public boolean hasTradeAgreement(House other) {
		return this.getHouseStat(other, House.HAS_TRADE_AGREEMENT) != 0;
	}

	public String getAffectorString(int stat) {
		String res = "";
		for (int i = 0; i < this.statModifiers.size(); ++i) {
			if (statModifiers.get(i).stat == stat && statModifiers.get(i).value != 0) {
				String sign = "+";
				if (statModifiers.get(i).value < 0) {
					sign = "";
				}
				
				res += Util.getFlaggedText(	sign 
											+ Math.round(statModifiers.get(i).value)
											+ " (" 
											+ statModifiers.get(i).name + ")", statModifiers.get(i).value >= 0);
				
				if (statModifiers.get(i) instanceof VaryingStatModifier) {
					VaryingStatModifier mod = (VaryingStatModifier) statModifiers.get(i);
					
					sign = "+";
					if (mod.changeSpeed < 0) {
						sign = "";
					}
					
					res += " " + sign +  ((int)(mod.changeSpeed*World.SECONDS_PER_DAY*World.DAYS_PER_YEAR*10)/10.f) + "/Year";
				}
				
				res += "\n";
			}
		}
		
		if (stat == HONORABLE) {
			res += Util.getFlaggedText(	Util.getSignedText((int)(honor / 3.f))
										+ " (Honor)", 
										honor >= 0) + "\n";
		}
		
		if (stat == TYRANNY) {
			int lawTyranny = 0;
			for (int i = 0; i < Law.COUNT_LAWS; ++i) {
				Law law = LawSet.getLaw(i);
				if (law.options[lawSettings[i]].isTyrannical) {
					lawTyranny += LAW_TYRANNY;
				}
			}
			
			if (lawTyranny != 0) {
				res += Util.getFlaggedText(	"+" + lawTyranny + " (Laws)", true) + "\n";
			}
			
			if (honor < 0) {
				res += Util.getFlaggedText(	Util.getSignedText((int)(-honor / 12.f))
											+ " (Honor)", 
											true) + "\n";
			}
		}
		
		return res;
	}

	public TIntArrayList getExploredHouses() {
		return visibleHouses;
	}
	
	public float getFertility() {
		return fertility;
	}
	
	public void startMarriage(House house) {
		addStatModifier(new MarriageStatModifier(this, house));
		addStatModifier(new StatModifier("Married", House.RELATION_STAT, this, house, Marriage.RELATION_DURATION, Marriage.RELATION_MARRIAGE));
	}
	
	public Technology getTechnology(int index) {
		return techs[index];
	}
	
	public int getCountTechnologies() {
		return countTechs;
	}

	public void research(Technology technology) {
		
		if (!researched[technology.ID]) {
			researched[technology.ID] = true;
			techs[countTechs++] = technology;
			
			if (techProject != null) {
				if (techProject.tech == technology) {
					techProject.abort();
				}
			}
		}
	}

	public boolean isProtector(House other) {
		return this.getHouseStat(other, House.OFFERS_PROTECTION) != 0;
	}
	
	public String toString() {
		return name;
	}

	public boolean hasReputation(String name) {
		for (int i = 0; i < activeReputations.length; ++i) {
			if (activeReputations[i] != null && activeReputations[i].name.equals(name)) {
				return true;
			}
		}
		return false;
	}

	public float getAccGrowth() {
		return accGrowth;
	}

	public void setResearch(float research) {
		this.research = research;
	}
	
	public void setIsNPCFaction(boolean state) {
		this.npcFaction = state;
	}
	
	public boolean isNPCFaction() {
		return npcFaction;
	}

	public void addJustification(WarJustification justification) {
		justifications.add(justification);
		
		if (EmpireFrameListener.MAIN_FRAME.getNetwork().isHost()) {
			EmpireFrameListener.MAIN_FRAME.getNetwork().broadcast(new ChangeJustification(justification, id, ChangeJustification.ADD));
		}
		
	}
	
	public void removeJustification(WarJustification justification) {
		justifications.remove(justification);
		
		if (EmpireFrameListener.MAIN_FRAME.getNetwork().isHost()) {
			EmpireFrameListener.MAIN_FRAME.getNetwork().broadcast(new ChangeJustification(justification, id, ChangeJustification.REMOVE));
		}
	}
	
	public void removeJustification(int index) {
		WarJustification wj = justifications.remove(index);
		justifications.remove(index);
		
		if (EmpireFrameListener.MAIN_FRAME.getNetwork().isHost()) {
			EmpireFrameListener.MAIN_FRAME.getNetwork().broadcast(new ChangeJustification(wj, id, ChangeJustification.REMOVE));
		}
	}
	
	public WarJustification getJustification(int index) {
		return justifications.get(index);
	}
	
	public int getCountJustfications() {
		return justifications.size();
	}

	public int getIndexOf(WarJustification justification) {
		return justifications.indexOf(justification);
	}

	public List<WarJustification> getJustifications() {
		return justifications;
	}
	
	public float getInfluenced(House house) {
		float influence = getHouseStat(house, INFLUENCED);
		if (house.houseNeighbours.contains(this) || this == house) {
			float holdingInfluence = house.getAllHoldings().size() * INFLUENCE_PER_HOLDING;
			if (house != this) {
				holdingInfluence /= 5;
			}
			influence += holdingInfluence;
		}
		
		if (house.hasMarriage(this)) influence += INFLUENCE_PER_SPECIAL;
		if (house.hasSpy(this)) influence += INFLUENCE_PER_SPECIAL;
		if (house.hasDiplomaticRelation(this)) influence += INFLUENCE_PER_SPECIAL;
		if (house.hasTradeAgreement(this)) influence += INFLUENCE_PER_SPECIAL;
		if (this == house) influence += BASE_SELF_INFLUENCE;
		if (house.isProtector(this)) influence +=INFLUENCE_PROTECT;
		
		influence += house.getHouseStat(this, House.FAVOR_STAT) * INFLUENCE_PER_FAVOR;
		
		return influence;
	}
	
	public void changeInfluenced(float value, House house) {
		changeHouseStat(house, INFLUENCED, value);
		updateHegemon();
	}
	
	public void updateHegemon() {
		
		if (this.isRebel()) return;
		
		House prevHegemon = hegemon;
		if (hegemon == null) {
			hegemon = this;
		}
		
		if (!this.isIndependend()) {
			if (hegemon != this && hegemon != null) {
				hegemon = this;
				this.updateBorders();
			}
			return;
		}
		
		float value = getInfluenced(hegemon);
		
		for (int i = 0; i < houseNeighbours.size(); ++i) {
			House h = houseNeighbours.get(i);
			if (h.isIndependend()) {
				float influenced = getInfluenced(h);
				if (influenced > value) {
					hegemon = h;
					value = influenced;
				}
			}
		}
		
		if (prevHegemon != hegemon) {
			updateBorders();
			hegemon.updateBorders();
		}
		
	}
	

	public House getHegemon() {
		if (hegemon == null) {
			updateHegemon();
		}
		return hegemon;
	}
	
	public boolean isInSphereOfInfluence(House house) {
		return getHegemon() == house || house.getHegemon() == this;
	}
	
	public boolean isUninfluenced() {
		return isInSphereOfInfluence(this) && isIndependend();
	}

	public boolean hasSameHegemon(House receiver) {
		return getHegemon() == receiver.getHegemon();
	}

	public boolean shareSphereOfInfluence(House other) {
		return this.isInSphereOfInfluence(other) || other.isInSphereOfInfluence(this);
	}
	
	public int getTotalCountRoads() {
		int res = 0;
		for (int i = 0; i < holdings.size(); ++i) {
			res += holdings.get(i).getCountRoads();
		}
		return res;
	}

	public int getCountSubHoldings() {
		int res = 0;
		for (int i = 0; i < baronies.size(); ++i) {
			res += baronies.get(i).getCountSubHoldings();
		}
		res -= baronies.size();
		return res;
	}

	public House getRival() {
		if (stats[RIVAL_ID] != 0) {
			return World.getInstance().getHouses().get((int)stats[RIVAL_ID]-1);
		} else {
			return null;
		}
	}
	
	public void setRival(House house) {
		stats[RIVAL_ID] = house.id+1;
	}
}
