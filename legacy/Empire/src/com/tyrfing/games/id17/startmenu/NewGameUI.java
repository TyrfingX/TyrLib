package com.tyrfing.games.id17.startmenu;

import com.tyrfing.games.id17.EmpireFrameListener;
import com.tyrfing.games.id17.gui.PaperButton;
import com.tyrfing.games.id17.gui.mails.HeaderedMail;
import com.tyrlib2.graphics.scene.SceneManager;
import com.tyrlib2.gui.IEventListener;
import com.tyrlib2.gui.ImageBox;
import com.tyrlib2.gui.Label;
import com.tyrlib2.gui.ScaledVector1;
import com.tyrlib2.gui.ScaledVector1.ScaleDirection;
import com.tyrlib2.gui.ScaledVector2;
import com.tyrlib2.gui.Window;
import com.tyrlib2.gui.WindowEvent;
import com.tyrlib2.gui.WindowEvent.WindowEventType;
import com.tyrlib2.gui.WindowManager;
import com.tyrlib2.math.Vector2;
import com.tyrlib2.networking.Connection;
import com.tyrlib2.networking.INetworkListener;
import com.tyrlib2.util.Color;

public class NewGameUI implements INetworkListener {
	
	public static final Vector2 MAIN_WINDOW_POS = MatchMakerUI.POS;
	public static final Vector2 MAIN_WINDOW_SIZE = MatchMakerUI.SIZE;
	
	public static final String[] MAPS = {"small", "medium", "huge"};
	
	public static final String[] MAP_SIZE_OPTIONS = { "Small", "Medium", "Huge" };
	public static final String[] RANDOM_JOIN_OPTIONS = { "No", "Yes" };
	public static final String[] ALLOW_JOINS_OPTIONS = { "No", "Yes" };
	public static final String NAME = "NewGameUI";
	public static final String MAIN_NAME = NAME + "/Main";
	
	public static final int SCALING = 1;
	
	public static final ScaledVector1 HEADER_OFFSET = new ScaledVector1(0.06f, ScaleDirection.Y, SCALING);
	public static final ScaledVector1 OPTION_OFFSET = new ScaledVector1(0.17f, ScaleDirection.X, SCALING);
	
	public static final ScaledVector2 MAP_SIZE_OPTIONS_HEADER_POS = new ScaledVector2(0.05f, 0.08f, SCALING);
	public static final ScaledVector2 GAME_SPEED_OPTIONS_HEADER_POS = new ScaledVector2(0.05f, 0.28f, SCALING);
	public static final ScaledVector2 ALLOW_JOINS_OPTIONS_HEADER_POS = new ScaledVector2(0.05f, 0.48f, SCALING);
	public static final ScaledVector2 OPTION_SIZE = new ScaledVector2(0.06625f, 0.11f, 0);
	
	public static final float FADE_TIME = 0.5f;
	
	public static final ScaledVector2 ACCEPT_SIZE = new ScaledVector2(0.2f, 0.1f, 0);
	
	private ImageBox[] sizeOptions = new ImageBox[MAP_SIZE_OPTIONS.length];
	private ImageBox[] randomOptions = new ImageBox[RANDOM_JOIN_OPTIONS.length];
	private ImageBox[] allowJoinsOptions = new ImageBox[ALLOW_JOINS_OPTIONS.length];
	
	private ImageBox selectedSizeOption;
	private ImageBox selectedSpeedOption;
	private ImageBox selectedAllowJoinOption;
	
	private int selectedSize;
	private int selectedRandomJoin;
	private int selectedAllowJoins;

	private PaperButton accept;
	
	private Label allowJoins;
	
	protected Window main;
	
	private boolean allowJoinsEnabled = false;
	
