package tyrfing.games.id3.lib.World;

import java.util.ArrayList;

import android.graphics.Color;
import tyrfing.common.input.InputManager;
import tyrfing.common.math.Vector2;
import tyrfing.common.render.Ressources;
import tyrfing.common.render.SceneManager;
import tyrfing.common.render.TargetMetrics;
import tyrfing.common.renderables.Text;
import tyrfing.common.struct.Node;
import tyrfing.common.ui.Event;
import tyrfing.common.ui.Window;
import tyrfing.common.ui.WindowManager;
import tyrfing.common.ui.widgets.ImageBox;
import tyrfing.games.id3.lib.MainGame;
import tyrfing.games.id3.lib.MenuConfig;
import tyrfing.games.id3.lib.R;
import tyrfing.games.id3.lib.Blessings.BlessingMenu;
import tyrfing.games.id3.lib.GodPowers.Power;
import tyrfing.games.id3.lib.Rituals.Ritual;
import tyrfing.games.id3.lib.Rituals.RitualListener;
import tyrfing.games.id3.lib.mechanics.State;
import tyrfing.games.id3.lib.prayers.PrayerPower;

public class Shrine extends Location {

	private static final Vector2 shrinePos = new Vector2(-TargetMetrics.width*0.1f, TargetMetrics.width*0.3f);
	
	private static String header = "This is your shrine, nothing but a humongous stone\nwith random inscriptions scribbled on it.\nWhoever did this had not expected any results.\nA creation of despair - a creation of wishes...";
	
	private ArrayList<Text> messages;
	
	private ShrineAction action = ShrineAction.NOTHING;
	
	private BlessingMenu blessingMenu;
	
	public Shrine(State state) {
		super(MainGame.getString(R.string.shrine), state);
		messages = new ArrayList<Text>();
		blessingMenu = new BlessingMenu(state);
		back.setEnabled(false);
	}

	public void construct()
	{
		super.construct(shrinePos, R.drawable.shrine1, R.drawable.shrine1click, -TargetMetrics.height*0.085f);	
	}

	@Override
	public void onClick(Event event) {

		for (Text text : messages)
		{
			text.fadeOut(new Vector2(0,0), 0.5f);
		}
		
		for (Power power : state.powers)
		{
			power.hideMenu(MenuConfig.FADE_TIME);
		}
		
		if (event.getEvoker() == back)
		{
			this.back();
		}		
		else if (event.getEvoker().getName().equals("Power/Rituals/openMenu"))
		{
			
			Vector2 itemPos = new Vector2(MenuConfig.LEFT, MenuConfig.TOP + MenuConfig.OFFSET);
			Vector2 itemSize = new Vector2(MenuConfig.WIDTH, MenuConfig.HEIGHT);
			
			for (Ritual ritual : state.rituals)
			{
				
				Window window = WindowManager.getWindow("Ritual/" + ritual.getName());
				if (window == null)
				{
					window = WindowManager.createButton("Ritual/" + ritual.getName(), TargetMetrics.width, MenuConfig.TOP + MenuConfig.OFFSET, itemSize.x, itemSize.y, ritual.getName());			
					
					//Show Hp costs
					ImageBox cost = WindowManager.createImageBox(window.getName() + "/HpCost", window.getWidth()*0.4f, 10, 20, 20, Ressources.getScaledBitmap("heart", new Vector2(20,20)));
					window.addChild(cost);
					cost.addChild(WindowManager.createLabel(cost.getName() + "/label", 20, 5, 20, 20, ritual.getHpCost()+"", Color.TRANSPARENT));
					
					//Show Money costs
					cost = WindowManager.createImageBox(window.getName() + "/MoneyCost", window.getWidth()*0.4f+60, 10, 20, 20, Ressources.getScaledBitmap("money", new Vector2(20,20)));
					window.addChild(cost);
					cost.addChild(WindowManager.createLabel(cost.getName() + "/label", 20, 5, 20, 20, ritual.getMoneyCost()+"", Color.TRANSPARENT));
					
					window.addClickListener(new RitualListener(ritual, state));
				}
				
				window.moveTo(new Vector2(itemPos.x, itemPos.y), MenuConfig.FADE_TIME);
				window.setEnabled(true);
				itemPos.y += MenuConfig.OFFSET;
			}
			
			action = ShrineAction.RITUAL;
			
			if (!state.tutorial.isItemDone("Ritual"))
			{
				final String message = "Here you can perform Rituals.\nRituals allow you to gain access\nto new kinds of power.\n\nBut be careful!\nThey drain your follower's money and HP!";
				state.tutorial.createInfo(message);
				state.tutorial.doItem("Ritual");
			}
		}
		else if (event.getEvoker().getName().equals("Power/Blessings/openMenu"))
		{
			action = ShrineAction.BLESSING;
			blessingMenu.display();
		}
		else if (event.getEvoker().getName().equals("Power/Prayer/openMenu"))
		{
			action = ShrineAction.PRAYER;
			((PrayerPower) state.getPower("Prayer")).display();
		}
	}

