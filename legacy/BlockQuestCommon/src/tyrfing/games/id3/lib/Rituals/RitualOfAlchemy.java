package tyrfing.games.id3.lib.Rituals;

import tyrfing.games.id3.lib.mechanics.State;

public class RitualOfAlchemy implements Ritual {

	private static final int MONEY_COST = 500;
	private static final int HP_COST = 10;
	private static final String DESCRIPTION = "Grant your followers the power of alchemy.\nViolet crytsals can now be used to\nrefill empty potions by 20%.\nWill only be done if HP is already full.";
	
	public RitualOfAlchemy() {
		super();
	}

	@Override
	public void acquire(State state) {
		state.character.getStats().setStat("Refill", 1);
	}

	@Override
	public String getName() {
		return "Ritual of Alchemy";
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
