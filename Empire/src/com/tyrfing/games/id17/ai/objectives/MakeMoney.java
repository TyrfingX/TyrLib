package com.tyrfing.games.id17.ai.objectives;

import com.tyrfing.games.id17.ai.BehaviorModel;
import com.tyrfing.games.id17.ai.Decision;
import com.tyrfing.games.id17.ai.actions.AIActions;
import com.tyrfing.games.id17.buildings.Building;
import com.tyrfing.games.id17.buildings.Building.TYPE;
import com.tyrfing.games.id17.diplomacy.Diplomacy;
import com.tyrfing.games.id17.diplomacy.Message;
import com.tyrfing.games.id17.diplomacy.actions.HonorDefensivePact;
import com.tyrfing.games.id17.diplomacy.actions.Marriage;
import com.tyrfing.games.id17.diplomacy.actions.PayLoan;
import com.tyrfing.games.id17.diplomacy.actions.RequestLoan;
import com.tyrfing.games.id17.diplomacy.actions.RequestResearchFunds;
import com.tyrfing.games.id17.diplomacy.actions.TradeAgreement;
import com.tyrfing.games.id17.diplomacy.actions.WhitePeace;
import com.tyrfing.games.id17.diplomacy.category.PactsCategory;
import com.tyrfing.games.id17.holdings.Holding;
import com.tyrfing.games.id17.houses.House;
import com.tyrfing.games.id17.laws.LawSet;
import com.tyrlib2.game.Stats;

public class MakeMoney extends Objective {

	/**
	 * 
	 */
	private static final long serialVersionUID = 411113252613464961L;
	/**
	 * OPTIONS
	 * 0: target money
	 * 1: taking loans allowed
	 */
	
	private float targetGold;
	
	public MakeMoney(BehaviorModel model, int[] options, float maxTime) {
		super(model, options, maxTime);
		targetGold = model.bufferMoney + model.house.getGold() + options[0];
		
		
	}

	@Override
	public Decision achieve() {
		
		if (model.house.getGold() >= targetGold) {
			return new Decision(null, null, null, null, true);
		}
		
		Stats marketStats = Building.STATS.get(Building.TYPE.Market);
		if (model.house.getHoldings().size() > 0) {
			int rnd = (int) (Math.random() * model.house.getHoldings().size());
			Holding holding = model.house.getHoldings().get(rnd);
			if (Building.isBuildableInHolding(Building.TYPE.Market, holding)) {
				float marketBenefit = (Building.getPrice(TYPE.Market, holding)*model.house.getIncome())/(marketStats.getStat(Building.INCOME_BONUS)+model.house.getIncome());
				if (marketBenefit/(targetGold-model.house.getGold()) <= Math.random() * BehaviorModel.BUILD_PROB) {
				
					int[] options = { holding.getHoldingID(), Building.TYPE.Market.ordinal() };
					if (AIActions.actions.get(1).isEnabled(model.house, options)) {
						return new Decision(AIActions.actions.get(1), 
											null,
											options, 
											null, 
											false );
					}
				}
			}
		}

		// Increase taxes
		
		int res = model.house.getBaronies().size() > 0 ? 10 : 100;
		
		int popTaxesSetting = model.house.getLawSetting(0);
		if (targetGold > model.house.getIncome()*res && Math.random() <= (1-LawSet.getLaw(0).options[popTaxesSetting].values[0])/100) {
			return new Decision(AIActions.actions.get(2),
								null,
								new int[] { 0, popTaxesSetting+1 },
								null,
								false);
		}
		
		int tradeTaxesSetting = model.house.getLawSetting(1);
		if (targetGold > model.house.getIncome()*res && Math.random() <= (1-LawSet.getLaw(1).options[tradeTaxesSetting].values[0])/100) {
			return new Decision(AIActions.actions.get(2),
								null,
								new int[] { 1, tradeTaxesSetting+1 },
								null,
								false);
		}
		
		int vassalTaxSettings = model.house.getLawSetting(2);
		if (targetGold > model.house.getIncome()*res && Math.random() <= (1-LawSet.getLaw(2).options[vassalTaxSettings].values[0])/100) {
			return new Decision(AIActions.actions.get(2),
								null,
								new int[] { 2, vassalTaxSettings+1 },
								null,
								false);
		}
		
		
		House target = model.getRandomNeighbour();
		if (target != null) {
			if (target.getHouseStat(model.house, House.HAS_DIPLOMAT) == 0 && model.house.getHouseStat(target, House.HAS_DIPLOMAT) == 0 && Math.random() <= 0.01f) {
				if (Diplomacy.getInstance().getAction(Diplomacy.PACTS_ID, PactsCategory.SEND_DIPLOMAT).isEnabled(model.house, target)) {
					return new Decision(Diplomacy.getInstance().getAction(Diplomacy.PACTS_ID, PactsCategory.SEND_DIPLOMAT),
										null,
										null,
										target,
										false);
				}		
			} else if (Diplomacy.getInstance().getAction(Diplomacy.PACTS_ID, PactsCategory.TRADE_AGREEMENT).isEnabled(model.house, target) && Math.random() <= 0.01f) {
					return new Decision(Diplomacy.getInstance().getAction(Diplomacy.PACTS_ID, PactsCategory.TRADE_AGREEMENT),
										null,
										null,
										target,
										false);
			}
		}
		
		return null;
	}

	@Override
	public float getResponseValue(int response, Message message) {
		if (message.action instanceof TradeAgreement) {
			return 2;
		} else if (message.action instanceof Marriage) {
			return 2;
		} else if (message.action instanceof PayLoan) {
			return 0.5f;
		} else if (message.action instanceof RequestLoan) {
			return 0.5f;
		} else if (message.action instanceof WhitePeace) {
			return 2;
		} else if (message.action instanceof HonorDefensivePact) {
			return 0.5f;
		} else if (message.action instanceof RequestResearchFunds) {
			return 0.5f;
		} else {
			return 1;
		}
	}

}
