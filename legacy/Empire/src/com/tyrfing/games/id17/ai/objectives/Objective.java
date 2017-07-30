package com.tyrfing.games.id17.ai.objectives;

import java.io.Serializable;

import com.tyrfing.games.id17.ai.Decision;
import com.tyrfing.games.id17.ai.BehaviorModel;
import com.tyrfing.games.id17.diplomacy.Diplomacy;
import com.tyrfing.games.id17.diplomacy.Message;
import com.tyrfing.games.id17.diplomacy.actions.DiploAction;

public abstract class Objective implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 2615775956060852084L;
	
	public final float maxTime;
	public final int[] options;
	public final BehaviorModel model;

	protected boolean failed = false;
	
	
	public Objective(BehaviorModel model, int options[], float maxTime) {
		this.options = options;
		this.maxTime = maxTime;
		this.model = model;
	}
	
	public abstract Decision achieve();
	public abstract float getResponseValue(int response, Message message);
	
	public boolean hasFailed() {
		return failed;
	}
	
	public static float[] getWeightSet() {
		float[] weights = new float[DiploAction.COUNT_DIPLO_ACTIONS];
		for (int i = 0; i < weights.length; ++i) {
			weights[i] = 1;
		}
		return weights;
	}
	
	public DiploAction getAction(int categoryID, int actionID) {
		return Diplomacy.getInstance().getAction(categoryID, actionID);
	}
}
