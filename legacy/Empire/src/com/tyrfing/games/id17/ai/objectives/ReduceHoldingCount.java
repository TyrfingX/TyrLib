package com.tyrfing.games.id17.ai.objectives;

import com.tyrfing.games.id17.ai.BehaviorModel;
import com.tyrfing.games.id17.ai.Decision;
import com.tyrfing.games.id17.diplomacy.Diplomacy;
import com.tyrfing.games.id17.diplomacy.Message;
import com.tyrfing.games.id17.diplomacy.category.HoldingsCategory;
import com.tyrfing.games.id17.holdings.Holding;
import com.tyrfing.games.id17.houses.House;

public class ReduceHoldingCount extends Objective {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1009771368068451319L;
	private int targetCount;
	
	public ReduceHoldingCount(BehaviorModel model, int[] options, float maxTime) {
		super(model, options, maxTime);
		targetCount = options[0];
	}

	@Override
	public Decision achieve() {
		
		if (targetCount == model.house.getHoldings().size()) return new Decision(null, null, null, null, true);
		
		House house = model.getGrantHoldingHouse();
		if (house != null) {
			Holding holding = model.getGrantHoldingHolding(house);
			if (holding != null) {
				return new Decision(Diplomacy.getInstance().getAction(Diplomacy.HOLDINGS_ID, HoldingsCategory.GRANT),
									null,
									new int[] { holding.getHoldingID() },
									house,
									false);
			}
		}
		
		return new Decision(null, null, null, null, true);
	}

	@Override
	public float getResponseValue(int response, Message message) {
		return 1;
	}

}
