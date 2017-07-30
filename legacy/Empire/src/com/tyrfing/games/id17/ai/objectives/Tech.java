package com.tyrfing.games.id17.ai.objectives;

import com.tyrfing.games.id17.ai.BehaviorModel;
import com.tyrfing.games.id17.ai.Decision;
import com.tyrfing.games.id17.ai.actions.AIActions;
import com.tyrfing.games.id17.diplomacy.Diplomacy;
import com.tyrfing.games.id17.diplomacy.Message;
import com.tyrfing.games.id17.technology.Technology;

public class Tech extends Objective {

	// Needs to be feasible: Enough Research TODO
	// Needs to have enough money (ok)
	// Needs to choose tech smartly
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 4986358877130438844L;
	private final Technology t;
	
	public Tech(BehaviorModel model, int[] options, float maxTime) {
		super(model, options, maxTime);
		
		t = model.getTechTarget();
	}

	@Override
	public Decision achieve() {
		
		if (t == null || model.house.hasResearched(t) || (model.house.techProject != null && model.house.techProject.tech == t)) {
			return new Decision(null, null, null, null, true);
		}
		
		if (model.house.getGold() < t.funds + model.bufferMoney || Math.random() < 0.1f) {
			if (	Math.random() <= 0.75f 
				&& 	model.house.getOverlord() != null 
				&& 	model.house.getOverlord().getGold() >= t.funds 
				&& 	model.recallReceivedResponse(
						getAction(Diplomacy.REQUEST_ID, 1),
						model.house.getOverlord(), 
						new int[] { t.ID }
					) != BehaviorModel.NO_RESPONSE_MEMORIZED) { 
				return new Decision(getAction(Diplomacy.REQUEST_ID, 1),
									null,
									new int[] { t.ID },
									model.house.getOverlord(),
									false);
			} else {
				return new Decision(null, 
									new MakeMoney(model, new int[] { (int) (t.funds - model.house.getGold())+1 }, maxTime),
									null, 
									null, 
									false );
			}
		} else {
			int[] options = { t.ID };
			if (AIActions.actions.get(AIActions.START_TECH).isEnabled(model.house, options)) {
				return new Decision(AIActions.actions.get(AIActions.START_TECH), 
									null,
									options, 
									null, 
									true );
			} 
		}
		
		return new Decision(null, null, null, null, true);
	}

	@Override
	public float getResponseValue(int response, Message message) {
		return 1;
	}

}
