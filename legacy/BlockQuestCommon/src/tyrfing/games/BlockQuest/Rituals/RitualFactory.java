package tyrfing.games.BlockQuest.Rituals;


public class RitualFactory {
	public Ritual create(RitualType type)
	{
		Ritual ritual = null;
		
		switch(type)
		{
		case RITUAL_OF_BLESSINGS:
			ritual = new RitualOfBlessings();
			break;
		case RITUAL_OF_VISION:
			ritual = new RitualOfVision();
			break;
		case RITUAL_OF_ENLIGHTENMENT:
			ritual = new RitualOfEnlightenment();
			break;
		case RITUAL_OF_PRAYER:
			ritual = new RitualOfPrayer();
			break;
		case RITUAL_OF_ALCHEMY:
			ritual = new RitualOfAlchemy();
			break;
		}		
		
		return ritual;
	}
}
