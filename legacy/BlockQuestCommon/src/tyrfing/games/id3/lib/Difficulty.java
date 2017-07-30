package tyrfing.games.id3.lib;

public enum Difficulty {
	EASY, MEDIUM, HARD;
	
	
	public float getMonsterPowerUp()
	{
		switch (this)
		{
		case EASY:
			return 0.5f;
		case MEDIUM:
			return 1.0f;
		case HARD:
			return 2.0f;
		default:
			return 1.0f;
		}
	}
}
