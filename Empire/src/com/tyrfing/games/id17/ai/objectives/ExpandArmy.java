package com.tyrfing.games.id17.ai.objectives;

import com.tyrfing.games.id17.ai.Decision;
import com.tyrfing.games.id17.ai.BehaviorModel;
import com.tyrfing.games.id17.ai.actions.AIActions;
import com.tyrfing.games.id17.diplomacy.Message;
import com.tyrfing.games.id17.holdings.Barony;
import com.tyrfing.games.id17.laws.LawSet;
import com.tyrfing.games.id17.war.Army;
import com.tyrfing.games.id17.war.Regiment;
import com.tyrfing.games.id17.war.UnitType;
import com.tyrfing.games.id17.world.World;

/**
 * OPTIONS
 * 0: Barony
 * @author Sascha
 *
 */

public class ExpandArmy extends Objective {

	/**
	 * 
	 */
	private static final long serialVersionUID = 5488341599882821475L;

	public ExpandArmy(BehaviorModel model, int[] options, float maxTime) {
		super(model, options, maxTime);
	}

	@Override
	public Decision achieve() {
		
		int conscriptionSetting = model.house.getLawSetting(4);
		
		int maxTroops = 0;
		for (int i = 0; i < model.house.getBaronies().size();++i) {
			maxTroops += model.house.getBaronies().get(i).getLevy().getTotalTroopsMax();
			maxTroops += model.house.getBaronies().get(i).getGarrison().getTotalTroopsMax();
		}
		
		if (model.house.getTotalTroops() < maxTroops/10f && Math.random() <= (1-LawSet.getLaw(4).options[conscriptionSetting].values[0])/100) {
			return new Decision(AIActions.actions.get(2),
								null,
								new int[] { 4, conscriptionSetting+1 },
								null,
								false);
		}
		
		Barony barony = (Barony) World.getInstance().getHolding(options[0]);
		
		boolean levy = Math.random() <= 0.7f;
		
		int pos = (int) (Math.random() * (levy ? Army.MAX_REGIMENTS : 2) +  (levy ? 0 : 2));
		
		Regiment regiment = levy ? barony.getLevy().getRegiment(pos) : barony.getGarrison().getRegiment(pos);
		UnitType type = UnitType.Guardians;
		int level = 0;
		if (regiment == null) {
			UnitType[] types = UnitType.values();
			for (int i = 0; i < 3; ++i) {
				type = types[(int)(Math.random() * (types.length-1))];
				if (type != UnitType.Guardians) break;
			}
		} else {
			type = regiment.unitType;
			level = (int) (regiment.maxTroops / 100);
		}
		
		int[] buildOptions = { type.ordinal(), options[0], pos, levy ? 1 : 0 };
		
		if (model.house.getGold() >= UnitType.getPrice(type, level) + model.bufferMoney) {
			if (AIActions.actions.get(0).isEnabled(model.house, buildOptions)) {
				return new Decision(AIActions.actions.get(0), 
									null,
									buildOptions, 
									null, 
									true );
			} 
		} else {
			return new Decision(null, 
								new MakeMoney(model, new int[] { (int) UnitType.getPrice(type, level) }, maxTime),
								null, 
								null, 
								false );
		}
		
		return null;
	}

	@Override
	public float getResponseValue(int response, Message message) {
		return 1;
	}

}
