package tyrfing.games.id3.lib.GodPowers;

import tyrfing.games.id3.lib.Blessings.BlessingPower;
import tyrfing.games.id3.lib.Rituals.RitualPower;
import tyrfing.games.id3.lib.prayers.PrayerPower;

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
