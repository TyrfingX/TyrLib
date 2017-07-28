package com.tyrfing.games.id18.edit.battle;

import com.tyrfing.games.id18.edit.surface.SurfaceActionListener;
import com.tyrfing.games.id18.edit.unit.ModifierActionListener;
import com.tyrfing.games.id18.edit.unit.TagReactionRule;
import com.tyrfing.games.id18.model.battle.Battle;
import com.tyrfing.games.id18.model.field.Field;
import com.tyrfing.games.id18.model.unit.Faction;
import com.tyrfing.games.tyrlib3.edit.ActionStack;

public class BattleFactory  {
	public static BattleFactory INSTANCE = new BattleFactory();
	
	public ActionStack createBattleActionStack(Battle battle) {
		ActionStack actionStack = new ActionStack();
		
		ModifierActionListener modifierActionListener = new ModifierActionListener();
		modifierActionListener.getReactionRules().add(new TagReactionRule());
		modifierActionListener.getReactionRules().add(new BattleReactionRule(battle));

		actionStack.getActionListeners().add(modifierActionListener);
		actionStack.getActionListeners().add(new SurfaceActionListener(battle.getField()));
		
		return actionStack;
	}
	
	public BattleDomain createBattleDomain(Field field) {
		Battle battle = new Battle();
		battle.setField(field);
		
		BattleDomain domain = new BattleDomain(battle);
		return domain;
	}
	
	public Faction createFaction(BattleDomain battleDomain) {
		Faction faction = new Faction();
		battleDomain.getBattle().getFactions().add(faction);
		return faction;
	}
}
