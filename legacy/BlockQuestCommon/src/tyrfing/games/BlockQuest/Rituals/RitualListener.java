package tyrfing.games.BlockQuest.Rituals;

import tyrfing.common.render.TargetMetrics;
import tyrfing.common.ui.ClickListener;
import tyrfing.common.ui.Event;
import tyrfing.common.ui.Window;
import tyrfing.common.ui.WindowManager;
import tyrfing.games.BlockQuest.mechanics.State;

public class RitualListener implements ClickListener {

	
	private Ritual ritual;
	private State state;
	private Window popup;
	
	public RitualListener(Ritual ritual, State state)
	{
		this.ritual = ritual;
		this.state = state;
	}
	
	@Override
	public void onClick(Event event) {
		
		if (popup == null)
		{
		
			if (ritual.getMoneyCost() > state.character.getMoney()) 
			{
				RitualListener.notEnough("money");
			}
			else if (ritual.getHpCost() >= state.character.getStats().getStat("MaxHp"))
			{
				RitualListener.notEnough("Hp");
			}
			else
			{
				popup = WindowManager.createYesNoMessageBox(	"perfomRitual/infoBox", 
				  												TargetMetrics.width * 0.1f, TargetMetrics.height * 0.25f,
				  												TargetMetrics.width * 0.8f, TargetMetrics.height * 0.35f, 
				  												ritual.getDescription() + "\n\nDo you wish to perfrom the ritual?");
				popup.addClickListener(this);
				WindowManager.makePopup(popup, "performRital");
			}
			
		}
		else
		{
			String result = event.getParam("Result");
			if (result != null && result.equals("Yes"))
			{
				state.character.setMoney(state.character.getMoney() - ritual.getMoneyCost());
				state.character.getStats().setStat("MaxHp", state.character.getStats().getStat("MaxHp") - ritual.getHpCost());
				state.character.getStats().setStat("Hp", state.character.getStats().getStat("MaxHp"));
				state.playerMoney.setCaption(state.character.getMoney()+"");
				ritual.acquire(state);
				state.rituals.remove(ritual);
				WindowManager.destroyWindow("Ritual/" + ritual.getName());
				state.save();
			}
			popup = null;
		}
	}
	
	private static void notEnough(String ressource)
	{
		WindowManager.makePopup(
				WindowManager.createConfirmMessageBox(	"notEnough/infoBox", 
											  			TargetMetrics.width * 0.1f, TargetMetrics.height * 0.25f,
											  			TargetMetrics.width * 0.8f, TargetMetrics.height * 0.35f, 
											  			"Not enough " + ressource + "!")
				, "notEnough");
	}

}
