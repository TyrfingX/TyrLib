package com.tyrfing.games.id18.test.edit.battle;

import static org.junit.Assert.assertTrue;

import java.io.IOException;
import java.net.UnknownHostException;

import org.junit.Before;
import org.junit.Test;

import com.tyrfing.games.id18.edit.ai.AiFactory;
import com.tyrfing.games.id18.edit.battle.BattleDomain;
import com.tyrfing.games.id18.edit.battle.BattleFactory;
import com.tyrfing.games.id18.edit.battle.action.EndTurnAction;
import com.tyrfing.games.id18.edit.faction.AFactionActionProvider;
import com.tyrfing.games.id18.edit.network.ActionSerializer;
import com.tyrfing.games.id18.edit.network.Client;
import com.tyrfing.games.id18.edit.network.Host;
import com.tyrfing.games.id18.edit.network.NetworkActionProvider;
import com.tyrfing.games.id18.edit.unit.UnitFactory;
import com.tyrfing.games.id18.model.battle.DestroyEnemyObjective;
import com.tyrfing.games.id18.model.field.Field;
import com.tyrfing.games.id18.model.unit.Arte;
import com.tyrfing.games.id18.model.unit.Faction;
import com.tyrfing.games.id18.model.unit.StatModifier;
import com.tyrfing.games.id18.model.unit.StatType;
import com.tyrfing.games.id18.model.unit.Unit;
import com.tyrfing.games.tyrlib3.edit.action.IActionRequester;
import com.tyrfing.games.tyrlib3.model.math.Vector2I;
import com.tyrfing.games.tyrlib3.model.networking.Connection;

public class BattleDomainTest {
	
	public static final int MAX_DEPTH = 4;

	public static final int SLEEP_TIME = 500;
	
	private BattleDomain battleDomain;
	private Faction faction1;
	private Faction faction2;
	private Unit unit1;
	private Unit unit2;
	
	@Before
	public void setup() {
		Field field = new Field(new Vector2I(10, 10));
		battleDomain = BattleFactory.INSTANCE.createBattleDomain(field);
		
		faction1 = BattleFactory.INSTANCE.createFaction(battleDomain);
		faction2 = BattleFactory.INSTANCE.createFaction(battleDomain);
		
		DestroyEnemyObjective objective1 = new DestroyEnemyObjective(faction1);
		objective1.getEnemyFactions().add(faction2);
		battleDomain.getBattle().getObjectives().add(objective1);
		
		DestroyEnemyObjective objective2 = new DestroyEnemyObjective(faction2);
		objective2.getEnemyFactions().add(faction1);
		battleDomain.getBattle().getObjectives().add(objective2);
		
		unit1 = UnitFactory.INSTANCE.createUnit(faction1);
		unit2 = UnitFactory.INSTANCE.createUnit(faction2);
		
		unit1.getStats().put(StatType.MOVE, 3);
		unit1.getStats().put(StatType.ACTIONS, 1);
		unit1.getStats().put(StatType.HP, 4);

		unit2.getStats().put(StatType.MOVE, 1);
		unit2.getStats().put(StatType.ACTIONS, 1);
		unit2.getStats().put(StatType.HP, 4);
		
		Arte attack = new Arte("Attack");
		attack.setMinRange(1);
		attack.setMaxRange(1);
		attack.getEffectModifiers().add(StatModifier.createDamageModifier(2));
		
		unit1.getArtes().add(attack);
		unit2.getArtes().add(attack);
		
		unit1.deploy(battleDomain.getBattle(), new Vector2I(3, 3), Vector2I.UNIT_X);
		unit2.deploy(battleDomain.getBattle(), new Vector2I(4, 4), Vector2I.NEGATIVE_UNIT_X);
	}
	
	@Test
	public void testOnUpdateAi() {
		AiFactory aiFactory = new AiFactory(MAX_DEPTH);
		aiFactory.createAi(battleDomain, faction1);
		aiFactory.createAi(battleDomain, faction2);
		
		battleDomain.startBattle();
		
		while(!battleDomain.isFinished()) {
			battleDomain.onUpdate(1);
		}
		
		assertTrue("Unit 1 is still alive", unit1.getStats().get(StatType.HP) > 0);
		assertTrue("Unit 2 is defeated", unit2.getStats().get(StatType.HP) <= 0);
	}
	
	@Test
	public void testOnUpdateNetwork() throws UnknownHostException, IOException, InterruptedException {
		AiFactory aiFactory = new AiFactory(MAX_DEPTH);
		aiFactory.createAi(battleDomain, faction1);
		
		final int PORT = 666;
		Host host = new Host(PORT);
		Client client = new Client();
		client.getNetwork().connectTo(host.getNetwork().getServer().getServerName(), PORT);
		
		Thread.sleep(SLEEP_TIME);
		
		Connection connection = host.getNetwork().getConnection(0);
		host.getNetwork().send(battleDomain.getBattle(), connection);
		
		Thread.sleep(SLEEP_TIME);
		
		client.setActionProvider(new AFactionActionProvider(faction2) {
			@Override
			public void requestAction(IActionRequester actionRequester) {
				actionRequester.onProvideRequest(new EndTurnAction(client.getBattleDomain().getBattle()));
			}
		});
		
		NetworkActionProvider networkActionProvider = new NetworkActionProvider(new ActionSerializer(battleDomain.getBattle()), faction2, connection);
		battleDomain.getFactionActionProviders().add(networkActionProvider);
		
		battleDomain.startBattle();
		
		while(!battleDomain.isFinished()) {
			System.out.println("-------------------------");
			System.out.println("Last Action: " + battleDomain.getActionStack().getActions().peek());
			System.out.println("Unit 1: HP=" + unit1.getStats().get(StatType.HP) +", X=" + unit1.getFieldPosition().x + ", Y=" + unit1.getFieldPosition().y);
			System.out.println("Unit 2: HP=" + unit2.getStats().get(StatType.HP) +", X=" + unit2.getFieldPosition().x + ", Y=" + unit2.getFieldPosition().y);
			
			battleDomain.onUpdate(1);
		}
		
		assertTrue("Unit 1 is still alive", unit1.getStats().get(StatType.HP) > 0);
		assertTrue("Unit 2 is defeated", unit2.getStats().get(StatType.HP) <= 0);
		
		host.getNetwork().close();
		client.getNetwork().close();
	}
}
