package com.tyrfing.games.id17.ai.objectives;

import com.tyrfing.games.id17.ai.Decision;
import com.tyrfing.games.id17.ai.BehaviorModel;
import com.tyrfing.games.id17.diplomacy.Diplomacy;
import com.tyrfing.games.id17.diplomacy.Message;
import com.tyrfing.games.id17.diplomacy.actions.Marriage;
import com.tyrfing.games.id17.diplomacy.category.IndependenceCategory;

public class SeekIndependence extends Objective {

	/**
	 * 
	 */
	private static final long serialVersionUID = 195771820880113871L;

	public SeekIndependence(BehaviorModel model, int[] options, float maxTime) {
		super(model, options, maxTime);
	}

	@Override
	public Decision achieve() {
		
		if (Diplomacy.getInstance().getAction(Diplomacy.INDEPENDENCE_ID, IndependenceCategory.DECLARE).isEnabled(model.house, model.house.getSupremeOverlord())) {
			return new Decision(Diplomacy.getInstance().getAction(Diplomacy.INDEPENDENCE_ID, IndependenceCategory.DECLARE),
								null,
								null,
								model.house.getSupremeOverlord(),
								true);
		}
		
		return new Decision(null, null, null, null, true);
	}

	@Override
	public float getResponseValue(int response, Message message) {
		if (message.action instanceof Marriage && message.sender == model.house.getSupremeOverlord()) {
			return 0;
		}
		return 1;
	}

}
