package com.tyrfing.games.id18.model.ai;

import java.util.Collections;
import java.util.List;

import com.tyrfing.games.id18.model.battle.Battle;
import com.tyrfing.games.id18.model.unit.Faction;
import com.tyrfing.games.id18.model.unit.StatType;
import com.tyrfing.games.id18.model.unit.Unit;
import com.tyrfing.games.tyrlib3.model.ai.IHeuristic;

public class Heuristic implements IHeuristic {
	
	private Faction faction;
	private Battle battle;
	
	public Heuristic(Battle battle, Faction faction) {
		this.faction = faction;
		this.battle = battle;
	}
	
	@Override
	public float getEvaluation(int depth) {
		if (battle.areObjectivesAchieved(faction)) {
			// This is a winning state, we should definitely take it,
			// only consideration is that we should take it 
			return 1 + 1f / depth;
		} 
		
		float evaluation = 0;
		float maxEvaluation = 0;
		
		List<Faction> superFactions = faction.getSuperFactions();
		for (Unit unit : battle.getWaitingUnits()) {
			int hp = unit.getStats().get(StatType.HP);
			List<Faction> allUnitSuperFactions = unit.getFaction().getSuperFactions();
			if (!Collections.disjoint(superFactions, allUnitSuperFactions)) {
				evaluation += hp;
			} 
			maxEvaluation += hp;
		}
		
		return evaluation / maxEvaluation;
	}
	
	public Faction getFaction() {
		return faction;
	}
}
