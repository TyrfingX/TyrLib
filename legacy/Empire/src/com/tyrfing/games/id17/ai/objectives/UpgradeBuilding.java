package com.tyrfing.games.id17.ai.objectives;

import com.tyrfing.games.id17.ai.Decision;
import com.tyrfing.games.id17.ai.BehaviorModel;
import com.tyrfing.games.id17.ai.actions.AIActions;
import com.tyrfing.games.id17.buildings.Building;
import com.tyrfing.games.id17.diplomacy.Message;

/**
 * OPTIONS:
 * 0: Holding ID
 * 1: Building Type ID
 * @author Sascha
 *
 */

public class UpgradeBuilding extends Objective {

	/**
	 * 
	 */
	private static final long serialVersionUID = 3836464320233842111L;

	public UpgradeBuilding(BehaviorModel model, int[] options, float maxTime) {
		super(model, options, maxTime);
	}

	@Override
	public Decision achieve() {
		if (AIActions.actions.get(1).isEnabled(model.house, options)) {
			return new Decision(AIActions.actions.get(1), 
								null,
								options, 
								null, 
								false );
		} else {
			return new Decision(null, 
								new MakeMoney(model, new int[] { (int) Building.STATS.get(Building.TYPE.values()[options[1]]).getStat(Building.PRICE) }, maxTime),
								null, 
								null, 
								false );
		}
	}

	@Override
	public float getResponseValue(int response, Message message) {
		return 1;
	}

}
