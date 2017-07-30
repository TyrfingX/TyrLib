package com.tyrfing.games.id17.war;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class Skirmish implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = -4787789579017946927L;
	
	public List<Regiment> attackers = new ArrayList<Regiment>();
	public List<Regiment> defenders = new ArrayList<Regiment>();
	
	public Army attackerArmy;
	public Army defenderArmy;
	
	public Battle battle;
	
	public boolean counterDamage = true;
	
	private boolean finished = false;
	
	public void calcDmg(float time) {
		
		if (finished) return;
		
		int totalAttack = 0;
		int totalTroopsAttack = 0;
		
		int totalCounterattack = 0;
		int totalDefense = 0;
		int totalTroopsDefense = 0;
		int totalCounterattackDefense = 0;
		
		for (int i = 0; i < defenders.size(); ++i) {
			Regiment r = defenders.get(i);
			totalTroopsDefense += r.troops;
		}
		
		for (int i = 0; i < attackers.size(); ++i) {
			Regiment r = attackers.get(i);
			totalTroopsAttack += r.troops;
		}
		
		for (int i = 0; i < defenders.size(); ++i) {
			Regiment r = defenders.get(i);
			float typeMult = defenderArmy.getTypeMult(r.unitType.ordinal())+defenderArmy.getOwner().unitTypeMult[r.unitType.ordinal()];
			totalDefense += UnitType.UNIT_STATS.get(r.unitType).getStat(UnitType.DEFENSE) * r.troops * typeMult / totalTroopsDefense;
			totalCounterattack += UnitType.UNIT_STATS.get(r.unitType).getStat(UnitType.COUNTERATTACK) * r.troops / totalTroopsDefense;
		}
		
		for (int i = 0; i < attackers.size(); ++i) {
			Regiment r = attackers.get(i);
			float typeMult = attackerArmy.getTypeMult(r.unitType.ordinal())+attackerArmy.getOwner().unitTypeMult[r.unitType.ordinal()];
			totalCounterattackDefense += UnitType.UNIT_STATS.get(r.unitType).getStat(UnitType.DEFENSE) * r.troops / totalTroopsAttack;
			totalAttack += UnitType.UNIT_STATS.get(r.unitType).getStat(UnitType.ATTACK) * r.troops * typeMult / totalTroopsAttack;
		}
		
		float affinityFactor = UnitType.UNIT_AFFINITIES[attackers.get(0).unitType.ordinal()][defenders.get(0).unitType.ordinal()];
		
		
		float attackerTechFactor = attackerArmy.getAtkMult() / defenderArmy.getDefMult();
		float defenderTechFactor = defenderArmy.getAtkMult() / attackerArmy.getDefMult();
		
		float moralFactor = (0.5f + attackerArmy.moral / 2) / (0.5f + defenderArmy.moral / 2);
		float troopFactor = (float) totalTroopsAttack / totalTroopsDefense;
		float attackFactor = (affinityFactor * totalAttack) / totalDefense;
		float counterattackFactor = (float) totalCounterattack / (totalCounterattackDefense/2.f);
		float dmgDefenders = time * troopFactor * attackFactor * 4 * moralFactor * attackerTechFactor;
		float dmgAttackers = time * (1/troopFactor) * counterattackFactor * (1/moralFactor) * defenderTechFactor;
		
		if (counterDamage) {
			attackerArmy.receiveDamage(0.5f * Math.min(dmgAttackers, totalTroopsAttack) / (attackerArmy.getRegimentAverage()+1));
		}
		
		defenderArmy.receiveDamage(Math.min(dmgDefenders, totalTroopsDefense) / (defenderArmy.getRegimentAverage()+1));
		
		if (counterDamage) {
			for (int i = 0; i < attackers.size(); ++i) {
				Regiment r = attackers.get(i);
				attackerArmy.changeTroops(r.formationPos, -dmgAttackers);
			}
		}
		
		
		for (int i = 0; i < defenders.size(); ++i) {
			Regiment r = defenders.get(i);
			defenderArmy.changeTroops(r.formationPos, -dmgDefenders);
		}
		
		
		if (attackerArmy.moral < 0) {
			attackerArmy.moral = 0;
		}
		
		if (defenderArmy.moral < 0) {
			defenderArmy.moral = 0;
		}
		
		for (int i = 0; i < attackers.size(); ++i) {
			Regiment r = attackers.get(i);
			if (r.unitType == UnitType.Walls && r.troops <= 0) {
				attackerArmy.changeTroops(r.formationPos, 1);
			}
		}
	
		for (int i = 0; i < defenders.size(); ++i) {
			Regiment r = defenders.get(i);
			if (r.unitType == UnitType.Walls && r.troops <= 0) {
				defenderArmy.changeTroops(r.formationPos, 1);
			}
		}
		
		if (totalTroopsAttack <= dmgAttackers || totalTroopsDefense <= dmgDefenders) {
			finished = true;
		} 
		
		
	}
}