	public NewGameUI() {
		main = WindowManager.getInstance().createFrame(MAIN_NAME, MAIN_WINDOW_POS, MAIN_WINDOW_SIZE);
	
		Label mapSizeHeader = (Label) WindowManager.getInstance().createLabel(	MAIN_NAME + "/SizeHeader", 
																				MAP_SIZE_OPTIONS_HEADER_POS, 
																				"Map Size");
		mapSizeHeader.setFont(SceneManager.getInstance().getFont("FONT_20"));
		main.addChild(mapSizeHeader);
		
		Vector2 pos = MAP_SIZE_OPTIONS_HEADER_POS.get().add(new Vector2(0, HEADER_OFFSET.get()));
		
		for (int i = 0; i < MAP_SIZE_OPTIONS.length; ++i) {
			sizeOptions[i] = (ImageBox) WindowManager.getInstance().createImageBox(MAIN_NAME+"/SizeOptions/"+ i, pos, "MAIN_GUI", "SMALL_CIRCLE_BORDER", OPTION_SIZE);
			main.addChild(sizeOptions[i]);
			Label optionText = (Label) WindowManager.getInstance().createLabel(MAIN_NAME+"/SizeOptionsLabel/"+ i, new Vector2(pos.x+OPTION_SIZE.get().x*1.1f,pos.y+OPTION_SIZE.get().y/5), MAP_SIZE_OPTIONS[i]);
			optionText.setReceiveTouchEvents(true);
			optionText.setFont(SceneManager.getInstance().getFont("FONT_20"));
			main.addChild(optionText);
			pos.x += OPTION_OFFSET.get();
			
			final int optionID = i;
			sizeOptions[i].addEventListener(WindowEventType.TOUCH_DOWN, new IEventListener() {
				@Override
				public void onEvent(WindowEvent event) {
					selectMapSize(optionID);
				}
			});
		}
		
		Label gameSpeedHeader = (Label) WindowManager.getInstance().createLabel(MAIN_NAME + "/SpeedHeader", 
																				GAME_SPEED_OPTIONS_HEADER_POS, 
																				"Random House");
		gameSpeedHeader.setFont(SceneManager.getInstance().getFont("FONT_20"));
		main.addChild(gameSpeedHeader);
		
		pos = GAME_SPEED_OPTIONS_HEADER_POS.get().add(new Vector2(0, HEADER_OFFSET.get()));
		
		for (int i = 0; i < RANDOM_JOIN_OPTIONS.length; ++i) {
			randomOptions[i] = (ImageBox) WindowManager.getInstance().createImageBox(MAIN_NAME+"/SpeedOptions/"+ i, pos, "MAIN_GUI", "SMALL_CIRCLE_BORDER", OPTION_SIZE);
			main.addChild(randomOptions[i]);
			Label optionText = (Label) WindowManager.getInstance().createLabel(MAIN_NAME+"/SpeedOptionsLabel/"+ i, new Vector2(pos.x+OPTION_SIZE.get().x*1.1f,pos.y+OPTION_SIZE.get().y/5), RANDOM_JOIN_OPTIONS[i]);
			optionText.setReceiveTouchEvents(true);
			optionText.setFont(SceneManager.getInstance().getFont("FONT_20"));
			main.addChild(optionText);
			pos.x += OPTION_OFFSET.get();
			
			final int optionID = i;
			IEventListener l = new IEventListener() {
				@Override
				public void onEvent(WindowEvent event) {
					selectRandomJoin(optionID);
				}
			};
			
			optionText.addEventListener(WindowEventType.TOUCH_DOWN, l);
			randomOptions[i].addEventListener(WindowEventType.TOUCH_DOWN, l);
		}
		
		allowJoins = (Label) WindowManager.getInstance().createLabel(	MAIN_NAME + "/AllowJoins", 
																			ALLOW_JOINS_OPTIONS_HEADER_POS, 
																			"Allow Joins (Checking Server Connection...)");
		allowJoins.setFont(SceneManager.getInstance().getFont("FONT_20"));
		main.addChild(allowJoins);
		
		pos = ALLOW_JOINS_OPTIONS_HEADER_POS.get().add(new Vector2(0, HEADER_OFFSET.get()));
		
		for (int i = 0; i < ALLOW_JOINS_OPTIONS.length; ++i) {
			allowJoinsOptions[i] = (ImageBox) WindowManager.getInstance().createImageBox(MAIN_NAME+"/AllowJoinsOptions/"+ i, pos, "MAIN_GUI", "SMALL_CIRCLE_BORDER", OPTION_SIZE);
			main.addChild(allowJoinsOptions[i]);
			Label optionText = (Label) WindowManager.getInstance().createLabel(MAIN_NAME+"/AllowJoinsOptionsLabel/"+ i, new Vector2(pos.x+OPTION_SIZE.get().x*1.1f,pos.y+OPTION_SIZE.get().y/5), ALLOW_JOINS_OPTIONS[i]);
			optionText.setReceiveTouchEvents(true);
			optionText.setFont(SceneManager.getInstance().getFont("FONT_20"));
			main.addChild(optionText);
			pos.x += OPTION_OFFSET.get();
			
			final int optionID = i;
			IEventListener l = new IEventListener() {
				@Override
				public void onEvent(WindowEvent event) {
					selectAllowJoins(optionID);
				}
			};
			
			optionText.addEventListener(WindowEventType.TOUCH_DOWN, l);
			allowJoinsOptions[i].addEventListener(WindowEventType.TOUCH_DOWN, l);
		}
	
		Vector2 acceptPos = new Vector2(MAIN_WINDOW_SIZE.x/2-ACCEPT_SIZE.get().x/2,MAIN_WINDOW_SIZE.y-ACCEPT_SIZE.get().y/2);
		accept = new PaperButton(MAIN_NAME + "/ACCEPT", acceptPos, ACCEPT_SIZE.get(), HeaderedMail.ACCEPT_BORDER_SIZE.get().x, "Start");
		main.addChild(accept);
		
		accept.addEventListener(WindowEventType.CONFIRMED, new IEventListener() {
			@Override
			public void onEvent(WindowEvent event) {
				hostGame();
			}
		});
		
		selectedSizeOption = (ImageBox) WindowManager.getInstance().createImageBox(MAIN_NAME + "/SelectedSize", new Vector2(), "MAIN_GUI", "CANCEL", OPTION_SIZE);
		selectedSpeedOption = (ImageBox) WindowManager.getInstance().createImageBox(MAIN_NAME + "/SelectedSpeed", new Vector2(), "MAIN_GUI", "CANCEL", OPTION_SIZE);
		selectedAllowJoinOption = (ImageBox) WindowManager.getInstance().createImageBox(MAIN_NAME + "/SelectedAllow", new Vector2(), "MAIN_GUI", "CANCEL", OPTION_SIZE);
		
		selectMapSize(0);
		selectRandomJoin(1);
		selectAllowJoins(0);
		
		disableAllowJoins();
		
		main.setAlpha(0);
		main.setVisible(false);
	}
	
