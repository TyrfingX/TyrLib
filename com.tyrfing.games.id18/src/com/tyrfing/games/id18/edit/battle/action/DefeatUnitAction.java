package com.tyrfing.games.id18.edit.battle.action;

import com.tyrfing.games.id18.model.battle.Battle;
import com.tyrfing.games.id18.model.unit.Unit;
import com.tyrfing.games.tyrlib3.edit.action.AAction;

public class DefeatUnitAction extends AAction {
	
	private Battle battle;
	private Unit unit;
	
	private int previousQueuePosition;
	
	public DefeatUnitAction(Battle battle, Unit unit) {
		this.battle = battle;
		this.unit = unit;
	}

	@Override
	public void execute() {
		previousQueuePosition = battle.getWaitingUnits().indexOf(unit);
		
		battle.getWaitingUnits().remove(unit);
		battle.getField().getObjects().remove(unit);
	}

	@Override
	public void undo() {
		battle.getWaitingUnits().add(previousQueuePosition, unit);
		battle.getField().getObjects().add(unit);
	}

}
