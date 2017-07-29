package com.tyrfing.games.id18.model.battle;

import java.util.ArrayList;
import java.util.List;

import com.tyrfing.games.id18.model.unit.Faction;
import com.tyrfing.games.id18.model.unit.Unit;

public class DestroyEnemyObjective implements IObjective {

	/**
	 * 
	 */
	private static final long serialVersionUID = 2234911601266262155L;
	private Faction faction;
	private List<Faction> enemyFactions;
	
	public DestroyEnemyObjective(Faction faction) {
		this.faction = faction;
		this.enemyFactions = new ArrayList<Faction>();
	}
	
	@Override
	public Faction getFaction() {
		return faction;
	}
	
	public List<Faction> getEnemyFactions() {
		return enemyFactions;
	}
	
	@Override
	public boolean isAchieved(Battle battle) {
		for (Unit unit : battle.getWaitingUnits()) {
			if (enemyFactions.contains(unit.getFaction())) {
				return false;
			}
		}
		
		return true;
	}

}
