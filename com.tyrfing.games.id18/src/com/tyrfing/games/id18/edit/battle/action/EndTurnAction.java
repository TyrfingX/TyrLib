package com.tyrfing.games.id18.edit.battle.action;

import com.tyrfing.games.id18.model.battle.Battle;
import com.tyrfing.games.id18.model.unit.StatType;
import com.tyrfing.games.id18.model.unit.Unit;
import com.tyrfing.games.tyrlib3.edit.action.AAction;

public class EndTurnAction extends AAction {

	private Battle battle;
	private Unit currentUnit;
	private int previousRemainingMovePoints;
	private int previousRemainingActionPoints;
	
	public EndTurnAction(Battle battle) {
		this.battle = battle;
	}

	@Override
	public void execute() {
		currentUnit = battle.getWaitingUnits().remove(0);
		battle.getWaitingUnits().add(currentUnit);
		Unit nextUnit = battle.getCurrentUnit();
		
		previousRemainingMovePoints = currentUnit.getStats().get(StatType.REMAINING_MOVE);
		previousRemainingActionPoints = currentUnit.getStats().get(StatType.REMAINING_ACTIONS);
		nextUnit.startTurn();
	}

	@Override
	public void undo() {
		battle.getWaitingUnits().remove(currentUnit);
		battle.getWaitingUnits().add(0, currentUnit);
		
		currentUnit.getStats().put(StatType.REMAINING_MOVE, previousRemainingMovePoints);
		currentUnit.getStats().put(StatType.REMAINING_ACTIONS, previousRemainingActionPoints);
	}

	@Override
	public String toString() {
		return "EndTurnAction";
	}
}
