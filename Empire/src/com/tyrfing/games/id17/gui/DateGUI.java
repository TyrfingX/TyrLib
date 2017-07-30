package com.tyrfing.games.id17.gui;

import java.util.ArrayList;
import java.util.List;

import com.tyrfing.games.id17.EmpireFrameListener;
import com.tyrfing.games.id17.world.World;
import com.tyrlib2.gui.IEventListener;
import com.tyrlib2.gui.ImageBox;
import com.tyrlib2.gui.ScaledVector2;
import com.tyrlib2.gui.Window;
import com.tyrlib2.gui.WindowEvent;
import com.tyrlib2.gui.WindowManager;
import com.tyrlib2.math.Vector2;

public class DateGUI {
	
	public static final Vector2 FAST_FORWARD_BUTTON_POS = new Vector2(0,0);
	public static final ScaledVector2 FAST_FORWARD_BUTTON_OFFSET = new ScaledVector2(0.01f,-0.14f, 3);
	public static final Vector2 DATE_WINDOW_POS = new Vector2(1,-1);
	
	public static final ScaledVector2 FAST_FORWARD_BUTTON_SIZE = new ScaledVector2(0.06625f, 0.11f, 3);
	public static final ScaledVector2 _FAST_FORWARD_BUTTON_SIZE = new ScaledVector2(14, 6, 5);
	
	public static final ScaledVector2 DATE_WINDOW_SIZE = new ScaledVector2(164, 32, 5);
	public static final ScaledVector2 DATE_WINDOW_OFFSET = new ScaledVector2(-TabGUI.HEADER_POS.x, TabGUI.HEADER_POS.y,5);
	
	public static final ScaledVector2 DATE_POS = new ScaledVector2(DATE_WINDOW_SIZE.x * 0.25f, DATE_WINDOW_SIZE.y * 0.2f, 5);
	
	public static final ScaledVector2 PLAY_BUTTON_POS = new ScaledVector2(DATE_WINDOW_SIZE.x * 0.8f, DATE_WINDOW_SIZE.y * 0.2f, 5);
	public static final ScaledVector2 PLAY_BUTTON_SIZE = new ScaledVector2(DATE_WINDOW_SIZE.x * 0.15f, DATE_WINDOW_SIZE.y * 0.6f, 5);
	
	public static final ScaledVector2 FAST_FORWARD_BUTTON_POS_ = new ScaledVector2(DATE_WINDOW_SIZE.x * 0.05f, DATE_WINDOW_SIZE.y * 0.29f, 5);
	public static final ScaledVector2 SPEED_BUTTON_POS = new ScaledVector2(_FAST_FORWARD_BUTTON_SIZE.x * 0.1f, _FAST_FORWARD_BUTTON_SIZE.y*2f, 5 );
	public static final ScaledVector2 SPEED_BUTTON_SIZE = _FAST_FORWARD_BUTTON_SIZE;
	public static final ScaledVector2 SPEED_BUTTON_OFFSET = new ScaledVector2(0, -SPEED_BUTTON_SIZE.y, 5);
	
	
	private ImageBox play;
	private Window dateHolder;
	private boolean touchStartedInWindow = false;
	private List<ImageBox> speedIcons = new ArrayList<ImageBox>();
	private Window speedHolder;
	
