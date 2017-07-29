package com.tyrfing.games.id18.model.ai;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import com.tyrfing.games.id18.edit.battle.BattleDomain;
import com.tyrfing.games.id18.edit.battle.BattleFactory;
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
import com.tyrfing.games.tyrlib3.math.Vector2I;

public class MiniMaxAlgorithm {
	
	private BattleDomain battleDomain;
	private Battle battle;
	private Field field;
	private int maxDepth;
	private Faction faction;
	
	private Heuristic heuristic;
	
	private ActionStack battleActionStack;
	private ActionStack actionStack;;
	
	private MinMaxStatistics minMaxStatistics;
	
	public MiniMaxAlgorithm(BattleDomain battleDomain, Heuristic heuristic, int maxDepth) {
		this.battleDomain = battleDomain;
		this.maxDepth = maxDepth;
		
		this.battle = battleDomain.getBattle();
		this.field = battle.getField();
		
		this.heuristic = heuristic;
		this.faction = heuristic.getFaction();
		
		actionStack = new ActionStack();
		battleActionStack = BattleFactory.INSTANCE.createBattleActionStack(battle);
		
		this.minMaxStatistics = new MinMaxStatistics();
	}
	
	public EvaluatedAction computeAction() {
		Unit unit = battleDomain.getBattle().getCurrentUnit();
		List<IAction> actions = generateActions(unit);
		List<EvaluatedAction> sortedActions = toSortedActions(actions, 1);
		
		EvaluatedAction bestAction = null;
		
		for (EvaluatedAction preEvaluatedAction : sortedActions) {
			float evaluation = evaluateAction(preEvaluatedAction.getAction(), 1, Float.NEGATIVE_INFINITY, Float.POSITIVE_INFINITY);
			
			if (bestAction == null || evaluation > bestAction.getEvaluation()) {
				bestAction = new EvaluatedAction(preEvaluatedAction.getAction(), evaluation);
			}	
		}
		
		return bestAction;
	}
	
	private float evaluateAction(IAction action, int depth, float alpha, float beta) {
		actionStack.execute(action);
		
		Unit unit = battle.getCurrentUnit();
		Faction faction = unit.getFaction();
		
		if (depth == maxDepth || battle.areObjectivesAchieved(this.faction)) {
			float evaluation = heuristic.getEvaluation(depth);
			actionStack.undo();
			return evaluation;
		}
		
		List<IAction> actions = generateActions(unit);
		
		if (actions.size() == 0) {
			float evaluation = heuristic.getEvaluation(depth);
			actionStack.undo();
			return evaluation;
		}
		
		List<EvaluatedAction> sortedActions = toSortedActions(actions, depth + 1);
		
		boolean isMaxNode = faction.equals(this.faction);
		float bestEvaluation = isMaxNode ? Float.NEGATIVE_INFINITY : Float.POSITIVE_INFINITY;
		
		for (EvaluatedAction preEvaluatedAction : sortedActions) {
			minMaxStatistics.generatedStates++;
			
			float evaluation = evaluateAction(preEvaluatedAction.getAction(), depth + 1, alpha, beta);
			
			if (isMaxNode) {
				bestEvaluation = Math.max(bestEvaluation, evaluation);
				alpha = Math.max(alpha, bestEvaluation);
				
				if (checkAlphaBetaCutoff(alpha, beta)) {
					break;
				}
			} else {
				bestEvaluation = Math.min(bestEvaluation, evaluation);
				beta = Math.min(bestEvaluation, beta);
				
				if (checkAlphaBetaCutoff(alpha, beta)) {
					break;
				}
			}
		}
		
		actionStack.undo();
		return bestEvaluation;
	}
	
	private boolean checkAlphaBetaCutoff(float alpha, float beta) {
		return beta <= alpha;
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
	
	private List<EvaluatedAction> toSortedActions(List<IAction> actions, int depth) {
		List<EvaluatedAction> evaluatedActions = new ArrayList<EvaluatedAction>();
		
		for (IAction action : actions) {
			minMaxStatistics.generatedStates++;
			
			battleActionStack.execute(action);
			
			float evaluation = heuristic.getEvaluation(depth);
			EvaluatedAction evaluatedAction = new EvaluatedAction(action, evaluation);
			evaluatedActions.add(evaluatedAction);
			
			battleActionStack.undo();
		}
		
		Collections.sort(evaluatedActions);
		
		return evaluatedActions;
	}

	public Heuristic getHeuristic() {
		return heuristic;
	}

	public Battle getBattle() {
		return battle;
	}
	
	public MinMaxStatistics getMinMaxStatistics() {
		return minMaxStatistics;
	}
}
