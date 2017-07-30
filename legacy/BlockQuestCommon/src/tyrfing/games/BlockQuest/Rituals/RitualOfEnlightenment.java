package tyrfing.games.BlockQuest.Rituals;

import tyrfing.games.BlockQuest.mechanics.State;

public class RitualOfEnlightenment implements Ritual {

	private static final int MONEY_COST = 1000;
	private static final int HP_COST = 25;
	private static final String DESCRIPTION = "Allows you to enlighten your followers\nin order to improve their skills.";
	
	public RitualOfEnlightenment() {
		super();
	}

	@Override
	public void acquire(State state) {
		state.tutorial.doItem("EnlightPower");
	}

	@Override
	public String getName() {
		return "Ritual of Enlightenment";
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
		return RitualType.RITUAL_OF_ENLIGHTENMENT;
	}

}
