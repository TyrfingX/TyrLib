package com.tyrfing.games.id18.test.edit.battle.action;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

import com.tyrfing.games.id18.edit.battle.action.EndTurnAction;
import com.tyrfing.games.id18.model.battle.Battle;
import com.tyrfing.games.id18.model.field.Field;
import com.tyrfing.games.id18.model.unit.StatType;
import com.tyrfing.games.id18.model.unit.Unit;
import com.tyrfing.games.tyrlib3.model.math.Vector2I;

public class EndTurnActionTest {
	
	@Test
	public void testExecute() {
		
		Battle battle = new Battle();
		Field field = new Field(new Vector2I(10, 10));
		battle.setField(field);
		
		final int INITIAL_REMAINING_MOVE = 5;
		final int INITIAL_REMAINING_ACTIONS = 1;
		final int MOVE = 8;
		final int ACTIONS = 2;
		
		Unit firstUnit = new Unit();
		battle.getWaitingUnits().add(firstUnit);
		
		firstUnit.getStats().put(StatType.REMAINING_MOVE, INITIAL_REMAINING_MOVE);
		firstUnit.getStats().put(StatType.REMAINING_ACTIONS, INITIAL_REMAINING_ACTIONS);
		
		Unit secondUnit = new Unit();
		secondUnit.getStats().put(StatType.MOVE, MOVE);
		secondUnit.getStats().put(StatType.ACTIONS, ACTIONS);
		battle.getWaitingUnits().add(secondUnit);
		
		EndTurnAction endTurnAction = new EndTurnAction(battle);
		
		endTurnAction.execute();
		
		assertEquals("Second unit is now the current unit", secondUnit, battle.getWaitingUnits().get(0));
		assertEquals("First unit is now waiting", firstUnit, battle.getWaitingUnits().get(1));
		assertEquals("Remaining move points set correctly", MOVE, secondUnit.getStats().get(StatType.REMAINING_MOVE).intValue());
		assertEquals("Remaining action points set correctly", ACTIONS, secondUnit.getStats().get(StatType.REMAINING_ACTIONS).intValue());
		
		endTurnAction.undo();
		
		assertEquals("First unit is now again the current unit", firstUnit, battle.getWaitingUnits().get(0));
		assertEquals("Second unit is now again waiting", secondUnit, battle.getWaitingUnits().get(1));
		assertEquals("Second unit is now again waiting", INITIAL_REMAINING_MOVE, firstUnit.getStats().get(StatType.REMAINING_MOVE).intValue());
		assertEquals("Second unit is now again waiting", INITIAL_REMAINING_ACTIONS, firstUnit.getStats().get(StatType.REMAINING_ACTIONS).intValue());
	}
}
