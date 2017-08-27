package com.tyrfing.games.id18.edit.battle;

import com.tyrfing.games.id18.edit.battle.action.DefeatUnitAction;
import com.tyrfing.games.id18.edit.unit.IModifierReactionRule;
import com.tyrfing.games.id18.model.battle.Battle;
import com.tyrfing.games.id18.model.unit.StatModifier;
import com.tyrfing.games.id18.model.unit.StatType;
import com.tyrfing.games.id18.model.unit.Unit;
import com.tyrfing.games.tyrlib3.edit.action.CompoundAction;
import com.tyrfing.games.tyrlib3.model.game.stats.IModifiable;

public class BattleReactionRule implements IModifierReactionRule{

	private Battle battle;
	
	public BattleReactionRule(Battle battle) {
		this.battle = battle;
	}
	
	@Override
	public void checkAndAppendActions(CompoundAction compoundAction, IModifiable<StatModifier> modifiable, StatModifier modifier) {
		if (modifiable instanceof Unit) {
			Unit unit = (Unit) modifiable;
			if (unit.getStats().get(StatType.HP) <= 0) {
				DefeatUnitAction defeatAction = new DefeatUnitAction(battle, unit);
				compoundAction.appendCurrentlyExecutingAction(defeatAction);
			}
		}
	}

}
