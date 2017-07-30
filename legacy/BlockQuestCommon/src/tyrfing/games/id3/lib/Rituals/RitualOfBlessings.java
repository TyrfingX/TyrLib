package tyrfing.games.id3.lib.Rituals;

import tyrfing.common.render.TargetMetrics;
import tyrfing.common.ui.WindowManager;
import tyrfing.games.id3.lib.GodPowers.PowerFactory;
import tyrfing.games.id3.lib.GodPowers.PowerType;
import tyrfing.games.id3.lib.mechanics.State;

public class RitualOfBlessings implements Ritual {

	private static final int MONEY_COST = 100;
	private static final int HP_COST = 3;
	private static final String DESCRIPTION = "Allows you to protect your followers\nby blessing them with your godly\npower, granting them various\nbonuses.";
	
	public RitualOfBlessings() {
		super();
	}

	@Override
	public void acquire(State state) {
		final String text = "You have learned the blessing...\n\n \"Blessing of the Novice\"";
		WindowManager.makePopup(
				WindowManager.createConfirmMessageBox(	this.getName() + "/infoBox", 
											  			TargetMetrics.width * 0.1f, TargetMetrics.height * 0.25f,
											  			TargetMetrics.width * 0.8f, TargetMetrics.height * 0.35f, 
											  			text)
				, this.getName());
		PowerFactory powerFactory = new PowerFactory();
		state.powers.add(powerFactory.create(PowerType.BLESSINGS));
	}

	@Override
	public String getName() {
		return "Ritual of Blessings";
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
