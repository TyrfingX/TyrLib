package com.tyrfing.games.id17.ai.objectives;

import com.tyrfing.games.id17.ai.Decision;
import com.tyrfing.games.id17.ai.BehaviorModel;
import com.tyrfing.games.id17.diplomacy.Diplomacy;
import com.tyrfing.games.id17.diplomacy.Message;
import com.tyrfing.games.id17.diplomacy.actions.DefensivePact;
import com.tyrfing.games.id17.diplomacy.actions.HonorDefensivePact;
import com.tyrfing.games.id17.diplomacy.actions.InviteToIntrigue;
import com.tyrfing.games.id17.diplomacy.actions.Marriage;
import com.tyrfing.games.id17.diplomacy.actions.PayLoan;
import com.tyrfing.games.id17.diplomacy.actions.RequestLoan;
import com.tyrfing.games.id17.diplomacy.actions.SendGift;
import com.tyrfing.games.id17.diplomacy.actions.TradeAgreement;
import com.tyrfing.games.id17.diplomacy.category.PactsCategory;
import com.tyrfing.games.id17.diplomacy.category.RelationsCategory;
import com.tyrfing.games.id17.houses.House;
import com.tyrfing.games.id17.world.World;

public class ImproveRelation extends Objective {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 7428924733595780314L;
	public static float[] WEIGHTS_TARGET = Objective.getWeightSet();
	public static float[] WEIGHTS_NONE_TARGET = Objective.getWeightSet();
	static {
		WEIGHTS_TARGET[DefensivePact.ID] = 2;
		WEIGHTS_TARGET[HonorDefensivePact.ID] = 2f;
		WEIGHTS_TARGET[InviteToIntrigue.ID] = 2;
		WEIGHTS_TARGET[Marriage.ID] = 2;
		WEIGHTS_TARGET[PayLoan.ID] = 2;
		WEIGHTS_TARGET[RequestLoan.ID] = 2;
		WEIGHTS_TARGET[TradeAgreement.ID] = 2;
	} 
	
	public ImproveRelation(BehaviorModel model, int[] options, float maxTime) {
		super(model, options, maxTime);
	}

	@Override
	public Decision achieve() {
		House target = World.getInstance().getHouses().get(options[0]);
		
		if (target.getRelation(model.house) >= options[1]) {
			return Decision.ACHIEVED_DECISION;
		}
		
		if (Math.random() <= 0.75f && model.house.getHouseStat(target, House.HAS_MARRIAGE) == 0) {
			if (model.house.getMales() > 0 && model.house.getGold() >= Marriage.getPrice(target)) {
				return new Decision(Diplomacy.getInstance().getAction(Diplomacy.RELATIONS_ID, RelationsCategory.MARRIAGE),
									null,
									new int[] { 1, Marriage.getPrice(model.house) },
									target,
									false);
			} else if(model.house.getFemales() > 0 && target.getGold() >= Marriage.getPrice(model.house)) {
				return new Decision(Diplomacy.getInstance().getAction(Diplomacy.RELATIONS_ID, RelationsCategory.MARRIAGE),
									null,
									new int[] { 0, Marriage.getPrice(target) },
									target,
									false);
			}
		}
		
		if (Diplomacy.getInstance().getAction(Diplomacy.RELATIONS_ID, RelationsCategory.SEND_GIFT).isEnabled(model.house, target) && target.getModifierValue("Gift", model.house) < SendGift.RECEIVER_RELATION_MAX) {
			return new Decision(Diplomacy.getInstance().getAction(Diplomacy.RELATIONS_ID, RelationsCategory.SEND_GIFT),
								null,
								null,
								target,
								false);
		}
		
		if (Math.random() <= 0.1f && Diplomacy.getInstance().getAction(Diplomacy.PACTS_ID, PactsCategory.TRADE_AGREEMENT).isEnabled(model.house, target)) {
			return new Decision(Diplomacy.getInstance().getAction(Diplomacy.PACTS_ID, PactsCategory.TRADE_AGREEMENT),
								null,
								null,
								target,
								false);
		}
		
		return null;
	}

	@Override
	public float getResponseValue(int response, Message message) {
		House target = World.getInstance().getHouses().get(options[0]);
		if (message.sender == target) {
			return WEIGHTS_TARGET[message.action.id];
		} else {
			return 1;
		}
	}

}
