package com.tyrfing.games.id18.benchmark.ai;

import org.junit.Before;
import org.junit.Test;

import com.tyrfing.games.id18.edit.ai.AiFactory;
import com.tyrfing.games.id18.edit.battle.BattleDomain;
import com.tyrfing.games.id18.edit.battle.BattleFactory;
import com.tyrfing.games.id18.edit.unit.UnitFactory;
import com.tyrfing.games.id18.model.battle.DestroyEnemyObjective;
import com.tyrfing.games.id18.model.field.Field;
import com.tyrfing.games.id18.model.unit.Arte;
import com.tyrfing.games.id18.model.unit.Faction;
import com.tyrfing.games.id18.model.unit.StatModifier;
import com.tyrfing.games.id18.model.unit.StatType;
import com.tyrfing.games.id18.model.unit.Unit;
import com.tyrfing.games.tyrlib3.math.Vector2I;

public class Simple2UnitBenchmark {
	
	private BattleDomain battleDomain;
	private Faction factionAi1;
	private Faction factionAi2;

	@Before
	public void setup() {
		Field field = new Field(new Vector2I(10, 10));
		battleDomain = BattleFactory.INSTANCE.createBattleDomain(field);
		
		factionAi1 = BattleFactory.INSTANCE.createFaction(battleDomain);
		factionAi2 = BattleFactory.INSTANCE.createFaction(battleDomain);
		
		DestroyEnemyObjective objectiveAi1 = new DestroyEnemyObjective(factionAi1);
		objectiveAi1.getEnemyFactions().add(factionAi2);
		battleDomain.getBattle().getObjectives().add(objectiveAi1);
		
		DestroyEnemyObjective objectiveAi2 = new DestroyEnemyObjective(factionAi2);
		objectiveAi2.getEnemyFactions().add(factionAi1);
		battleDomain.getBattle().getObjectives().add(objectiveAi2);
		
		Unit unitAi1 = UnitFactory.INSTANCE.createUnit(factionAi1);
		Unit unitAi2 = UnitFactory.INSTANCE.createUnit(factionAi2);
		
		unitAi1.getStats().put(StatType.MOVE, 3);
		unitAi1.getStats().put(StatType.ACTIONS, 1);
		unitAi1.getStats().put(StatType.HP, 4);

		unitAi2.getStats().put(StatType.MOVE, 1);
		unitAi2.getStats().put(StatType.ACTIONS, 1);
		unitAi2.getStats().put(StatType.HP, 4);
		
		Arte attack = new Arte("Attack");
		attack.setMinRange(1);
		attack.setMaxRange(1);
		attack.getEffectModifiers().add(StatModifier.createDamageModifier(2));
		
		unitAi1.getArtes().add(attack);
		unitAi2.getArtes().add(attack);
		
		unitAi1.deploy(battleDomain.getBattle(), new Vector2I(3, 3), Vector2I.UNIT_X);
		unitAi2.deploy(battleDomain.getBattle(), new Vector2I(4, 4), Vector2I.NEGATIVE_UNIT_X);
	}
	
	@Test
	public void benchmarkDepth8() {
		final int MAX_DEPTH = 8;
		AiFactory aiFactory = new AiFactory(MAX_DEPTH);
		aiFactory.createAi(battleDomain, factionAi1);
		aiFactory.createAi(battleDomain, factionAi2);
		
		battleDomain.startBattle();
		
		while(!battleDomain.isFinished()) {
			battleDomain.onUpdate(1);
		}
	}
	
	@Test
	public void benchmarkDepth9() {
		final int MAX_DEPTH = 9;
		AiFactory aiFactory = new AiFactory(MAX_DEPTH);
		aiFactory.createAi(battleDomain, factionAi1);
		aiFactory.createAi(battleDomain, factionAi2);
		
		battleDomain.startBattle();
		
		while(!battleDomain.isFinished()) {
			battleDomain.onUpdate(1);
		}
	}
}
