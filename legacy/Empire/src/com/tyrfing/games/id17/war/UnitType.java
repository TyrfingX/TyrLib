package com.tyrfing.games.id17.war;

import java.util.HashMap;
import java.util.Map;

import com.tyrfing.games.id17.gui.mails.Mail;
import com.tyrfing.games.id17.gui.war.BuildUnitGUI;
import com.tyrfing.games.id17.trade.Horse;
import com.tyrfing.games.id17.trade.Iron;
import com.tyrfing.games.id17.trade.Stone;
import com.tyrfing.games.id17.trade.Wood;
import com.tyrfing.games.id17.world.World;
import com.tyrlib2.game.ILink;
import com.tyrlib2.game.LinkManager;
import com.tyrlib2.game.Stats;

public enum UnitType {
	Swordmen, Pikemen, Archers, Axemen, Cavalry, Guardians, Walls; //, Catapults, Knights;
	
	public static final Map<UnitType, Stats> UNIT_STATS = new HashMap<UnitType, Stats>();
	public static final float[][] UNIT_AFFINITIES = new float[UnitType.values().length][UnitType.values().length];
	
	public static final int SPEED = 1;
	public static final int ATTACK = 2;
	public static final int DEFENSE = 3;
	public static final int COUNTERATTACK = 4;
	public static final int BASE_COST = 5;
	public static final int BASE_BUILD_TIME = 6;
	public static final int COST_INC = 7;
	public static final int BUILD_TIME_INC = 8;
	public static final int GOOD_DEMAND_1_ID = 9;
	public static final int GOOD_DEMAND_1_AMOUNT = 10;
	public static final int GOOD_DEMAND_2_ID = 11;
	public static final int GOOD_DEMAND_2_AMOUNT = 12;
	
	public final static String DESC[] = {
			"Strengths: <link=Axemen>Axemen\\l\nWeaknesses: None\nDemands: 2 <link=Iron>Iron\\l<img GOODS Iron>",
			"Special: Backrow support\nStrengths: <link=Cavalry>Cavalry\\l\nWeaknesses: None\nDemands: 2 <link=Iron>Iron\\l<img GOODS Iron>",
			"Special: Ranged\nStrengths: None\nWeaknesses: <link=Guardians>Guardians\\l\nDemands: 2 <link=Wood>Wood\\l<img GOODS Wood>",
			"Strengths: <link=Guardians>Guardians\\l\nWeaknesses: <link=Swordmen>Swordmen\\l\nDemands: 1 <link=Wood>Wood\\l<img GOODS Wood> and 1 <link=Iron>Iron\\l<img GOODS Iron>",
			"Special: Flanking, Can't Siege\nStrengths: None\nWeaknesses: <link=Pikemen>Pikemen\\l\nDemands: 2 <link=Horse>Horse\\l<img GOODS Horse>",
			"Special: Protect adjacent units\nStrengths: <link=Archers>Archers\\l\nWeaknesses: <link=Axemen>Axemen\\l\nDemands: 1 <link=Wood>Wood\\l<img GOODS Wood> and 1 <link=Iron>Iron\\l<img GOODS Iron>",
			""
	};
	
