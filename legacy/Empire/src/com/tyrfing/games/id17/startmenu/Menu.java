package com.tyrfing.games.id17.startmenu;

import java.io.IOException;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.List;

import com.tyrfing.games.id17.EmpireFrameListener;
import com.tyrlib2.graphics.renderables.FormattedText2.ALIGNMENT;
import com.tyrlib2.graphics.scene.SceneManager;
import com.tyrlib2.gui.DestroyOnEvent;
import com.tyrlib2.gui.IEventListener;
import com.tyrlib2.gui.ImageBox;
import com.tyrlib2.gui.Label;
import com.tyrlib2.gui.ScaledVector2;
import com.tyrlib2.gui.WindowEvent;
import com.tyrlib2.gui.WindowEvent.WindowEventType;
import com.tyrlib2.gui.WindowManager;
import com.tyrlib2.main.Media;
import com.tyrlib2.math.Vector2;
import com.tyrlib2.networking.Network;
import com.tyrlib2.util.Color;

public class Menu {
	
	public static final ScaledVector2 OPTION_SIZE = new ScaledVector2(0.2f, 0.13f, 0);
	public static final Vector2 BASE_POS = new Vector2(0.1f, -0.9f);
	public static final ScaledVector2 OFFSET = new ScaledVector2(0, 0.24f, 1);
	
	private List<ImageBox> subMenus = new ArrayList<ImageBox>();
	
	private MatchMakerUI matchmaker;
	private NewGameUI newGameUI;;
	private Network matchmakerNetwork;
	
	public Menu() {
		
		matchmaker = new MatchMakerUI();
		newGameUI = new NewGameUI();
		
		final ImageBox option1 = (ImageBox) WindowManager.getInstance().createImageBox("NewGame", BASE_POS, "MAIN_GUI", "SMALL_RECT_WOOD", OPTION_SIZE);
		subMenus.add(option1);
		Label caption = (Label) WindowManager.getInstance().createLabel("NewGame/Label", new Vector2(OPTION_SIZE.get().multiply(0.5f).x, OPTION_SIZE.get().multiply(0.25f).y), "New");
		option1.addChild(caption);
		
		option1.addEventListener(WindowEventType.TOUCH, new IEventListener() {
			@Override
			public void onEvent(WindowEvent event) {
				option1.setReceiveTouchEvents(false);
				if (!newGameUI.main.isVisible()) {
					newGameUI.show();
				} else {
					newGameUI.hide();
				}
				
				if (matchmaker.isVisible()) {
					matchmaker.hide();
				}
			}
		});
		
		final ImageBox option2 = (ImageBox) WindowManager.getInstance().createImageBox("Load", BASE_POS.add(OFFSET.get()), "MAIN_GUI", "SMALL_RECT_WOOD", OPTION_SIZE);
		subMenus.add(option2);
		caption = (Label) WindowManager.getInstance().createLabel("Load/Label", new Vector2(OPTION_SIZE.get().multiply(0.5f).x, OPTION_SIZE.get().multiply(0.25f).y), "Load");
		option2.addChild(caption);
		
		option2.addEventListener(WindowEventType.TOUCH, new IEventListener() {
			@Override
			public void onEvent(WindowEvent event) {
				if (Media.CONTEXT.fileExists("/res/sav", "save.ser")) {
					option2.setReceiveTouchEvents(false);
					hostGame("save");
				}  else {
					Label label = (Label) WindowManager.getInstance().createLabel("  NO_SAVE  " + System.currentTimeMillis(), new Vector2(0.5f, -0.5f), "No save data");
					label.setColor(Color.WHITE);
					label.setBgColor(new Color(0.1f, 0.1f, 0.1f, 0.9f));
					label.fadeOut(0, 3);
					label.addEventListener(WindowEventType.FADE_OUT_FINISHED, new DestroyOnEvent());
				}
			}
		});
		
		final ImageBox option3 = (ImageBox) WindowManager.getInstance().createImageBox("Join", BASE_POS.add(OFFSET.get()).add(OFFSET.get()), "MAIN_GUI", "SMALL_RECT_WOOD", OPTION_SIZE);
		subMenus.add(option3);
		caption = (Label) WindowManager.getInstance().createLabel("Join/Label", new Vector2(OPTION_SIZE.get().multiply(0.5f).x, OPTION_SIZE.get().multiply(0.25f).y), "Join");
		option3.addChild(caption);
		
		option3.addEventListener(WindowEventType.TOUCH, new IEventListener() {
			@Override
			public void onEvent(WindowEvent event) {
				if (!matchmaker.isVisible()) {
					matchmaker.show();
				} else {
					matchmaker.hide();
				}
				
				if (newGameUI.main.isVisible()) {
					newGameUI.hide();
				}
			}
		});
		
		for (int i = 0; i < subMenus.size(); ++i) {
			final Label cap = (Label) subMenus.get(i).getChild(0);
			cap.setAlignment(ALIGNMENT.CENTER);
			cap.setFont(SceneManager.getInstance().getFont("FONT_20"));
			
			subMenus.get(i).setReceiveTouchEvents(true);
			
			subMenus.get(i).addEventListener(WindowEventType.TOUCH_ENTERS, new IEventListener() {
				@Override
				public void onEvent(WindowEvent event) {
					cap.setColor(Color.RED);
				}
			});
			
			subMenus.get(i).addEventListener(WindowEventType.TOUCH_LEAVES, new IEventListener() {
				@Override
				public void onEvent(WindowEvent event) {
					cap.setColor(Color.WHITE);
				}
			});
		}
		
		connectToMatchmaker();
		
		matchmaker.show();
	}
	
	public void hostGame(String save) {
		EmpireFrameListener.MAIN_FRAME.hostGameFromSave(save);
	}
	
	public void hide() {
		matchmakerNetwork.close();
		for (int i = 0; i < subMenus.size(); ++i) {
			subMenus.get(i).moveTo(new Vector2(-OPTION_SIZE.get().x*2, subMenus.get(i).getRelativePos().y), 0.5f);
		}
		
		if (matchmaker.isVisible()) {
			matchmaker.hide();
		}
		
		if (newGameUI.main.isVisible()) {
			newGameUI.hide();
		}
	}
	
	public void connectToMatchmaker() {
		if (matchmakerNetwork == null) {
			matchmakerNetwork = new Network();
			matchmakerNetwork.setLog(true);
			matchmakerNetwork.addListener(matchmaker);
			matchmakerNetwork.addListener(newGameUI);
			
			Thread connector = new Thread() {
				@Override
				public void run() {
					try {
						matchmakerNetwork.connectTo("swordscroll.com", 3000);
					} catch (UnknownHostException e) {
						e.printStackTrace();
					} catch (IOException e) {
						e.printStackTrace();
					}
				}
			};
			
			connector.start();
		}
	}
}
