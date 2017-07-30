package tyrfing.games.BlockQuest.lib;

import java.util.Observable;
import java.util.Observer;

import tyrfing.common.files.FileReader;
import tyrfing.common.game.BaseGame;
import tyrfing.common.math.Vector2;
import tyrfing.common.render.TargetMetrics;
import tyrfing.common.sound.SoundManager;
import tyrfing.common.sound.Soundtrack;
import tyrfing.common.ui.ClickListener;
import tyrfing.common.ui.Event;
import tyrfing.common.ui.Window;
import tyrfing.common.ui.WindowManager;
import tyrfing.common.ui.widgets.Button;
import tyrfing.games.BlockQuest.mechanics.State;
import tyrfingx.games.BlockQuest.lib.R;
import android.content.Intent;
import android.net.Uri;

public class MainMenu implements ClickListener, Observer {
	
	private Button newCharacter;
	private Button continueOld;
	
	private Button resume;
	private State state;
	
	private String resumeData = null;
	
	
	public MainMenu()
	{		
		
		SoundManager.getInstance().createSoundtrack(R.raw.world, "WORLD");
		
		newCharacter = MenuConfig.createMenuItem("newCharacter", MainGame.getString(R.string.newgame), 0, this);
		continueOld = MenuConfig.createMenuItem("continue", BaseGame.getString(R.string.continueGame), 1, this);
		
		if (FileReader.fileExists(BaseGame.CONTEXT, MainLogic.RESUME_FILE))
		{
			resumeData = FileReader.readFile(MainGame.CONTEXT, MainLogic.RESUME_FILE);
			if (resumeData.charAt(0) == MainLogic.RESUME_GAME)
			{
				resume = MenuConfig.createMenuItem("resume", MainGame.getString(R.string.resume), 3, this);
			}
		}
		
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

		if (resume != null)
		{
			resume.setVisible(true);
			resume.moveTo(new Vector2(MenuConfig.LEFT, MenuConfig.TOP + MenuConfig.OFFSET * 3), MenuConfig.FADE_TIME);
			resume.getMovementListener(0).setListening(false);
		}
	}
	
	@Override
	public void onClick(Event event) {
		if 		(event.getEvoker() == newCharacter)
		{
			if (!continueOld.isEnabled())
			{
				resumeData = null;
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
							resumeData = null;
							startGame(false);
						}
					}
				});
				WindowManager.makePopup(info, newCharacter.getName() + "/popup");	
			}
		}
		else if (event.getEvoker() == continueOld)
		{
			resumeData = null;
			this.startGame(true);
		}
		else if (event.getEvoker() == resume)
		{
			this.startGame(true);
		}
		
	}
	
	private void startGame(boolean continueOldSave)
	{
		
		Soundtrack track = SoundManager.getInstance().getSoundtrack("WORLD");
		track.play();
		
		newCharacter.disable();
		continueOld.disable();
		
		
		newCharacter.moveTo(new Vector2(-MenuConfig.WIDTH, MenuConfig.TOP), MenuConfig.FADE_TIME);
		continueOld.moveTo(new Vector2(TargetMetrics.width, MenuConfig.TOP + MenuConfig.OFFSET), MenuConfig.FADE_TIME);
		
		
		newCharacter.getMovementListener(0).setListening(true);
		continueOld.getMovementListener(0).setListening(true);
		
		if (resume != null)
		{
			resume.disable();
			resume.moveTo(new Vector2(TargetMetrics.width, MenuConfig.TOP + MenuConfig.OFFSET*3), MenuConfig.FADE_TIME);
			resume.getMovementListener(0).setListening(true);
		}
		
		this.state = new State(continueOldSave);
		
		
		
	//	state.deepestLevel = 6;
	//	state.character.setMoney(10000000);

		
		state.worldMap.addObserver(this);
		
		if (resumeData == null)
		{
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
		else
		{
			//User wants to resume his last game within the dungeon
			Thread thread = new Thread() {
				public void run()
				{
					state.createHeroPreview();
					state.playerMoney.setVisible(true);
					MainLogic.resume(resumeData, state);	
				}
			};
			thread.run();
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
