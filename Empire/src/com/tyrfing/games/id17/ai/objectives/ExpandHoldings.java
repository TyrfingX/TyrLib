package com.tyrfing.games.id17.ai.objectives;

import com.tyrfing.games.id17.ai.BehaviorModel;
import com.tyrfing.games.id17.ai.Decision;
import com.tyrfing.games.id17.diplomacy.Diplomacy;
import com.tyrfing.games.id17.diplomacy.Message;
import com.tyrfing.games.id17.diplomacy.actions.DiploAction;
import com.tyrfing.games.id17.diplomacy.actions.InviteToIntrigue;
import com.tyrfing.games.id17.diplomacy.actions.RequestClaim;
import com.tyrfing.games.id17.diplomacy.category.RequestCategory;
import com.tyrfing.games.id17.holdings.Holding;
import com.tyrfing.games.id17.houses.House;
import com.tyrfing.games.id17.intrigue.Intrigue;
import com.tyrfing.games.id17.war.WarGoal;
import com.tyrfing.games.id17.war.WarJustification;

public class ExpandHoldings extends Objective {

	/**
	 * 
	 */
	private static final long serialVersionUID = -8243659496721037888L;

	public ExpandHoldings(BehaviorModel model, int[] options, float maxTime) {
		super(model, options, maxTime);
	}

	@Override
	public Decision achieve() {
		if (model.house.isIndependend()) {
			Holding holding = model.revokeHolding();
			if (holding != null) {
				return new Decision(Diplomacy.getInstance().getCategory(3).getAction(1), 
									null,
									new int[] { holding.getHoldingID() }, 
									holding.getOwner(), 
									true );
			} else if (Math.random() <= 1.f / (model.house.getCountWars()+1)){
				holding = model.conquerTarget();
				if (holding != null) {
					return new Decision(null, 
										new Conquer(model, new int[] { holding.getHoldingID() }, maxTime),
										null, 
										null, 
										true );
				}
			}
		} else if (model.house.getCountJustfications() > 0 && !model.house.isIndependend()) {
			DiploAction claim = Diplomacy.getInstance().getAction(Diplomacy.REQUEST_ID, RequestCategory.CLAIM);
			for (int i = 0; i < model.house.getCountJustfications(); ++i ) {
				WarJustification j =  model.house.getJustification(i);
				Holding holding = j.getHolding();
				if (	claim.isEnabled(model.house, holding.getOwner()) 
					&& 	model.house.getHouseStat(model.house.getSupremeOverlord(), House.FAVOR_STAT) >= RequestClaim.getFavorCost(holding, model.house, model.house.getSupremeOverlord())
					&& 	holding.getOwner() != model.house) {
					return new Decision(claim, 
										null,
										new int[] { holding.getHoldingID() }, 
										model.house.getSupremeOverlord(), 
										false );
				}
			}
		} else if (model.house.intrigueProject == null || Math.random() <= model.getAggressiveness()) {
			Holding holding = model.conquerTarget();
			if (holding != null) {
				if (	(model.house.hasSpy(holding.getOwner()) || model.house.intrigueProject != null || Math.random() <= model.getAggressiveness()) 
					&& 	Intrigue.getInstance().categories.get(Intrigue.HOLDINGS_CATRGORY_ID).getAction(0).isEnabled(model.house, holding.getOwner()) 
					&&  model.house.hasWarReason(holding) != WarGoal.NO_REASON ) {
					return new Decision(Intrigue.getInstance().categories.get(Intrigue.HOLDINGS_CATRGORY_ID).getAction(0), 
							null,
							new int[] { holding.getHoldingID() }, 
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
			}
		} else {
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
		
		return null;
	}

	@Override
	public float getResponseValue(int response, Message message) {
		if (model.house.getSupremeOverlord() == message.sender && message.action instanceof InviteToIntrigue) {
			return 2;
		}
		
		return 1;
	}

}