	private void disableAllowJoins() {
		allowJoins.setColor(Color.GRAY);
		for (int i = 0; i < allowJoinsOptions.length; ++i) {
			allowJoinsOptions[i].setAtlasRegion("SMALL_CIRCLE_BORDER_DISABLED");
			Label text = (Label) WindowManager.getInstance().getWindow(MAIN_NAME+"/AllowJoinsOptionsLabel/"+ i);
			text.setColor(Color.GRAY);
			allowJoinsOptions[i].setReceiveTouchEvents(false);
			text.setReceiveTouchEvents(false);
		}
	}
	
	private void enableAllowJoins() {
		allowJoins.setColor(Color.GRAY);
		for (int i = 0; i < allowJoinsOptions.length; ++i) {
			allowJoinsOptions[i].setAtlasRegion("SMALL_CIRCLE_BORDER");
			Label text = (Label) WindowManager.getInstance().getWindow(MAIN_NAME+"/AllowJoinsOptionsLabel/"+ i);
			text.setColor(Color.WHITE);
			allowJoinsOptions[i].setReceiveTouchEvents(true);
			text.setReceiveTouchEvents(true);
		}
	}

	public void show() {
		main.fadeIn(1, FADE_TIME);
		accept.enable();
		optionsReceiveTouchEvents(true, sizeOptions);
		optionsReceiveTouchEvents(true, randomOptions);
		if (allowJoinsEnabled) {
			optionsReceiveTouchEvents(true, allowJoinsOptions);
		}
		
		EmpireFrameListener.MAIN_FRAME.setupHostNetwork();
		EmpireFrameListener.MAIN_FRAME.getNetwork().addListener(this);
	}
	
	public void hide() {
		main.fadeOut(0, FADE_TIME);	
		accept.disable();
		optionsReceiveTouchEvents(false, sizeOptions);
		optionsReceiveTouchEvents(false, randomOptions);
		optionsReceiveTouchEvents(false, allowJoinsOptions);
		
		EmpireFrameListener.MAIN_FRAME.getNetwork().removeListener(this);
	}
	
	private void optionsReceiveTouchEvents(boolean state, ImageBox[] options) {
		for (int i = 0; i < options.length; ++i) {
			options[i].setReceiveTouchEvents(state);
		}
	}
	
	public void selectMapSize(int i) {
		if (selectedSizeOption.getParent() != null) {
			selectedSizeOption.getParent().removeChild(selectedSizeOption);
		}
		sizeOptions[i].addChild(selectedSizeOption);
		selectedSize = i;
	}
	
	public void selectRandomJoin(int i) {
		if (selectedSpeedOption.getParent() != null) {
			selectedSpeedOption.getParent().removeChild(selectedSpeedOption);
		}
		randomOptions[i].addChild(selectedSpeedOption);
		selectedRandomJoin = i;
	}
	
	public void selectAllowJoins(int i) {
		if (selectedAllowJoinOption.getParent() != null) {
			selectedAllowJoinOption.getParent().removeChild(selectedAllowJoinOption);
		}
		allowJoinsOptions[i].addChild(selectedAllowJoinOption);
		selectedAllowJoins = i;
	}
	
	public void hostGame() {
		EmpireFrameListener.MAIN_FRAME.getNetwork().removeListener(this);
		EmpireFrameListener.MAIN_FRAME.hostGame(MAPS[selectedSize], selectedAllowJoins == 1, selectedRandomJoin == 1);
	}

	@Override
	public void onNewConnection(Connection c) {
	}

	@Override
	public void onConnectionLost(Connection c) {
	}

	@Override
	public void onReceivedData(Connection c, Object o) {
		if (o instanceof Integer) {
			Integer i = (Integer) o;
			if (i == EmpireFrameListener.CAN_HOST) {
				allowJoins.setText("Allow Joins (Server available)");
				enableAllowJoins();
			} else if (i == EmpireFrameListener.CANNOT_HOST) {
				disableAllowJoins();
				allowJoins.setText("Allow Joins (Server unavailable or Port " + EmpireFrameListener.SERVER_PORT + " not free)");
			}
		}
	}
}