	public DateGUI() {
		
		Vector2 basePos = DATE_WINDOW_POS.add(DATE_WINDOW_OFFSET.get());
		dateHolder = WindowManager.getInstance().createRectWindow(	"DATE_WINDOW", 
																	new Vector2(basePos.x - DATE_WINDOW_SIZE.get().x, basePos.y), 
																	DATE_WINDOW_SIZE.get(), TabGUI.GOLDEN_WOOD_PAINT);
		
		speedHolder = WindowManager.getInstance().createWindow("FAST_FORWARD_BUTTON", FAST_FORWARD_BUTTON_POS_.get(), FAST_FORWARD_BUTTON_SIZE.get());
		
		play = (ImageBox) WindowManager.getInstance().createImageBox("DATE_WINDOW/PLAY_BUTTON", PLAY_BUTTON_POS.get(), "MAIN_GUI", "PAUSE", PLAY_BUTTON_SIZE.get());
		WindowManager.getInstance().getWindow("DATE_WINDOW").addChild(WindowManager.getInstance().getWindow("DATE_WINDOW/PLAY_BUTTON"));
		
		speedIcons.add((ImageBox)WindowManager.getInstance().createImageBox("FAST_FORWARD_BUTTON/SPEED_ICON1", SPEED_BUTTON_POS.get(), "MAIN_GUI", "PLAY_SPEED", SPEED_BUTTON_SIZE.get()));
		WindowManager.getInstance().getWindow("FAST_FORWARD_BUTTON").addChild(WindowManager.getInstance().getWindow("FAST_FORWARD_BUTTON/SPEED_ICON1"));
		
		speedIcons.add((ImageBox)WindowManager.getInstance().createImageBox("FAST_FORWARD_BUTTON/SPEED_ICON2", SPEED_BUTTON_POS.add(SPEED_BUTTON_OFFSET).get(), "MAIN_GUI", "PLAY_SPEED", SPEED_BUTTON_SIZE.get()));
		WindowManager.getInstance().getWindow("FAST_FORWARD_BUTTON").addChild(WindowManager.getInstance().getWindow("FAST_FORWARD_BUTTON/SPEED_ICON2"));
		
		speedIcons.add((ImageBox)WindowManager.getInstance().createImageBox("FAST_FORWARD_BUTTON/SPEED_ICON3", SPEED_BUTTON_POS.add(SPEED_BUTTON_OFFSET.multiply(2)).get(), "MAIN_GUI", "PLAY_SPEED", SPEED_BUTTON_SIZE.get()));
		WindowManager.getInstance().getWindow("FAST_FORWARD_BUTTON").addChild(WindowManager.getInstance().getWindow("FAST_FORWARD_BUTTON/SPEED_ICON3"));
		
		speedIcons.add((ImageBox)WindowManager.getInstance().createImageBox("FAST_FORWARD_BUTTON/SPEED_ICON4", SPEED_BUTTON_POS.add(SPEED_BUTTON_OFFSET.multiply(3)).get(), "MAIN_GUI", "PLAY_SPEED", SPEED_BUTTON_SIZE.get()));
		WindowManager.getInstance().getWindow("FAST_FORWARD_BUTTON").addChild(WindowManager.getInstance().getWindow("FAST_FORWARD_BUTTON/SPEED_ICON4"));
		
		speedIcons.get(2).setVisible(false);
		speedIcons.get(3).setVisible(false);
		
		Window dateLabel = WindowManager.getInstance().createLabel("DATE_WINDOW/DATE", DATE_POS, World.getInstance().getDate());
		dateHolder.addChild(dateLabel);
		WindowManager.getInstance().addTextTooltip(dateLabel, "");
		dateLabel.setReceiveTouchEvents(true);
		dateHolder.setReceiveTouchEvents(true);
		
		if (EmpireFrameListener.MAIN_FRAME.getNetwork().isHost()) {
		
			dateHolder.addEventListener(WindowEvent.WindowEventType.TOUCH_DOWN, new IEventListener() {
				@Override
				public void onEvent(WindowEvent event) {
					if (play.getAtlasRegion().equals("PAUSE")) {
						play.setAtlasRegion("PAUSE_ACTIVE");
					} else if (play.getAtlasRegion().equals("PLAY")) {
						play.setAtlasRegion("PLAY_ACTIVE");
					}
					
					touchStartedInWindow = true;
				}
			});
		
			dateHolder.addEventListener(WindowEvent.WindowEventType.TOUCH_LEAVES, new IEventListener() {
				@Override
				public void onEvent(WindowEvent event) {
					if (play.getAtlasRegion().equals("PAUSE_ACTIVE")) {
						play.setAtlasRegion("PAUSE");
					} else if (play.getAtlasRegion().equals("PLAY_ACTIVE")) {
						play.setAtlasRegion("PLAY");
					}
				}
			});
		
			dateHolder.addEventListener(WindowEvent.WindowEventType.TOUCH_UP, new IEventListener() {
				@Override
				public void onEvent(WindowEvent event) {
					
					if (touchStartedInWindow) {
					
						if (World.getInstance().isPaused()) {
							play.setAtlasRegion("PAUSE");
							World.getInstance().unpause();
						} else {
							play.setAtlasRegion("PLAY");
							World.getInstance().pause();
						}
						
						touchStartedInWindow = false;
					
					}
				}
			});
		
		
			speedHolder.setReceiveTouchEvents(true);
			speedHolder.setSizeRelaxation(new Vector2(1.3f, 1.1f));
			
			speedHolder.addEventListener(WindowEvent.WindowEventType.TOUCH_DOWN, new IEventListener() {
				@Override
				public void onEvent(WindowEvent event) {
					for (int i = 0; i < speedIcons.size(); ++i) {
						speedIcons.get(i).setAtlasRegion("PLAY_SPEED_ACTIVE");
					}
					touchStartedInWindow = true;
				}
			});
	
			speedHolder.addEventListener(WindowEvent.WindowEventType.TOUCH_LEAVES, new IEventListener() {
				@Override
				public void onEvent(WindowEvent event) {
					for (int i = 0; i < speedIcons.size(); ++i) {
						speedIcons.get(i).setAtlasRegion("PLAY_SPEED");
					}
				}
			});
			
			speedHolder.addEventListener(WindowEvent.WindowEventType.TOUCH_UP, new IEventListener() {
				@Override
				public void onEvent(WindowEvent event) {
					
					if (touchStartedInWindow) {
						
						int speed = ((int)Math.sqrt(World.getInstance().getPlaySpeed())) % 4;
						World.getInstance().setPlaySpeed(speed+1);
						
						for (int i = 0; i < speedIcons.size(); ++i) {
							if (i <= speed) {
								speedIcons.get(i).setVisible(true);
							} else {
								speedIcons.get(i).setVisible(false);
							}
						}
						
						touchStartedInWindow = false;
					
					}
				}
			});
		}
		
		dateHolder.addChild(speedHolder);
		
	}
	
}
