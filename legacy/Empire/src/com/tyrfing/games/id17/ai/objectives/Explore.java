package com.tyrfing.games.id17.ai.objectives;

import com.tyrfing.games.id17.ai.BehaviorModel;
import com.tyrfing.games.id17.ai.Decision;
import com.tyrfing.games.id17.diplomacy.Message;
import com.tyrfing.games.id17.holdings.Barony;
import com.tyrfing.games.id17.world.World;

public class Explore extends Objective {

	/**
	 * 
	 */
	private static final long serialVersionUID = -4669561662090938625L;

	private Barony b;
	
	public Explore(BehaviorModel model, int[] options, float maxTime) {
		super(model, options, maxTime);
		b = World.getInstance().getBarony(options[0]);
	}

	@Override
	public Decision achieve() {
		if (b != null) {
			model.armyModel.requestExploration(b);
			return Decision.ACHIEVED_DECISION;
		} else {
			return Decision.UNACHIEVED_DECISION;
		}
	}

	@Override
	public float getResponseValue(int response, Message message) {
		return 1;
	}

}
