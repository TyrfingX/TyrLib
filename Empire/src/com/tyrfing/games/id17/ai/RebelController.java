package com.tyrfing.games.id17.ai;

import com.tyrfing.games.id17.diplomacy.Diplomacy;
import com.tyrfing.games.id17.diplomacy.Message;
import com.tyrfing.games.id17.diplomacy.actions.DiploAction;
import com.tyrfing.games.id17.diplomacy.category.PeaceCategory;
import com.tyrfing.games.id17.war.Army;
import com.tyrfing.games.id17.war.War;


public class RebelController extends AIController  {

	/**
	 * 
	 */
	private static final long serialVersionUID = 4089956422066868838L;

	public RebelController(BehaviorModel model, Army army) {
		super(model);
		model.armyModel.addController(army);
	}
	
	
	public void update() {
		model.armyModel.update();
		
		for (int i = 0; i < model.house.getCountWars(); ++i) {
			War war = model.house.getWar(i);
			if (war.getProgress() >= 1) {
				performDecision(new Decision(	Diplomacy.getInstance().getCategory(Diplomacy.REACE_ID).getAction(PeaceCategory.DICTATE_ID), 
												null,
												null,
												war.getOther(model.house), 
												false ));
			} else if (war.getProgress() <= -1) {
				performDecision(new Decision(	Diplomacy.getInstance().getCategory(Diplomacy.REACE_ID).getAction(PeaceCategory.ADMIT_ID), 
												null,
												null,
												war.getOther(model.house), 
												false ));
			}
		}
	}
	
	private void performDecision(Decision decision) {
		Message m = new Message((DiploAction)decision.action, model.house, decision.target, decision.options);
		AIThread.getInstance().addMessage(m);
	}

}
