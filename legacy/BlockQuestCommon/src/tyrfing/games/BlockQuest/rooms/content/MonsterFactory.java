package tyrfing.games.BlockQuest.rooms.content;

import tyrfing.common.factory.IFactory;
import tyrfing.common.game.objects.Stats;
import tyrfing.common.game.objects.Updater;

public class MonsterFactory implements IFactory  {
	
	private int level;
	
	public static final int BASE_HP = 0;
	public static final float HP_PER_LEVEL = 2;
	public static final int BASE_ATK = 1;
	public static final float ATK_PER_LEVEL = 0.4f;
	
	private MonsterType type;
	private String bitmap;
	protected Updater updater;
	
	private float powerUp;
	
	public MonsterFactory(int level, MonsterType type, Updater updater, float powerUp)
	{
		this.level = level;
		this.type = type;
		this.powerUp = powerUp;
		
		switch (type)
		{
		case ORC:
			bitmap = "monster1";
			break;
		case DEMON:
			this.level *= 2;
			this.level += 10;
			bitmap = "demon";
			break;
		case ZOMBIE:
			bitmap = "monster2";
			break;
		case WISP:
			bitmap = "wisp";
		case ORCBOSS:
			bitmap = "OrcBoss";
			this.level *= 5;
			break;
		case UNDEADBOSS:
			bitmap = "UndeadBoss";
			this.level *= 5;
			break;
		}
		
		this.updater = updater;
	}
	
	public Object create()
	{
		Stats stats = new Stats();
		Monster monster = new Monster(this, stats, bitmap);		
		
		if (type == MonsterType.ZOMBIE)
		{
			monster.makeRevivable(30);
			stats.setStat("GiveExp", (int)(level/0.75f));
		}
		else
		{
			stats.setStat("GiveExp", level);			
		}

		stats.setStat("Level", level);
		
		if (type == MonsterType.WISP)
		{
			stats.setStat("MaxHp", BASE_HP + (int)(level*powerUp*HP_PER_LEVEL/8));
			stats.setStat("Hp", BASE_HP + (int)((level*powerUp*HP_PER_LEVEL/8)));
			stats.setStat("Atk", BASE_ATK + (int)((level*powerUp) * ATK_PER_LEVEL*8));			
		}
		else
		{
			stats.setStat("MaxHp", BASE_HP + (int)((level*powerUp) * HP_PER_LEVEL));
			stats.setStat("Hp", BASE_HP + (int)((level*powerUp) * HP_PER_LEVEL));
			stats.setStat("Atk", BASE_ATK + (int)((level*powerUp) * ATK_PER_LEVEL));			
		}


		int typeNr = type.ordinal();
		stats.setStat("Type", typeNr);
		
		return monster;
	}
	

}
