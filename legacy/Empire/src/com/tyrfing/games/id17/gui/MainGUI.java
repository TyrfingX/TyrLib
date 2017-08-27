package com.tyrfing.games.id17.gui;

import com.tyrfing.games.id17.EmpireFrameListener;
import com.tyrfing.games.id17.gui.house.HouseGUI;
import com.tyrfing.games.id17.gui.mails.MailboxGUI;
import com.tyrfing.games.id17.startmenu.Menu;
import com.tyrlib2.graphics.renderables.FormattedText2.ALIGNMENT;
import com.tyrlib2.graphics.scene.SceneManager;
import com.tyrlib2.gui.IEventListener;
import com.tyrlib2.gui.Label;
import com.tyrlib2.gui.ScaledVector2;
import com.tyrlib2.gui.Window;
import com.tyrlib2.gui.WindowEvent;
import com.tyrlib2.gui.WindowEvent.WindowEventType;
import com.tyrlib2.gui.WindowManager;
import com.tyrlib2.math.Vector2;
import com.tyrlib2.util.Color;

public class MainGUI {
	
	public static final Vector2 HOUSE_WINDOW_POS = new Vector2(0.0f,-1);
	public static final Vector2 DIPLO_BUTTON_POS = new Vector2(0.3525f,-1);
	public static final ScaledVector2 ARMY_BUTTON_OFFSET = new ScaledVector2(0.42625f-0.3525f,0);

	
	public static final ScaledVector2 HOUSE_WINDOW_SIZE = new ScaledVector2(0.2875f, 0.25f);
	public static final ScaledVector2 DIPLO_BUTTON_SIZE = new ScaledVector2(0.06625f, 0.12f);
	public static final ScaledVector2 ARMY_BUTTON_SIZE = new ScaledVector2(0.06625f, 0.12f);
	
	public static final ScaledVector2 DIPLO_ICON_POS = DIPLO_BUTTON_SIZE.multiply(0.25f);
	public static final ScaledVector2 DIPLO_ICON_SIZE = DIPLO_BUTTON_SIZE.multiply(0.5f);
	
	public static final ScaledVector2 HOUSE_BUTTON_POS = new ScaledVector2(0.0125f, 0.01875f);
	public static final ScaledVector2 HOUSE_BUTTON_SIZE = new ScaledVector2(0.125f, 0.2083f);
	
	public static final ScaledVector2 JOIN_GAME_SIZE = Menu.OPTION_SIZE;
	public static final Vector2 JOIN_GAME_POS = new Vector2(0.7f, 0);
	
	private DateGUI dateGUI;
	public final PickerGUI pickerGUI;
	public MailboxGUI mailboxGUI;
	public HouseGUI houseGUI;
	
	private Window joinGame;
	
	public MainGUI() {
		
		houseGUI = new HouseGUI();
		pickerGUI = new PickerGUI();
		
		joinGame = WindowManager.getInstance().createImageBox("JoinGame", JOIN_GAME_POS, "MAIN_GUI", "SMALL_RECT_WOOD", JOIN_GAME_SIZE);
		
		String text = "Join Game";
		if (EmpireFrameListener.MAIN_FRAME.getNetwork().isHost()) {
			text = "Start Game";
		}
		
		final Label caption = (Label) WindowManager.getInstance().createLabel("JoinGame/Label", new Vector2(JOIN_GAME_SIZE.get().multiply(0.5f).x, JOIN_GAME_SIZE.get().multiply(0.25f).y), text);
		caption.setAlignment(ALIGNMENT.CENTER);
		caption.setFont(SceneManager.getInstance().getFont("FONT_20"));
		joinGame.addChild(caption);
		joinGame.moveTo(new Vector2(joinGame.getRelativePos().x, -joinGame.getSize().y), 0.5f);
		joinGame.setReceiveTouchEvents(true);
		
		joinGame.addEventListener(WindowEventType.TOUCH_ENTERS, new IEventListener() {
			@Override
			public void onEvent(WindowEvent event) {
				caption.setColor(Color.RED);
			}
		});
		
		joinGame.addEventListener(WindowEventType.TOUCH_LEAVES, new IEventListener() {
			@Override
			public void onEvent(WindowEvent event) {
				caption.setColor(Color.WHITE);
			}
		});
		
		joinGame.addEventListener(WindowEventType.TOUCH, new IEventListener() {
			@Override
			public void onEvent(WindowEvent event) {
				EmpireFrameListener.MAIN_FRAME.joinGame();
			}
		});
		
		buildDateGUI();
		
	}
	
	public void onEnterMainGame() {
		
	}
	
	public void onLeaveMainGame() {
		
	}
	
	private void buildDateGUI() {
		dateGUI = new DateGUI();
	}
	
	public DateGUI getDateGUI() {
		return dateGUI;
	}
	
	public void display() {
		houseGUI.show();
	}
	
	public void mainDisplay() {
		joinGame.moveTo(JOIN_GAME_POS, 0.5f);
		joinGame.setReceiveTouchEvents(false);
		
		mailboxGUI = new MailboxGUI();
	}
	
	public void hide() {
		
	}
	
	public void hideAllSubGUIs() {
		pickerGUI.armyGUI.hide();
		houseGUI.hide();
	}
}
