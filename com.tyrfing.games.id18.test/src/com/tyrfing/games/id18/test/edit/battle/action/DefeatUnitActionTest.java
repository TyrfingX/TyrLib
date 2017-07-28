package com.tyrfing.games.id18.test.edit.battle.action;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.Before;
import org.junit.Test;

import com.tyrfing.games.id18.edit.battle.BattleDomain;
import com.tyrfing.games.id18.edit.battle.BattleFactory;
import com.tyrfing.games.id18.edit.battle.action.DefeatUnitAction;
import com.tyrfing.games.id18.edit.unit.action.AddStatModifierAction;
import com.tyrfing.games.id18.model.battle.Battle;
import com.tyrfing.games.id18.model.field.Field;
import com.tyrfing.games.id18.model.unit.StatModifier;
import com.tyrfing.games.id18.model.unit.StatType;
import com.tyrfing.games.id18.model.unit.Unit;
import com.tyrfing.games.tyrlib3.edit.ActionStack;
import com.tyrfing.games.tyrlib3.math.Vector2I;

public class DefeatUnitActionTest {
	
	private static final int INITIAL_HP = 30;
	private Unit unit;
	private ActionStack actionStack;
	private Battle battle;

	@Before
	public void setup() {
		Field field = new Field(new Vector2I(10,10));
		
		unit = new Unit();
		unit.getStats().put(StatType.HP, INITIAL_HP);
		
	
		BattleDomain battleDomain = BattleFactory.INSTANCE.createBattleDomain(field);
		battle = battleDomain.getBattle();
		battle.getWaitingUnits().add(unit);
		actionStack = battleDomain.getActionStack();
	}
	
	@Test
	public void testExecute() {
		DefeatUnitAction defeatUnitAction = new DefeatUnitAction(battle, unit);
		actionStack.execute(defeatUnitAction);
		
		assertFalse("Unit has been removed from the waiting queue", battle.getWaitingUnits().contains(unit));
		
		actionStack.undo();
		
		assertTrue("Unit has been readded to the waiting queue", battle.getWaitingUnits().contains(unit));
	}
	
	public void testTriggerDefeat() {
		StatModifier damage = StatModifier.createDamageModifier(INITIAL_HP);
		AddStatModifierAction addModifierAction = new AddStatModifierAction(unit, damage);
		
		actionStack.execute(addModifierAction);
		
		assertFalse("Unit has been removed from the waiting queue", battle.getWaitingUnits().contains(unit));
		
		actionStack.undo();
		
		assertTrue("Unit has been readded to the waiting queue", battle.getWaitingUnits().contains(unit));
	}
}
