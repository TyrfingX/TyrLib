package tyrfing.games.id3.lib;

import java.util.Observable;
import java.util.Observer;

import tyrfing.common.files.FileReader;
import tyrfing.common.game.BaseGame;
import tyrfing.common.math.Vector2;
import tyrfing.common.render.TargetMetrics;
import tyrfing.common.ui.ClickListener;
import tyrfing.common.ui.Event;
import tyrfing.common.ui.Window;
import tyrfing.common.ui.WindowManager;
import tyrfing.common.ui.widgets.Button;
import tyrfing.games.id3.lib.mechanics.State;

public class MainMenu implements ClickListener, Observer {
	
	private Button newCharacter;
	private Button continueOld;
	
	private State state;
	
	
	public MainMenu()
	{
		
		newCharacter = MenuConfig.createMenuItem("newCharacter", MainGame.getString(R.string.newgame), 0, this);
		continueOld = MenuConfig.createMenuItem("continue", BaseGame.getString(R.string.continueGame), 1, this);
		
	}
	
	public void display()
	{
		newCharacter.setVisible(true);
		continueOld.setVisible(true);

		newCharacter.moveTo(new Vector2(MenuConfig.LEFT, MenuConfig.TOP), MenuConfig.FADE_TIME);
		continueOld.moveTo(new Vector2(MenuConfig.LEFT, MenuConfig.TOP + MenuConfig.OFFSET), MenuConfig.FADE_TIME);
		newCharacter.getMovementListener(0).setListening(false);
		continueOld.getMovementListener(0).setListening(false);
		
		if (!FileReader.fileExists(BaseGame.CONTEXT, "character.bs"))
		{
			continueOld.disable();
		}
	}
	
	@Override
	public void onClick(Event event) {
		if 		(event.getEvoker() == newCharacter)
		{
			if (!continueOld.isEnabled())
			{
				this.startGame(false);
			}
			else
			{
				Window info = WindowManager.createYesNoMessageBox(newCharacter.getName() + "/infoBox",
						TargetMetrics.width * 0.1f, TargetMetrics.height * 0.25f,
						TargetMetrics.width * 0.8f, TargetMetrics.height * 0.35f, MainGame.getString(R.string.newGameConfirm));
				info.addClickListener(new ClickListener() {
					public void onClick(Event event) {	
						if (event.getParam("Result").equals("Yes"))
						{
							startGame(false);
						}
					}
				});
				WindowManager.makePopup(info, newCharacter.getName() + "/popup");	
			}
		}
		else if (event.getEvoker() == continueOld)
		{
			this.startGame(true);
		}
		
	}
	
	private void startGame(boolean continueOldSave)
	{
		
		newCharacter.disable();
		continueOld.disable();
		
		
		newCharacter.moveTo(new Vector2(-MenuConfig.WIDTH, MenuConfig.TOP), MenuConfig.FADE_TIME);
		continueOld.moveTo(new Vector2(TargetMetrics.width, MenuConfig.TOP + MenuConfig.OFFSET), MenuConfig.FADE_TIME);
		
		
		newCharacter.getMovementListener(0).setListening(true);
		continueOld.getMovementListener(0).setListening(true);
		
		this.state = new State(continueOldSave);

		
		state.worldMap.addObserver(this);
		
		if (!state.tutorial.isItemDone("Intro"))
		{
			state.tutorial.addObserver(state);
			state.tutorial.doIntro();
		}
		else
		{
			state.worldMap.show();
		}
	}


	@Override
	public void update(Observable arg0, Object arg1) {
		if (arg0 == state.worldMap)
		{
		}
		else
		{

		}
	}
	

}
