package com.tyrfing.games.id17.ai.objectives;

import com.tyrfing.games.id17.ai.BehaviorModel;
import com.tyrfing.games.id17.ai.Decision;
import com.tyrfing.games.id17.ai.actions.AIActions;
import com.tyrfing.games.id17.ai.actions.BuildRoad;
import com.tyrfing.games.id17.diplomacy.Message;
import com.tyrfing.games.id17.holdings.Holding;
import com.tyrlib2.util.Pair;

public class ImproveRoads extends Objective {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 499193751739895064L;

	private Pair<Holding, Holding> road;
	
	public ImproveRoads(BehaviorModel model, int[] options, float maxTime) {
		super(model, options, maxTime);
	}

	@Override
	public Decision achieve() {
		if (road == null)  {
			road = model.getTargetRoad();
		}
		
		if (road != null) {
			
			int[] options = { road.getFirst().getHoldingID(), road.getSecond().getHoldingID() };
			
			if (BuildRoad.sIsEnabled(model.house, options)) {
				return new Decision(AIActions.actions.get(AIActions.BUILD_ROAD), 
									null,
									options, 
									null, 
									true );
			} 
		}
		
		return Decision.UNACHIEVED_DECISION;
	}
	
	@Override
	public float getResponseValue(int response, Message message) {
		return 1;
	}
}
