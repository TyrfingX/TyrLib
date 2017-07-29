package com.tyrfing.games.id18.test.edit.ai;

import static org.junit.Assert.assertTrue;

import org.junit.Test;

import com.tyrfing.games.id18.edit.ai.AiActionProvider;
import com.tyrfing.games.id18.edit.ai.AiFactory;
import com.tyrfing.games.id18.edit.battle.BattleDomain;
import com.tyrfing.games.id18.edit.battle.BattleFactory;
import com.tyrfing.games.id18.edit.battle.action.DeployAction;
import com.tyrfing.games.id18.model.battle.Battle;
import com.tyrfing.games.id18.model.battle.DestroyEnemyObjective;
import com.tyrfing.games.id18.model.field.Field;
import com.tyrfing.games.id18.model.unit.Arte;
import com.tyrfing.games.id18.model.unit.Faction;
import com.tyrfing.games.id18.model.unit.StatModifier;
import com.tyrfing.games.id18.model.unit.StatType;
import com.tyrfing.games.id18.model.unit.Unit;
import com.tyrfing.games.tyrlib3.edit.ActionStack;
import com.tyrfing.games.tyrlib3.edit.action.IAction;
import com.tyrfing.games.tyrlib3.edit.action.IActionRequester;
import com.tyrfing.games.tyrlib3.math.Vector2I;

public class AiTest {
	
	@Test
	public void test() {
		Field field = new Field(new Vector2I(10, 10));
		
		BattleDomain battleDomain = BattleFactory.INSTANCE.createBattleDomain(field);
		Battle battle = battleDomain.getBattle();
		ActionStack actionStack = battleDomain.getActionStack();

		Faction factionAi = new Faction();
		Faction factionEnemy = new Faction();
		
		DestroyEnemyObjective objective = new DestroyEnemyObjective(factionAi);
		objective.getEnemyFactions().add(factionEnemy);
		battle.getObjectives().add(objective);
		
		Unit unitAi = new Unit();
		unitAi.setFaction(factionAi);
		unitAi.getStats().put(StatType.REMAINING_ACTIONS, 1);
		unitAi.getStats().put(StatType.REMAINING_MOVE, 10);
		Arte attack = new Arte("Attack");
		final int ENEMY_HP = 30;
		attack.setMinRange(1);
		attack.setMaxRange(1);
		attack.getEffectModifiers().add(StatModifier.createDamageModifier(ENEMY_HP));
		unitAi.getArtes().add(attack);
		
		final Vector2I INITIAL_POS_UNIT_AI = new Vector2I(3, 3);
		DeployAction deployUnitAiAction = new DeployAction(battle, unitAi, INITIAL_POS_UNIT_AI, Vector2I.UNIT_X);
		actionStack.execute(deployUnitAiAction);
		
		Unit unitEnemy = new Unit();
		unitEnemy.setFaction(factionEnemy);
		unitEnemy.getStats().put(StatType.HP, ENEMY_HP);
		
		final Vector2I INITIAL_POS_UNIT_ENEMY = new Vector2I(3, 6);
		DeployAction deployUnitEnemyAction = new DeployAction(battle, unitEnemy, INITIAL_POS_UNIT_ENEMY, Vector2I.UNIT_X);
		actionStack.execute(deployUnitEnemyAction);
		
		AiActionProvider ai = AiFactory.INSTANCE.createAi(battleDomain, factionAi);
		
		IActionRequester executer = new IActionRequester() {
			@Override
			public void onProvideRequest(IAction action) {
				actionStack.execute(action);
			}
		};
		
		ai.requestAction(executer);
		ai.requestAction(executer);
		ai.requestAction(executer);
		
		assertTrue("Enemy unit is dead", unitEnemy.getStats().get(StatType.HP) <= 0);
		assertTrue("Battle objectives achieved", battle.areObjectivesAchieved(factionAi));
	}
	
}
