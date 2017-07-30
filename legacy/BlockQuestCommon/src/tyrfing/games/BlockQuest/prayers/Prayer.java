package tyrfing.games.BlockQuest.prayers;

import java.util.Observable;
import java.util.Observer;

import android.graphics.Color;

import tyrfing.common.input.BackListener;
import tyrfing.common.input.InputManager;
import tyrfing.common.math.Vector2;
import tyrfing.common.render.Ressources;
import tyrfing.common.render.SceneManager;
import tyrfing.common.render.TargetMetrics;
import tyrfing.common.renderables.Image;
import tyrfing.common.renderables.Text;
import tyrfing.common.struct.Node;
import tyrfing.common.ui.ClickListener;
import tyrfing.common.ui.Event;
import tyrfing.common.ui.Window;
import tyrfing.common.ui.WindowManager;
import tyrfing.common.ui.widgets.Button;
import tyrfing.common.ui.widgets.ImageBox;
import tyrfing.common.ui.widgets.Label;
import tyrfing.games.BlockQuest.lib.MainGame;
import tyrfing.games.BlockQuest.lib.MainLogic;
import tyrfing.games.BlockQuest.lib.MenuConfig;
import tyrfingx.games.BlockQuest.lib.R;
import tyrfing.games.BlockQuest.lib.Settings;
import tyrfing.games.BlockQuest.mechanics.Floor;
import tyrfing.games.BlockQuest.mechanics.State;

public abstract class Prayer implements Observer, ClickListener, BackListener{
	
	protected Button prayerButton;
	
	protected MainLogic currentGame;
	
	public abstract String getName();
	protected abstract String getDesc();
	protected abstract Settings setupDungeon();
	protected abstract PrayerType getType();
	public abstract int getReward();
	public abstract int getLevel();
	
	protected State state;
	protected int pos;
	protected PrayerPower prayerPower;

	public Prayer(int pos, State state, PrayerPower prayerPower)
	{
		this.state = state;
		this.pos = pos;
		this.prayerPower = prayerPower;	
	}
	
	protected void build() {
		
		prayerButton = MenuConfig.createMenuItem("Prayers/" + this.getName() + "/" + PrayerPower.receivedPrayers, this.getName(), pos, this);

		//Show level
		Label level = WindowManager.createLabel(prayerButton.getName() + "/level", 20, 5, 20, 20, "Lvl: " + this.getLevel(), Color.TRANSPARENT);
		prayerButton.addChild(level);
		
		//Show reward
		ImageBox reward = WindowManager.createImageBox(prayerButton.getName() + "/reward", prayerButton.getWidth()*0.65f, 10, 20, 20, Ressources.getScaledBitmap("money", new Vector2(20,20)));
		prayerButton.addChild(reward);
		
		reward.addChild(WindowManager.createLabel(reward.getName() + "/label", 20, 5, 20, 20, this.getReward() +"", Color.TRANSPARENT));
	}
	
	public void displayButton() {
		prayerButton.enable();
		prayerButton.setVisible(true);
		prayerButton.moveTo(new Vector2(MenuConfig.LEFT, MenuConfig.TOP + MenuConfig.OFFSET*(pos-1)), MenuConfig.FADE_TIME);
		prayerButton.getMovementListener(0).setListening(false);
	}
	
	public void hideButton() {
		prayerButton.disable();
		prayerButton.moveTo(new Vector2( TargetMetrics.width, prayerButton.getY()), MenuConfig.FADE_TIME);
		prayerButton.getMovementListener(0).setListening(true);
	}
	
	public void moveUp()
	{
		pos--;
	}
	
	@Override
	public void onClick(Event event) {
		final Prayer prayer = this;
		Window info = WindowManager.createYesNoMessageBox(this.getName() + "/infoBox",
				TargetMetrics.width * 0.1f, TargetMetrics.height * 0.25f,
				TargetMetrics.width * 0.8f, TargetMetrics.height * 0.35f, this.getDesc());
		info.addClickListener(new ClickListener() {
			public void onClick(Event event) {	
				if (event.getParam("Result").equals("Yes"))
				{
					InputManager.addBackListener(prayer);
					prayerPower.hide();
					state.currentFloor = new Floor(prayer.getLevel());
					Settings settings = prayer.setupDungeon();
					currentGame = new MainLogic(state, state.character, state.currentFloor, settings);
					SceneManager.RENDER_THREAD.addFrameListener(currentGame);
					currentGame.addObserver(prayer);
					Window back = WindowManager.getWindow(MainGame.getString(R.string.shrine) + "\back");
					back.disable();
					back.fadeOut(0.5f);
				}
			}
		});
		WindowManager.makePopup(info, this.getName());		
	}
	
	@Override
	public void update(Observable arg0, Object arg1) {
		currentGame = null;
		state.save();
		InputManager.removeBackListener(this);
		prayerPower.removePrayer(this);
		prayerPower.display();
		Window back = WindowManager.getWindow(MainGame.getString(R.string.shrine) + "\back");
		back.enable();
		back.blendIn(0.5f);
		
		if (state.currentFloor.cleared())
		{
			state.character.setMoney(state.character.getMoney() + this.getReward());
			state.playerMoney.setCaption(state.character.getMoney()+"");
			state.save();
			
			Text rewardMessage = SceneManager.createText("+" + this.getReward(), Color.YELLOW, new Node(TargetMetrics.width * 0.25f,TargetMetrics.height*0.5f));
			rewardMessage.setSize(35);
			rewardMessage.fadeOut(new Vector2(0, -20), 5);
			rewardMessage.setPriority(WindowManager.OVERLAY_LAYER+10000);	
			
			Image moneyImg = SceneManager.createImage(Ressources.getScaledBitmap("money", new Vector2(50,50)), rewardMessage.getParent());
			moneyImg.fadeOut(new Vector2(0,-20), 5);
			moneyImg.setPriority(WindowManager.OVERLAY_LAYER+10000);
		}
	}
	
	@Override
	public boolean onPressBack() {
		if (currentGame != null)
		{
			currentGame.exitDungeon("You are leaving your follower...");
		}
		return true;
	}
	
}
