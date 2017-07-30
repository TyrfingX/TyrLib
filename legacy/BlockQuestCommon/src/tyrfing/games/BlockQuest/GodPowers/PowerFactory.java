package tyrfing.games.BlockQuest.GodPowers;

import tyrfing.games.BlockQuest.Blessings.BlessingPower;
import tyrfing.games.BlockQuest.Rituals.RitualPower;
import tyrfing.games.BlockQuest.prayers.PrayerPower;

public class PowerFactory {
	public Power create(PowerType type)
	{
		Power power = null;
		
		switch (type)
		{
		case RITUALS:
			power = new RitualPower();
			break;
		case BLESSINGS:
			power = new BlessingPower();
			break;
		case PRAYER:
			power = new PrayerPower();
			break;
		}
		
		return power;
	}
}
