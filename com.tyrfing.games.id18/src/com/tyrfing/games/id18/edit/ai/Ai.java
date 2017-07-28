package com.tyrfing.games.id18.edit.ai;

import java.util.ArrayList;
import java.util.List;

import com.tyrfing.games.id18.edit.battle.BattleDomain;
import com.tyrfing.games.id18.edit.battle.BattleFactory;
import com.tyrfing.games.id18.edit.battle.action.EndTurnAction;
import com.tyrfing.games.id18.edit.faction.AFactionActionProvider;
import com.tyrfing.games.id18.edit.unit.action.ApplyAffectorAction;
import com.tyrfing.games.id18.edit.unit.action.MoveAction;
import com.tyrfing.games.id18.model.battle.Battle;
import com.tyrfing.games.id18.model.field.Field;
import com.tyrfing.games.id18.model.unit.Arte;
import com.tyrfing.games.id18.model.unit.Faction;
import com.tyrfing.games.id18.model.unit.Unit;
import com.tyrfing.games.tyrlib3.edit.ActionStack;
import com.tyrfing.games.tyrlib3.edit.action.IAction;
import com.tyrfing.games.tyrlib3.edit.action.IActionRequester;
import com.tyrfing.games.tyrlib3.math.Vector2I;

public class Ai extends AFactionActionProvider {
	
	private BattleDomain battleDomain;
	private Battle battle;
	private Field field;
	private int maxDepth;
	
	public Ai(BattleDomain battleDomain, Faction faction, int maxDepth) {
		super(faction);
		this.battleDomain = battleDomain;
		this.maxDepth = maxDepth;
		
		this.battle = battleDomain.getBattle();
		this.field = battle.getField();
	}
	
	public void requestAction(IActionRequester actionRequester) {
		ActionStack actionStack = BattleFactory.INSTANCE.createBattleActionStack(battle);
		
		int depth = 0;
		EvaluatedAction action = computeAction(depth, actionStack);
		actionRequester.onProvideRequest(action.getAction());
	}
	
	private EvaluatedAction computeAction(int depth, ActionStack actionStack) {
		Unit unit = battleDomain.getBattle().getCurrentUnit();
		List<IAction> actions = generateActions(unit);
		
		EvaluatedAction bestAction = null;
		
		for (IAction action : actions) {
			float evaluation = evaluateAction(action, depth + 1, actionStack);
			
			if (bestAction == null || evaluation > bestAction.getEvaluation()) {
				bestAction = new EvaluatedAction(action, evaluation);
			}	
		}
		
		return bestAction;
	}
	
	private float evaluateAction(IAction action, int depth, ActionStack actionStack) {
		actionStack.execute(action);
		
		Unit unit = battle.getCurrentUnit();
		Faction faction = unit.getFaction();
		
		if (depth == maxDepth || battle.areObjectivesAchieved(getFaction())) {
			float evaluation = getEvaluation(depth);
			actionStack.undo();
			return evaluation;
		}
		
		List<IAction> actions = generateActions(unit);
		
		if (actions.size() == 0) {
			float evaluation = getEvaluation(depth);
			actionStack.undo();
			return evaluation;
		}
		
		float bestEvaluation = Float.NEGATIVE_INFINITY;
		
		for (IAction nextAction : actions) {
			float evaluation = evaluateAction(nextAction, depth + 1, actionStack);
			if (bestEvaluation == Float.NEGATIVE_INFINITY) {
				bestEvaluation = evaluation;
			} else {
				if (faction.equals(getFaction())) {
					bestEvaluation = Math.max(bestEvaluation, evaluation);
				} else {
					bestEvaluation = Math.min(bestEvaluation, evaluation);
				}
			}
		}
		
		actionStack.undo();
		return bestEvaluation;
	}
	
	private float getEvaluation(int depth) {
		if (battle.areObjectivesAchieved(getFaction())) {
			return 1.f / depth;
		} 
		
		return 0;
	}
	
	private List<IAction> generateActions(Unit unit) {
		List<IAction> actions = new ArrayList<IAction>();
		
		for (Arte arte : unit.getArtes()) {
			for (int x = 0;  x < field.getSize().x; ++x) {
				for (int y = 0;  y < field.getSize().y; ++y) {
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
	

	
}
