package com.tyrfing.games.id18.test.edit.battle.action;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

import com.tyrfing.games.id18.edit.battle.action.DeployAction;
import com.tyrfing.games.id18.model.battle.Battle;
import com.tyrfing.games.id18.model.field.Field;
import com.tyrfing.games.id18.model.unit.Unit;
import com.tyrfing.games.tyrlib3.model.math.Vector2I;

public class DeployActionTest {
	
	@Test
	public void testExecute() {
		Field field = new Field(new Vector2I(10, 10));
		Battle battle = new Battle();
		battle.setField(field);
		
		final Vector2I DEPLOY_POS = new Vector2I(5, 5);
		Unit unit = new Unit();
		
		DeployAction deployAction = new DeployAction(battle, unit, DEPLOY_POS, Vector2I.UNIT_X);
		deployAction.execute();
		
		assertEquals("Deployed unit has correct position", DEPLOY_POS, unit.getFieldPosition());
		assertEquals("Deployed unit has correct orientation", Vector2I.UNIT_X, unit.getFieldOrientation());
		assertEquals("Deployed unit has correct field", field, unit.getDeployedField());
		assertTrue("Deployed unit is listed by the field", field.getObjects().contains(unit));
		assertTrue("Deployed unit is listed by the mission", battle.getWaitingUnits().contains(unit));
		
		deployAction.undo();
		
		assertNull("Undeployed unit has no field anymore", unit.getDeployedField());
		assertFalse("Undeployed unit is not listed by the field", field.getObjects().contains(unit));
		assertFalse("Undeployed unit is not listed by the mission", battle.getWaitingUnits().contains(unit));
	}
}
