package com.tyrfing.games.id18.test.edit.unit.action;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import org.junit.Before;
import org.junit.Test;

import com.tyrfing.games.id18.edit.unit.action.AddStatModifierAction;
import com.tyrfing.games.id18.model.unit.StatModifier;
import com.tyrfing.games.id18.model.unit.StatType;
import com.tyrfing.games.id18.model.unit.Unit;

public class AddStatModifierActionTest {

	public static final int INITIAL_HP = 30;
	
	private Unit unit;
	
	@Before
	public void setup() {
		unit = new Unit();
		unit.getStats().put(StatType.HP, INITIAL_HP);
	}

	@Test
	public void testExecute() {
		final int DAMAGE = 10;
		StatModifier statModifier = new StatModifier("Damage", StatType.HP, -DAMAGE, true);
		AddStatModifierAction addStatModifierAction = new AddStatModifierAction(unit, statModifier);
	
		addStatModifierAction.execute();
		
		assertTrue("Unit has damage modifier", unit.getModifiers().contains(statModifier));
		assertEquals("Unit has correct amount of HP left", INITIAL_HP - DAMAGE, unit.getStats().get(StatType.HP).intValue());
	
		addStatModifierAction.undo();
		
		assertTrue("Unit has no more modifiesr", unit.getModifiers().isEmpty());
		assertEquals("Unit has correct amount of HP back", INITIAL_HP, unit.getStats().get(StatType.HP).intValue());
	
	}
}
