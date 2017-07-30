package com.tyrfing.games.id17.ai.objectives;

import java.util.List;

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
import com.tyrfing.games.id17.diplomacy.actions.TradeAgreement;
import com.tyrfing.games.id17.diplomacy.category.RelationsCategory;
import com.tyrfing.games.id17.holdings.Barony;
import com.tyrfing.games.id17.holdings.Holding;
import com.tyrfing.games.id17.houses.House;
import com.tyrfing.games.id17.intrigue.Intrigue;
import com.tyrfing.games.id17.war.WarGoal;
import com.tyrfing.games.id17.world.World;

public class Conquer extends Objective {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 6074534816369204496L;
	public static float[] WEIGHTS_TARGET = Objective.getWeightSet();
	public static float[] WEIGHTS_NONE_TARGET = Objective.getWeightSet();
	static {
		WEIGHTS_TARGET[DefensivePact.ID] = 0;
		WEIGHTS_TARGET[HonorDefensivePact.ID] = 0.0f;
		WEIGHTS_TARGET[InviteToIntrigue.ID] = 0;
		WEIGHTS_TARGET[Marriage.ID] = 0;
		WEIGHTS_TARGET[PayLoan.ID] = 0.5f;
		WEIGHTS_TARGET[RequestLoan.ID] = 0;
		WEIGHTS_TARGET[TradeAgreement.ID] = 0;
		
		WEIGHTS_NONE_TARGET[HonorDefensivePact.ID] = 0.5f;
	} 
	
	public Conquer(BehaviorModel model, int[] options, float maxTime) {
		super(model, options, maxTime);
	}

	@Override
	public Decision achieve() {
		Holding holding = World.getInstance().getHolding(options[0]);
		
		if (Math.random() <= 0.5f && model.house.getWeighedTotalTroopCount() <= holding.getOwner().getSupremeOverlord().getWeighedTotalTroopCount()) {
			List<Barony> baronies = model.house.getBaronies();
			if (baronies.size() == 0)  return new Decision(null, null, null, null, true);
			Barony barony = baronies.get((int)(Math.random() * baronies.size()));
			return new Decision(null, 
								new ExpandArmy(model, new int[] { barony.getHoldingID() } , 30),
								null, 
								null, 
								false );
		}
		
		
		int reason = model.house.hasWarReason(holding);
		if (WarGoal.NO_REASON != reason || (Math.random() <= 0.01f && (model.house.intrigueProject == null || model.house.intrigueProject.receiver.getSupremeOverlord() != holding.getOwner().getSupremeOverlord()))) {
			if (Diplomacy.getInstance().getAction(Diplomacy.RELATIONS_ID,RelationsCategory.DECLARE_WAR).isEnabled(model.house, holding.getOwner().getSupremeOverlord())) {
				return new Decision(Diplomacy.getInstance().getAction(Diplomacy.RELATIONS_ID, RelationsCategory.DECLARE_WAR), 
									null,
									new int[] { options[0], reason, 0 }, 
									holding.getOwner().getSupremeOverlord(), 
									true );
			}
		} else if (model.house.intrigueProject == null && model.house.isEnemy(holding.getOwner()) == null) {
			if (	(model.house.hasSpy(holding.getOwner()) || Math.random() <= 0.1f) 
					&& 	Intrigue.getInstance().categories.get(Intrigue.HOLDINGS_CATRGORY_ID).getAction(0).isEnabled(model.house, holding.getOwner()) 
					&&  reason == WarGoal.NO_REASON) {
				return new Decision(Intrigue.getInstance().categories.get(Intrigue.HOLDINGS_CATRGORY_ID).getAction(0), 
									null,
									new int[] { options[0] }, 
									holding.getOwner(), 
									false );
			} else if (Intrigue.getInstance().categories.get(Intrigue.ESPIONAGE_CATRGORY_ID).getAction(0).isEnabled(model.house, holding.getOwner())){
				int choice = -1;
				if (Math.random() <= 0.5 && model.house.getMales() > 0) {
					choice = 0;
				} else if (model.house.getFemales() > 0) {
					choice = 1;
				}
				if (choice != -1) {
					return new Decision(Intrigue.getInstance().categories.get(Intrigue.ESPIONAGE_CATRGORY_ID).getAction(0), 
										null,
										new int[] { choice }, 
										holding.getOwner(), 
										false );
				}
			} 
		} else if (model.house.intrigueProject != null) {
			House house = model.chooseIntriguePartner();
			if (house != null) {
				InviteToIntrigue invite = new InviteToIntrigue();
				if (invite.isEnabled(model.house, house)) {
					return new Decision(invite, 
										null,
										null, 
										house, 
										false );
				}
			}
		}
		
		if (model.house.getWeighedTotalTroopCount() <= holding.getOwner().getSupremeOverlord().getWeighedTotalTroopCount()) {
			List<Barony> baronies = model.house.getBaronies();
			if (baronies.size() == 0)  return new Decision(null, null, null, null, true);
			Barony barony = baronies.get((int)(Math.random() * baronies.size()));
			return new Decision(null, 
								new ExpandArmy(model, new int[] { barony.getHoldingID() } , 30),
								null, 
								null, 
								false );
		}
		
		return new Decision(null, null, null, null, false);
	}
	
	@Override
	public float getResponseValue(int response, Message message) {
		Holding holding = World.getInstance().getHolding(options[0]);
		House target = holding.getOwner();
		if (message.sender == target || message.sender.getSupremeOverlord() == target) {
			return WEIGHTS_TARGET[message.action.id];
		} else {
			return WEIGHTS_NONE_TARGET[message.action.id];
		}
	}

}
