package tyrfing.games.id3.lib.Rituals;

import tyrfing.games.id3.lib.GodPowers.PowerFactory;
import tyrfing.games.id3.lib.GodPowers.PowerType;
import tyrfing.games.id3.lib.mechanics.State;
import tyrfing.games.id3.lib.prayers.PrayerPower;
import tyrfing.games.id3.lib.prayers.PrayerType;

public class RitualOfPrayer implements Ritual {

	private static final int MONEY_COST = 500;
	private static final int HP_COST = 5;
	private static final String DESCRIPTION = "Receive the power to listen\nto your follower's pleas for help,\nshould they encounter difficulties within\nthe dungeons.";
	
	public RitualOfPrayer() {
		super();
	}

	@Override
	public void acquire(State state) {
		PowerFactory powerFactory = new PowerFactory();
		state.powers.add(powerFactory.create(PowerType.PRAYER));
		PrayerPower power = (PrayerPower)state.getPower("Prayer");
		power.setState(state);
		power.addPrayer(PrayerType.LOST, (int)(Math.random()*(state.deepestLevel-5))+5);
		power.addPrayer(PrayerType.RUN, (int)(Math.random()*(state.deepestLevel-5))+5);
	}

	@Override
	public String getName() {
		return "Ritual of Prayer";
	}

	@Override
	public int getMoneyCost() {
		return MONEY_COST;
	}

	@Override
	public int getHpCost() {
		return HP_COST;
	}
	
	public String getDescription()
	{
		return DESCRIPTION;
	}

	@Override
	public RitualType getType() {
		return RitualType.RITUAL_OF_BLESSINGS;
	}
	
}
