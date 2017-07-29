package com.tyrfing.games.id18.edit.battle.action;

import com.tyrfing.games.id18.model.battle.Battle;
import com.tyrfing.games.id18.model.unit.Unit;
import com.tyrfing.games.tyrlib3.edit.action.AAction;
import com.tyrfing.games.tyrlib3.math.Vector2I;

public class DeployAction extends AAction {

	/**
	 * 
	 */
	private static final long serialVersionUID = 8097285052192079377L;
	private Battle battle;
	private Unit unit;
	private Vector2I position;
	private Vector2I orientation;
	
	public DeployAction(Battle battle, Unit unit, Vector2I position, Vector2I orientation) {
		this.battle = battle;
		this.unit = unit;
		this.position = position;
		this.orientation = orientation;
	}
	
	@Override
	public void execute() {
		unit.deploy(battle, position, orientation);
	}

	@Override
	public void undo() {
		battle.getWaitingUnits().remove(unit);
		battle.getField().getObjects().remove(unit);
		unit.setDeployedField(null);
	}

}
