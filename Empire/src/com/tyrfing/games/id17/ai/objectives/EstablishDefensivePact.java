package com.tyrfing.games.id17.ai.objectives;

import com.tyrfing.games.id17.ai.Decision;
import com.tyrfing.games.id17.ai.BehaviorModel;
import com.tyrfing.games.id17.diplomacy.Diplomacy;
import com.tyrfing.games.id17.diplomacy.Message;
import com.tyrfing.games.id17.diplomacy.actions.DefensivePact;
import com.tyrfing.games.id17.diplomacy.actions.Marriage;
import com.tyrfing.games.id17.diplomacy.category.PactsCategory;
import com.tyrfing.games.id17.diplomacy.category.RelationsCategory;
import com.tyrfing.games.id17.houses.House;
import com.tyrfing.games.id17.world.World;

/**
 * OPTIONS:
 * 0: ID of target house
 * @author Sascha
 *
 */

public class EstablishDefensivePact extends Objective {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1425656362636548819L;

	public EstablishDefensivePact(BehaviorModel model, int[] options, float maxTime) {
		super(model, options, maxTime);
	}

	@Override
	public Decision achieve() {
		House target = World.getInstance().getHouses().get(options[0]);
		
		if (target.isEnemy(model.house) != null || target.getHouseStat(model.house, House.HAS_DEFENSIVE_PACT) == 1) {
			failed = true;
			return null;
		}
		
		if (model.house.getHouseStat(target, House.HAS_DIPLOMAT) == 1 || target.getHouseStat(model.house, House.HAS_DIPLOMAT) == 1) {
			if (target.getRelation(model.house) < DefensivePact.REQUIRED_RELATION) {
				return new Decision(null,
									new ImproveRelation(model, new int[] { options[0], (int) (DefensivePact.REQUIRED_RELATION - target.getRelation(model.house) + 0.5f) } ,maxTime),
									null,
									target,
									false);			
			} else {
				return new Decision(Diplomacy.getInstance().getAction(Diplomacy.PACTS_ID, PactsCategory.DEFENSIVE_PACT),
									null,
									null,
									target,
									true);
			}
		} else if (Math.random() <= 0.5f) {
			return new Decision(Diplomacy.getInstance().getAction(Diplomacy.PACTS_ID, PactsCategory.SEND_DIPLOMAT),
								null,
								null,
								target,
								false);
		} else {
			return new Decision(Diplomacy.getInstance().getAction(Diplomacy.RELATIONS_ID, RelationsCategory.MARRIAGE),
								null,
								new int[] { 0, Marriage.getPrice(model.house) },
								target,
								false);
		}
	}

	@Override
	public float getResponseValue(int response, Message message) {
		House target = World.getInstance().getHouses().get(options[0]);
		if (message.action instanceof DefensivePact && message.sender == target) {
			return 8;
		} else if (message.action instanceof Marriage && message.sender == target) {
			return 4;
		} else {
			return 1;
		}
	}

}
