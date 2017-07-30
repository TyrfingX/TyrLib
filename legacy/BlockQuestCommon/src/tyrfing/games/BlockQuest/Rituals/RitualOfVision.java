package tyrfing.games.BlockQuest.Rituals;


import tyrfing.games.BlockQuest.GodPowers.PowerFactory;
import tyrfing.games.BlockQuest.GodPowers.PowerType;
import tyrfing.games.BlockQuest.mechanics.State;

public class RitualOfVision implements Ritual {

	private static final int MONEY_COST = 800;
	private static final int HP_COST = 20;
	private static final String DESCRIPTION = "Allows you to appear in your follower's dreams\nand command them to do your bidding.";
	
	@Override
	public void acquire(State state) {
		state.tutorial.doItem("VisionPower");
	}

	@Override
	public String getName() {
		return "Ritual of Vision";
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
		return RitualType.RITUAL_OF_VISION;
	}

}
