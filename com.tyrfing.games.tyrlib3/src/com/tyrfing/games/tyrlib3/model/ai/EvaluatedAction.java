package com.tyrfing.games.tyrlib3.model.ai;

import com.tyrfing.games.tyrlib3.edit.action.IAction;

public class EvaluatedAction implements Comparable<EvaluatedAction> {
	private IAction action;
	private float evaluation;
	
	public EvaluatedAction(IAction action, float evaluation) {
		this.action = action;
		this.evaluation = evaluation;
	}
	
	public IAction getAction() {
		return action;
	}
	
	public float getEvaluation() {
		return evaluation;
	}
	
	public void setAction(IAction action) {
		this.action = action;
	}
	
	public void setEvaluation(float evaluation) {
		this.evaluation = evaluation;
	}

	@Override
	public int compareTo(EvaluatedAction other) {
		return Float.compare(other.getEvaluation(), evaluation);
	}
}
