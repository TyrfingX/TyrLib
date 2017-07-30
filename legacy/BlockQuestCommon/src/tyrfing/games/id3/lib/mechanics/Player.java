package tyrfing.games.id3.lib.mechanics;

import tyrfing.common.files.FileReader;
import tyrfing.common.game.objects.Board;
import tyrfing.common.game.objects.Stats;
import tyrfing.common.struct.Node;
import tyrfing.games.id3.lib.MainGame;
import tyrfing.games.id3.lib.World.Market;
import tyrfing.games.id3.lib.rooms.content.Hero;

public class Player {
	private int money;
	private Stats stats;
	
	public Player()
	{
		money = 0;
		stats = new Stats();
		stats.setStat("MaxHp", 35);
		stats.setStat("Hp", stats.getStat("MaxHp"));
		stats.setStat("Atk", 3);
		stats.setStat("Level", 1);
		stats.setStat("NextExp", 10);
		stats.setStat("ExpPerLvl", 5);
		stats.setStat("Speed", 150);
		stats.setStat("TICKS_PER_SECOND", 5);
		stats.setStat("HpPotionEffect", Market.BASE_HP_POTION_EFFECT);
	}
	
	public Player(String data)
	{
		
		/***
		 * Creates a character based from a source file
		 * File has the following format:
		 * money_value;Attribute_1_Name:value;Attribute_2_Name:value;... etc
		 */
		
		
		String source = FileReader.readFile(MainGame.CONTEXT, data);
		
		stats = new Stats();
		
		String[] attributes = source.split(";");
		money = Integer.parseInt(attributes[0]);
		for (int i = 1; i < attributes.length; i++)
		{
			String pair = attributes[i];
			String[] splitPair = pair.split(":");
			stats.setStat(splitPair[0], Integer.parseInt(splitPair[1]));
		}
		
		if (stats.getStat("Speed") == 0) stats.setStat("Speed", 150);
		if (stats.getStat("HpPotionEffect") == 0) stats.setStat("HpPotionEffect", Market.BASE_HP_POTION_EFFECT);
		if (stats.getStat("TICKS_PER_SECOND") == 0) stats.setStat("TICKS_PER_SECOND", 5);
		
	}
	
	public String toString()
	{
		String res = "";
		res += money + ";";
		res += stats.toString(':', ';');
		return res;
	}
	
	public Hero createHero(Node node, Board board)
	{
		return new Hero(node, board, stats.copy());
	}
	
	public void synchWithHero(Hero hero)
	{
		stats.setStats(hero.getStats());
		stats.setStat("Hp", stats.getStat("MaxHp"));
	}
	
	public int getMoney()
	{
		return money;
	}
	
	public void setMoney(int money)
	{
		this.money = money;
	}
	
	public Stats getStats()
	{
		return stats;
	}
	
}