	@Override
	public void openMenu() {
		
		back.enable();
		back.setVisible(true);
		back.moveTo(new Vector2(MenuConfig.LEFT, MenuConfig.TOP), MenuConfig.FADE_TIME);
		back.getMovementListener(0).setListening(false);
		
		InputManager.addBackListener(this);
		
		for (Text message : messages) {
			SceneManager.RENDER_THREAD.removeRenderable(message);
		}
		
		messages.clear();
		
		messages.add(SceneManager.createText(header, Color.BLACK, new Node(TargetMetrics.width*0.13f,TargetMetrics.height*0.075f)));
		
		for (Text message : messages) {
			message.blendIn(new Vector2(0,0), 0.5f);
		}
		
		Vector2 itemPos = new Vector2(MenuConfig.LEFT, MenuConfig.TOP + MenuConfig.OFFSET);
		Vector2 itemSize = new Vector2(MenuConfig.WIDTH, MenuConfig.HEIGHT);
		
		for (Power power : state.powers)
		{
			power.displayMenu(itemPos, itemSize, MenuConfig.FADE_TIME);
			power.setMenuListener(this);
			itemPos.y += MenuConfig.OFFSET;
		}
		
		if (state.hasPower("Prayer"))
		{
			PrayerPower prayerPower = (PrayerPower) state.getPower("Prayer");
			prayerPower.onDisplayMenu();
		}
	}

	public void back()
	{
		if (action == ShrineAction.NOTHING)
		{
			back.disable();
			back.moveTo(new Vector2(TargetMetrics.width, MenuConfig.TOP + MenuConfig.OFFSET), MenuConfig.FADE_TIME);
			back.getMovementListener(0).setListening(true);
			state.worldMap.show();
		
			InputManager.removeBackListener(this);
		}
		else if (action == ShrineAction.RITUAL)
		{
			for (Ritual ritual : state.rituals)
			{
				Window window = WindowManager.getWindow("Ritual/" + ritual.getName());
				window.moveTo(new Vector2(TargetMetrics.width, MenuConfig.TOP  + MenuConfig.OFFSET), MenuConfig.FADE_TIME);
				window.setEnabled(false);
			}
			this.openMenu();
		} else if (action == ShrineAction.BLESSING)
		{
			blessingMenu.hide();
			this.openMenu();
		}
		else if (action == ShrineAction.PRAYER)
		{
			((PrayerPower) state.getPower("Prayer")).hide();
			this.openMenu();
		}
		
		action = ShrineAction.NOTHING;
	}
	
	@Override
	public boolean onPressBack() {
		for (Text text : messages)
		{
			text.fadeOut(new Vector2(0,0), 0.5f);
		}
		
		for (Power power : state.powers)
		{
			power.hideMenu(MenuConfig.FADE_TIME);
		}
		this.back();
		return true;
	}
	
}
