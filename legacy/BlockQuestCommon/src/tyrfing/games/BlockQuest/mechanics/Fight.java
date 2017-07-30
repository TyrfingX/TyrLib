package tyrfing.games.BlockQuest.mechanics;

import tyrfing.common.game.objects.Stats;

public class Fight {
	
	protected Stats stats1;
	protected Stats stats2;
	
	public Fight(Stats stats1, Stats stats2)
	{
		this.stats1 = stats1;
		this.stats2 = stats2;
	}
	
	public Stats getStats1() {
		return stats1;
	}
	
	public Stats getStats2() {
		return stats2;
	}
	
	public void simulate(int turns)
	{
		while (stats1.getStat("Hp")  > 0 && stats2.getStat("Hp") > 0 && turns > 0)
		{
			this.dealDamage(stats1, stats2);
			this.dealDamage(stats2, stats1);
			--turns;
		}
		
		if (stats1.getStat("Hp") <= 0) {
			this.addEXP(stats2, stats1);
		}
		
		if (stats2.getStat("Hp") <= 0) {
			this.addEXP(stats1, stats2);
		}
		
		
	}
	
	private void dealDamage(Stats attacker, Stats defender)
	{
		int dmg = attacker.getStat("Atk") - defender.getStat("Def") * 2;
		if (dmg < 0) dmg = 1;
		defender.setStat("Hp", defender.getStat("Hp") - dmg);		
	
		if (defender.getStat("Hp") <= 0 && defender.getStat("PotionFill") > 0)
		{
			defender.setStat("PotionFill", 0);
			defender.setStat("Hp", (int)(defender.getStat("HpPotionEffect") * (defender.getStat("PotionFill")/5.0))+ defender.getStat("Hp"));
			if (defender.getStat("Hp") >= defender.getStat("MaxHp")) defender.setStat("Hp", defender.getStat("MaxHp"));
		}
		
		while (defender.getStat("Hp") <= 0 && defender.getStat("HpPotions") > 0)
		{
			defender.setStat("HpPotions", defender.getStat("HpPotions") - 1);
			defender.setStat("Hp", defender.getStat("HpPotionEffect") + defender.getStat("Hp"));
			if (defender.getStat("Hp") >= defender.getStat("MaxHp")) defender.setStat("Hp", defender.getStat("MaxHp"));
		}
	}
	
	private void addEXP(Stats receiver, Stats giver)
	{
		receiver.setStat("Exp", receiver.getStat("Exp") + (int)(giver.getStat("GiveExp") * (1+receiver.getStat("EXTRA_EXP")/100.f)));
	}
	
}