	static {
		Stats swordmen = new Stats();
		swordmen.setStat(SPEED, 10.f);
		swordmen.setStat(ATTACK, 65.f);
		swordmen.setStat(DEFENSE, 10.f);
		swordmen.setStat(COUNTERATTACK, 28.f);
		swordmen.setStat(BASE_COST, 50.f);
		swordmen.setStat(BASE_BUILD_TIME, 10000.f);
		swordmen.setStat(COST_INC, 300.f);
		swordmen.setStat(BUILD_TIME_INC, 20000.f);
		swordmen.setStat(GOOD_DEMAND_1_ID, Iron.ID+1);
		swordmen.setStat(GOOD_DEMAND_1_AMOUNT, 2);
		UNIT_STATS.put(UnitType.Swordmen, swordmen);
		
		Stats pikemen = new Stats();
		pikemen.setStat(SPEED, 10.f);
		pikemen.setStat(ATTACK, 30.f);
		pikemen.setStat(DEFENSE, 10.f);
		pikemen.setStat(COUNTERATTACK, 65.f);
		pikemen.setStat(BASE_COST, 75.f);
		pikemen.setStat(BASE_BUILD_TIME, 20000.f);
		pikemen.setStat(COST_INC, 150.f);
		pikemen.setStat(BUILD_TIME_INC, 10000.f);
		pikemen.setStat(GOOD_DEMAND_1_ID, Iron.ID+1);
		pikemen.setStat(GOOD_DEMAND_1_AMOUNT, 2);
		UNIT_STATS.put(UnitType.Pikemen, pikemen);
		
		Stats archers = new Stats();
		archers.setStat(SPEED, 10.f);
		archers.setStat(ATTACK, 70.f);
		archers.setStat(DEFENSE, 2.f);
		archers.setStat(COUNTERATTACK, 0.f);
		archers.setStat(BASE_COST, 50.f);
		archers.setStat(BASE_BUILD_TIME, 20000.f);
		archers.setStat(COST_INC, 150.f);
		archers.setStat(BUILD_TIME_INC, 10000.f);
		archers.setStat(GOOD_DEMAND_1_ID, Wood.ID+1);
		archers.setStat(GOOD_DEMAND_1_AMOUNT, 2);
		UNIT_STATS.put(UnitType.Archers, archers);
		
		Stats axemen = new Stats();
		axemen.setStat(SPEED, 30.f);
		axemen.setStat(ATTACK, 90.f);
		axemen.setStat(DEFENSE, 5.f);
		axemen.setStat(COUNTERATTACK, 15.f);
		axemen.setStat(BASE_COST, 150.f);
		axemen.setStat(BASE_BUILD_TIME, 10000.f);
		axemen.setStat(COST_INC, 250.f);
		axemen.setStat(BUILD_TIME_INC, 20000.f);
		axemen.setStat(GOOD_DEMAND_1_ID, Iron.ID+1);
		axemen.setStat(GOOD_DEMAND_1_AMOUNT, 1);
		axemen.setStat(GOOD_DEMAND_2_ID, Wood.ID+1);
		axemen.setStat(GOOD_DEMAND_2_AMOUNT, 1);
		UNIT_STATS.put(UnitType.Axemen, axemen);
		
		Stats cavalry = new Stats();
		cavalry.setStat(SPEED, 50.f);
		cavalry.setStat(ATTACK, 130.f);
		cavalry.setStat(DEFENSE, 10.f);
		cavalry.setStat(COUNTERATTACK, 20.f);
		cavalry.setStat(BASE_COST, 200.f);
		cavalry.setStat(BASE_BUILD_TIME, 10000.f);
		cavalry.setStat(COST_INC, 300.f);
		cavalry.setStat(BUILD_TIME_INC, 20000.f);
		cavalry.setStat(GOOD_DEMAND_1_ID, Horse.ID+1);
		cavalry.setStat(GOOD_DEMAND_1_AMOUNT, 2);
		UNIT_STATS.put(UnitType.Cavalry, cavalry);
		
		Stats guardians = new Stats();
		guardians.setStat(SPEED, 5.f);
		guardians.setStat(ATTACK, 20.f);
		guardians.setStat(DEFENSE, 70.f);
		guardians.setStat(COUNTERATTACK, 2.f);
		guardians.setStat(BASE_COST, 50.f);
		guardians.setStat(BASE_BUILD_TIME, 10000.f);
		guardians.setStat(COST_INC, 150.f);
		guardians.setStat(BUILD_TIME_INC, 20000.f);
		guardians.setStat(GOOD_DEMAND_1_ID, Iron.ID+1);
		guardians.setStat(GOOD_DEMAND_1_AMOUNT, 1);
		guardians.setStat(GOOD_DEMAND_2_ID, Wood.ID+1);
		guardians.setStat(GOOD_DEMAND_2_AMOUNT, 1);
		UNIT_STATS.put(UnitType.Guardians, guardians);
		
		Stats walls = new Stats();
		walls.setStat(SPEED, 5.f);
		walls.setStat(ATTACK, 0.f);
		walls.setStat(DEFENSE, 5.f);
		walls.setStat(COUNTERATTACK, 0.f);
		walls.setStat(BASE_COST, 1000.f);
		walls.setStat(BASE_BUILD_TIME, 10000.f);
		walls.setStat(COST_INC, 300.f);
		walls.setStat(BUILD_TIME_INC, 40000.f);
		walls.setStat(GOOD_DEMAND_1_ID, Stone.ID+1);
		walls.setStat(GOOD_DEMAND_1_AMOUNT, 5);
		UNIT_STATS.put(UnitType.Walls, walls);
		
		int countUnitTypes = UnitType.values().length;
		
		for (int i = 0; i < countUnitTypes; ++i) {
			for (int j = 0; j < countUnitTypes; ++j) {
				UNIT_AFFINITIES[i][j] = 1;
			}	
			
			final UnitType type = UnitType.values()[i];
			LinkManager.getInstance().registerLink(new ILink() {
				@Override
				public void onCall() {
					Mail back = World.getInstance().getMainGUI().mailboxGUI.getCurrentMail();
					Mail mail = BuildUnitGUI.createUnitMail(type);
					if (back != null) {
						mail.setBackMail(back);
					}
				}
			}, type.toString());
		}
		
		UNIT_AFFINITIES[UnitType.Axemen.ordinal()][UnitType.Guardians.ordinal()] = 2;
		UNIT_AFFINITIES[UnitType.Swordmen.ordinal()][UnitType.Axemen.ordinal()] = 2;
		UNIT_AFFINITIES[UnitType.Archers.ordinal()][UnitType.Guardians.ordinal()] = 0.5f;
		UNIT_AFFINITIES[UnitType.Pikemen.ordinal()][UnitType.Cavalry.ordinal()] = 5f;
	}
	
