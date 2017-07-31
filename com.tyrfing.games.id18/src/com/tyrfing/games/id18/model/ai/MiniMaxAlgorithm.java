package com.tyrfing.games.id18.model.ai;

import java.util.ArrayList;
import java.util.List;

import com.tyrfing.games.id18.edit.battle.action.EndTurnAction;
import com.tyrfing.games.id18.edit.unit.action.ApplyAffectorAction;
import com.tyrfing.games.id18.edit.unit.action.MoveAction;
import com.tyrfing.games.id18.model.battle.Battle;
import com.tyrfing.games.id18.model.field.Field;
import com.tyrfing.games.id18.model.unit.Arte;
import com.tyrfing.games.id18.model.unit.Faction;
import com.tyrfing.games.id18.model.unit.Unit;
import com.tyrfing.games.tyrlib3.edit.ActionStack;
import com.tyrfing.games.tyrlib3.edit.action.IAction;
import com.tyrfing.games.tyrlib3.model.ai.AMiniMaxAlgorithm;
import com.tyrfing.games.tyrlib3.model.math.Vector2I;

public class MiniMaxAlgorithm extends AMiniMaxAlgorithm {
	
	private Battle battle;
	private Field field;
	private Faction faction;
	
	public MiniMaxAlgorithm(Battle battle, ActionStack actionStackWithListeners, Heuristic heuristic, int maxDepth) {
		super(actionStackWithListeners, heuristic, maxDepth);
		this.battle = battle;
		this.field = battle.getField();
		this.faction = heuristic.getFaction();
	}

	@Override
	protected List<IAction> generateActions() {
		Unit unit = battle.getCurrentUnit();
		List<IAction> actions = new ArrayList<IAction>();
		
		for (Arte arte : unit.getArtes()) {
			for (int x = 0;  x < field.getTileGrid().getWidth(); ++x) {
				for (int y = 0;  y < field.getTileGrid().getHeight(); ++y) {
					Vector2I target = new Vector2I(x, y);
					ApplyAffectorAction affectorAction = new ApplyAffectorAction(unit, arte, target, true);
					
					if (affectorAction.canExecute()) {
						actions.add(affectorAction);
					}
				}
			}
		}
		
		for (Vector2I unitVector : Vector2I.UNIT_VECTORS) {
			Vector2I targetPosition = unit.getFieldPosition().add(unitVector);
			MoveAction moveAction = new MoveAction(unit, targetPosition, true);
			
			if (moveAction.canExecute()) {
				actions.add(moveAction);
			}
		}
		
		EndTurnAction endTurnAction = new EndTurnAction(battle);
		actions.add(endTurnAction);
		
		return actions;
	}

	@Override
	protected boolean isTerminalState() {
		return battle.areObjectivesAchieved(this.faction);
	}

	@Override
	protected boolean isMaxNode() {
		return battle.getCurrentUnit().getFaction().equals(this.faction);
	}
	
	public Battle getBattle() {
		return battle;
	}
}
