package tyrfing.games.id3.lib.rooms.content;

import tyrfing.common.game.objects.Stats;
import tyrfing.common.math.Vector2;
import tyrfing.common.render.Ressources;
import tyrfing.common.render.SceneManager;

public class Monster extends StaticContent{
	private Stats stats;
	private String bitmap;
	
	private boolean countable = true;
	private MonsterFactory creator;
	private float passedTime;
	
	public Monster(MonsterFactory creator, Stats stats, String bitmap)
	{
		this.stats = stats;
		this.bitmap = bitmap;
		this.creator = creator;
	}
	
	public void createEntity(float size)
	{
		this.entity = SceneManager.createImage(Ressources.getBitmap(bitmap), node);
		this.entity.setPriority(10);
	}
	
	public Stats getStats()
	{
		return stats;
	}
	
	public void powerUp(int amount)
	{
		int level = amount + stats.getStat("Level");
		stats.setStat("Level", amount + level);
		stats.setStat("Hp", MonsterFactory.BASE_HP + (int)((amount + level) * MonsterFactory.HP_PER_LEVEL));
		stats.setStat("Atk", MonsterFactory.BASE_ATK + (int)((amount + level) * MonsterFactory.ATK_PER_LEVEL));
	}
	
	public void makeRevivable(int deathTime)
	{
		stats.setStat("DeathTime", deathTime);
		stats.setStat("MaxHp", stats.getStat("Hp"));
	}
	
	public void die()
	{
		if (stats.getStat("DeathTime") != 0)
		{
			creator.updater.addItem(this);
			SceneManager.RENDER_THREAD.removeRenderable(entity);
			this.entity = SceneManager.createAnimation(Ressources.getScaledBitmap("grave", new Vector2(entity.getWidth()*10, entity.getHeight())), 10, 3, node);
			this.entity.setPriority(10);
			this.countable = false;
			passedTime = 0;
		}
		else
		{
			this.remove();
		}
	}
	
	public boolean countable()
	{
		return countable;
	}
	
	public void onUpdate(float time)
	{
		if (stats.getStat("Hp") <= 0)
		{
			if (stats.getStat("DeathTime") != 0)
			{
				passedTime += time;
				if (passedTime >= stats.getStat("DeathTime"))
				{
					int maxHp = stats.getStat("MaxHp");
					stats.setStat("Hp", maxHp);
					SceneManager.RENDER_THREAD.removeRenderable(entity);
					this.createEntity(entity.getWidth());
					creator.updater.removeItem(this);
					this.countable = true;
					passedTime = 0;
				}
			}
		}
	}
	
	public void remove()
	{
		creator.updater.removeItem(this);
		super.remove();
	}
	
	public String toString()
	{
		String res 	 = "monster\n";
		res			+=	super.toString();
		res			+= stats.getStat("Type") + "\n";
		return res;
	}

	
}