	public boolean isMeleeAttacker() {
		return this != Archers && this != Walls;
	}
	
	public boolean isGuarding() {
		return this == Guardians || this == Walls;
	}
	
	public static float getPrice(UnitType type, int level) {
		Stats stats = UNIT_STATS.get(type);
		return stats.getStat(BASE_COST) + stats.getStat(COST_INC) * level;
	}
	
	public static float getProd(UnitType type, int level) {
		Stats stats = UNIT_STATS.get(type);
		return stats.getStat(BASE_BUILD_TIME) + stats.getStat(BUILD_TIME_INC) * level * level;
	}

	public static String getDesc(UnitType type, Army army) {
		String desc = "";
		float atk = UNIT_STATS.get(type).getStat(ATTACK)*(army.getTypeMult(type.ordinal())+army.getOwner().unitTypeMult[type.ordinal()])*army.getAtkMult();
		desc += "<link=Attack (ATK)>ATK\\l: " + (int) atk + "\n";
		float def = UNIT_STATS.get(type).getStat(DEFENSE)*(army.getTypeMult(type.ordinal())+army.getOwner().unitTypeMult[type.ordinal()])*army.getDefMult();
		desc += "<link=Defense (DEF)>DEF\\l: " + (int) def + "\n";
		float ctr = UNIT_STATS.get(type).getStat(COUNTERATTACK)*(army.getTypeMult(type.ordinal())+army.getOwner().unitTypeMult[type.ordinal()]);
		desc += "<link=Counter (CTR)>CTR\\l: " + (int) ctr + "\n\n";
		desc += DESC[type.ordinal()];
		return desc;
	}
	
	public static String getBaseDesc(UnitType type) {
		String desc = "";
		float atk = UNIT_STATS.get(type).getStat(ATTACK);
		desc += "Base <link=Attack (ATK)>ATK\\l: " + (int) atk + "\n";
		float def = UNIT_STATS.get(type).getStat(DEFENSE);
		desc += "Base <link=Defense (DEF)>DEF\\l: " + (int) def + "\n";
		float ctr = UNIT_STATS.get(type).getStat(COUNTERATTACK);
		desc += "Base <link=Counter (CTR)>CTR\\l: " + (int) ctr + "\n\n";
		desc += DESC[type.ordinal()];
		return desc;
	}
}
