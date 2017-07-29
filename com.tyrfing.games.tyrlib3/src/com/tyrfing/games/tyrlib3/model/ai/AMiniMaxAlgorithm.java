package com.tyrfing.games.tyrlib3.model.ai;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import com.tyrfing.games.tyrlib3.edit.ActionStack;
import com.tyrfing.games.tyrlib3.edit.action.IAction;

public abstract class AMiniMaxAlgorithm {
	
	private int maxDepth;
	
	private IHeuristic heuristic;
	
	private ActionStack actionStackWithListeners;
	private ActionStack actionStack;;
	
	private MinMaxStatistics minMaxStatistics;
	
	public AMiniMaxAlgorithm(ActionStack actionStackWithListeners, IHeuristic heuristic, int maxDepth) {
		this.maxDepth = maxDepth;
		
		this.heuristic = heuristic;
		
		actionStack = new ActionStack();
		this.actionStackWithListeners = actionStackWithListeners;
		
		this.minMaxStatistics = new MinMaxStatistics();
	}
	
	protected abstract List<IAction> generateActions();
	protected abstract boolean isTerminalState();
	protected abstract boolean isMaxNode();
	
	public EvaluatedAction computeAction() {
		List<IAction> actions = generateActions();
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
		
		if (depth == maxDepth || isTerminalState()) {
			float evaluation = heuristic.getEvaluation(depth);
			actionStack.undo();
			return evaluation;
		}
		
		List<IAction> actions = generateActions();
		
		if (actions.size() == 0) {
			float evaluation = heuristic.getEvaluation(depth);
			actionStack.undo();
			return evaluation;
		}
		
		List<EvaluatedAction> sortedActions = toSortedActions(actions, depth + 1);
		
		boolean isMaxNode = isMaxNode();
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
	
	private List<EvaluatedAction> toSortedActions(List<IAction> actions, int depth) {
		List<EvaluatedAction> evaluatedActions = new ArrayList<EvaluatedAction>();
		
		for (IAction action : actions) {
			minMaxStatistics.generatedStates++;
			
			actionStackWithListeners.execute(action);
			
			float evaluation = heuristic.getEvaluation(depth);
			EvaluatedAction evaluatedAction = new EvaluatedAction(action, evaluation);
			evaluatedActions.add(evaluatedAction);
			
			actionStackWithListeners.undo();
		}
		
		Collections.sort(evaluatedActions);
		
		return evaluatedActions;
	}

	public IHeuristic getHeuristic() {
		return heuristic;
	}

	public MinMaxStatistics getMinMaxStatistics() {
		return minMaxStatistics;
	}
}
