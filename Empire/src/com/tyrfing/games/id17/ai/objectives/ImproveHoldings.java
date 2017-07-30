package com.tyrfing.games.id17.ai.objectives;

import com.tyrfing.games.id17.ai.BehaviorModel;
import com.tyrfing.games.id17.ai.Decision;
import com.tyrfing.games.id17.ai.actions.AIActions;
import com.tyrfing.games.id17.buildings.Building;
import com.tyrfing.games.id17.buildings.Guild;
import com.tyrfing.games.id17.diplomacy.Message;
import com.tyrfing.games.id17.holdings.Barony;
import com.tyrfing.games.id17.holdings.Holding;

public class ImproveHoldings extends Objective {

	/**
	 * 
	 */
	private static final long serialVersionUID = 8244969437970612524L;
	private int holdingID = -1;
	private int buildingID;
	private Building.TYPE type;
	private Holding holding;
	
	public ImproveHoldings(BehaviorModel model, int[] options, float maxTime) {
		super(model, options, maxTime);
	}

	@Override
	public Decision achieve() {
		
		if (model.house.getHoldings().size() == 0) return new Decision(null, null, null, null, true);
		
		holdingID = (int) (model.house.getHoldings().size() * Math.random());
		buildingID = (int) (Building.TYPE.values().length * Math.random());
		type = Building.VALUES[buildingID];
		holding = model.house.getHoldings().get(holdingID);
		holdingID = holding.getHoldingID();
		
		if (!Building.isBuildableInHolding(type, holding)) {
			holdingID = -1;
			return null;
		}
		
		float price = Building.getPrice(type, holding);
		if (price + model.bufferMoney > model.house.getGold()) {
			return new Decision(null, 
					new MakeMoney(model, new int[] { (int) price }, maxTime),
					null, 
					null, 
					false );
		} else {
			if (type != Building.TYPE.Guild || holding.isBuilt(type) == null) {
				int[] options = { holdingID, buildingID };
				if (AIActions.actions.get(1).isEnabled(model.house, options)) {
					return new Decision(AIActions.actions.get(1), 
										null,
										options, 
										null, 
										true );
				}
			} else {
				for (int i = 0; i < 5; ++i) {
					Guild.TYPE gType = Guild.TYPE.values()[(int)(Math.random() * Guild.TYPE.values().length)];
					if (Guild.isProductive(gType, holding)) {
						int[] options = { holding.getHoldingID(), gType.ordinal(), -1 };
						
						if (gType == Guild.TYPE.Merchants) {
							if (model.house.getBaronies().size() <= 1) {
								continue;
							} else {
								Barony b = model.house.getBaronies().get((int) (Math.random() * model.house.getBaronies().size()));
								options[2] = b.getIndex();
								if (b == holding) {
									continue;
								}
							}
						} 
						
						return new Decision(AIActions.actions.get(AIActions.SETTLE_GUILD), 
											null,
											options, 
											null, 
											true );
					}
				}
			}
		}
		
		return null;
	}

	@Override
	public float getResponseValue(int response, Message message) {
		return 1;
	}

}
