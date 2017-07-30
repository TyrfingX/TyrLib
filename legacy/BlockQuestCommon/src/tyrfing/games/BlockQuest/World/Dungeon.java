package tyrfing.games.BlockQuest.World;

import java.util.Observable;
import java.util.Observer;

import tyrfing.common.input.InputManager;
import tyrfing.common.math.Vector2;
import tyrfing.common.render.SceneManager;
import tyrfing.common.render.TargetMetrics;
import tyrfing.common.ui.Event;
import tyrfing.common.ui.widgets.Button;
import tyrfing.games.BlockQuest.boss.OrcBoss;
import tyrfing.games.BlockQuest.boss.UndeadBoss;
import tyrfing.games.BlockQuest.lib.BlockQuestActivity;
import tyrfing.games.BlockQuest.lib.MainGame;
import tyrfing.games.BlockQuest.lib.MainLogic;
import tyrfing.games.BlockQuest.lib.MenuConfig;
import tyrfingx.games.BlockQuest.lib.R;
import tyrfing.games.BlockQuest.mechanics.Floor;
import tyrfing.games.BlockQuest.mechanics.State;

public class Dungeon extends Location implements Observer {
	
	private Button startFloor;
	private Button startDeepestFloor;
	
	private static final Vector2 dungeonPos = new Vector2(TargetMetrics.width *0.5f, TargetMetrics.width*0.15f);
	
	private MainLogic currentGame;
	
	public Dungeon(State state)
	{
		super(MainGame.getString(R.string.dungeon),state);
		
		this.state = state;

		startFloor = MenuConfig.createMenuItem("startFloor", "Enter dungeon (Floor " + (state.deepestLevel-1) + ")", 1, this);
		startDeepestFloor = MenuConfig.createMenuItem("startDeepestFloor", "Enter dungeon (Floor " + state.deepestLevel + ")", 1, this);
		
	}
	
	public void construct()
	{
		super.construct(dungeonPos, R.drawable.dungeon, R.drawable.dungeonclick, -TargetMetrics.height*0.08f);	
	}
	
	public void openMenu()
	{
		
		back.enable();
		back.moveTo(new Vector2(MenuConfig.LEFT, MenuConfig.TOP), MenuConfig.FADE_TIME);
		back.getMovementListener(0).setListening(false);
		back.setVisible(true);
		
		InputManager.addBackListener(this);
		
		startFloor.enable();
		startFloor.setVisible(true);
		startFloor.getMovementListener(0).setListening(false);
		
		if (state.deepestLevel != 1)
		{
			startFloor.moveTo(new Vector2(MenuConfig.LEFT, MenuConfig.TOP + MenuConfig.OFFSET*2), MenuConfig.FADE_TIME);
		}		
		
		startDeepestFloor.enable();
		startDeepestFloor.moveTo(new Vector2(MenuConfig.LEFT, MenuConfig.TOP + MenuConfig.OFFSET * 3), MenuConfig.FADE_TIME);
		startDeepestFloor.setVisible(true);
		startDeepestFloor.getMovementListener(0).setListening(false);
		
		if (!state.tutorial.isItemDone("Dungeon"))
		{
			final String message = "Here you can accompany the adventurers\non their quest through the dungeon.\nAdvancing here will earn you power\nand influence.";
			state.tutorial.createInfo(message);
			state.tutorial.doItem("Dungeon");
		}
	}

	@Override
	public void onClick(Event event) {
		
		if (event.getEvoker() == startFloor || event.getEvoker() == startDeepestFloor)
		{
			startFloor.disable();
			startFloor.moveTo(new Vector2(TargetMetrics.width, MenuConfig.TOP + MenuConfig.OFFSET), MenuConfig.FADE_TIME);
			startFloor.getMovementListener(0).setListening(true);

			startDeepestFloor.disable();
			startDeepestFloor.moveTo(new Vector2(TargetMetrics.width, MenuConfig.TOP + MenuConfig.OFFSET), MenuConfig.FADE_TIME);
			startDeepestFloor.getMovementListener(0).setListening(true);
			
			back.disable();
			back.moveTo(new Vector2(TargetMetrics.width, MenuConfig.TOP + MenuConfig.OFFSET), MenuConfig.FADE_TIME);
			back.getMovementListener(0).setListening(true);
			
			if (event.getEvoker() == startFloor)
			{
				state.currentFloor = new Floor(state.deepestLevel-1);
			}
			else
			{
				state.currentFloor = new Floor(state.deepestLevel);
			}
			
			currentGame = new MainLogic(state, state.character, state.currentFloor, null);	
			if (state.currentFloor.getLevel() >= OrcBoss.LEVEL && !state.tutorial.isItemDone("OrcBoss"))
			{
				currentGame.addSkript(new OrcBoss(currentGame));
			} else if (state.currentFloor.getLevel() >= UndeadBoss.LEVEL && !state.tutorial.isItemDone("UndeadBoss"))
			{
				currentGame.addSkript(new UndeadBoss(currentGame));
			}
			SceneManager.RENDER_THREAD.addFrameListener(currentGame);
			currentGame.addObserver(this);
		}
		else if (event.getEvoker() == back)
		{
			this.closeMenu();
		}
	}

	@Override
	public void update(Observable arg0, Object arg1) {
		currentGame = null;
		if (state.currentFloor.cleared() && state.currentFloor.getLevel() == state.deepestLevel)
		{
			state.deepestLevel++;
			BlockQuestActivity.tracker.trackPageView("/Level" + state.deepestLevel);
			BlockQuestActivity.tracker.dispatch();
			startFloor.setCaption("Enter dungeon (Floor " + (state.deepestLevel-1) +  ")");
			startDeepestFloor.setCaption("Enter dungeon (Floor " + state.deepestLevel +  ")");
		}
		state.save();
		state.worldMap.show();
		InputManager.removeBackListener(this);
	}
	
	public void closeMenu()
	{
		
		InputManager.removeBackListener(this);
		
		startFloor.disable();
		startFloor.moveTo(new Vector2(TargetMetrics.width, MenuConfig.TOP + MenuConfig.OFFSET), MenuConfig.FADE_TIME);
		startFloor.getMovementListener(0).setListening(true);

		startDeepestFloor.disable();
		startDeepestFloor.moveTo(new Vector2(TargetMetrics.width, MenuConfig.TOP + MenuConfig.OFFSET), MenuConfig.FADE_TIME);
		startDeepestFloor.getMovementListener(0).setListening(true);
		
		back.disable();
		back.moveTo(new Vector2(TargetMetrics.width, MenuConfig.TOP + MenuConfig.OFFSET), MenuConfig.FADE_TIME);
		back.getMovementListener(0).setListening(true);
		
		state.worldMap.show();
	}

	@Override
	public boolean onPressBack() {
		if (currentGame == null)
		{
			this.closeMenu();
		}
		else
		{
			state.character.setMoney(currentGame.oldMoney);
			currentGame.exitDungeon("You are leaving your follower...");
		}
		return true;
	}
	

	
}
