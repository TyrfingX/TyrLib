package com.tyrfing.games.id17.gui;

import java.util.ArrayList;
import java.util.List;

import com.tyrfing.games.id17.gui.holding.HoldingGUI;
import com.tyrfing.games.id17.world.World;
import com.tyrlib2.game.IUpdateable;
import com.tyrlib2.gui.IEventListener;
import com.tyrlib2.gui.ImageBox;
import com.tyrlib2.gui.Paint;
import com.tyrlib2.gui.ScaledVector2;
import com.tyrlib2.gui.Window;
import com.tyrlib2.gui.WindowEvent;
import com.tyrlib2.gui.WindowEvent.WindowEventType;
import com.tyrlib2.gui.WindowManager;
import com.tyrlib2.math.Vector2;
import com.tyrlib2.util.Color;

public abstract class TabGUI<T> implements GUI, IUpdateable{
	
	public static final float UPDATE_DELAY = 1;
	public static final ScaledVector2 HEADER_POS = new ScaledVector2(12, 10, 5);
	public static final ScaledVector2 TAB_POS = new ScaledVector2(3, 4, 5);
	public static final ScaledVector2 TAB_SIZE = new ScaledVector2(41, 41, 5);
	public static final ScaledVector2 TAB_PADDING = new ScaledVector2(1, 0, 5);
	public static final Color WOOD_COLOR = Color.fromRGBA(53, 33, 11, 255);
	public static final Color GOLD_COLOR = Color.fromRGBA(155, 104, 51, 175);
	public static final Paint GOLDEN_WOOD_PAINT = new Paint(WOOD_COLOR.copy(), GOLD_COLOR.copy(), 4);
	public static final Paint GOLDEN_WOOD_PAINT2 = new Paint(WOOD_COLOR.copy(), GOLD_COLOR.copy(), 2);
	public static final Color GRAY_BORDER = Color.fromRGBA(63, 63, 63, 150);
	public static final Color GRAY_FILL = Color.fromRGBA(32, 32, 32, 150);
	public static final Paint GRAY_PAINT = new Paint(GRAY_FILL.copy(), GRAY_BORDER.copy(), 4);
	
	public static final ScaledVector2 LEFT_LABEL_POS = new ScaledVector2(2, 1, 5);
	public static final ScaledVector2 RIGHT_LABEL_POS = new ScaledVector2(-41,-LEFT_LABEL_POS.y,5);
	
	public static final ScaledVector2 WINDOW_SIZE = new ScaledVector2(0.6f, 0.3f, 2);
	public static final ScaledVector2 WINDOW_POS = new ScaledVector2(0, -WINDOW_SIZE.y, 2);
	public static final float DISPLAY_TIME = 0.3f;
	
	public static final Vector2 SIGIL_HOLDER_POS = new Vector2(0, 0);
	public static final ScaledVector2 SIGIL_HOLDER_SIZE = DateGUI.FAST_FORWARD_BUTTON_SIZE;
	public static final Vector2 SIGIL_POS = new Vector2(-0.1f, -0.76f);
	public static final ScaledVector2 SIGIL_SIZE = new ScaledVector2(TabGUI.SIGIL_HOLDER_SIZE.x, TabGUI.SIGIL_HOLDER_SIZE.y, 3);
	
	public static final ScaledVector2 PADDING = new ScaledVector2(1.05f * TabGUI.SIGIL_HOLDER_SIZE.x, 1.05f * TabGUI.SIGIL_HOLDER_SIZE.y, 3);
	
	public static final Vector2 CANCEL_POS = new Vector2(TabGUI.SIGIL_HOLDER_SIZE.y * 0.05f, -TabGUI.SIGIL_SIZE.y/2);
	public static final ScaledVector2 CANCEL_HOLDER_OFFSET = new ScaledVector2(3*TabGUI.PADDING.x,4*TabGUI.PADDING.y);
	public static final Vector2 CANCEL_HOLDER_POS = new Vector2(HoldingGUI.BUILD_HOLDER_POS.x +  TabGUI.SIGIL_HOLDER_SIZE.x * 0.025f,  HoldingGUI.BUILD_HOLDER_POS.y + TabGUI.SIGIL_HOLDER_SIZE.y * 0.025f);


	private float passedUpdateTime;
	
	protected Window main;
	protected Window cancel;
	public T displayed = null;
	protected boolean redisplay = false;
	
	protected List<ImageBox> options = new ArrayList<ImageBox>();
	protected List<GUI> subGUIs = new ArrayList<GUI>();
	protected boolean hiding = false;
	
	protected Vector2 headerSize;
	protected Window header;
	protected Window subHeader;
	protected String[] tabNames;
	
	public TabGUI(String mainName) {
		this(mainName, 0, null, null, null, null);
	}
	
	public TabGUI(String mainName, int tabs, String[] tabNames, String[] tabAtlasNames, String[] tabRegionNames, String[] tabTooltips) {
		main = WindowManager.getInstance().createFrame(mainName, new Vector2(WINDOW_POS.x,2*SIGIL_HOLDER_SIZE.y), WINDOW_SIZE);
		main.setReceiveTouchEvents(true);
		main.setPassTouchEventsThrough(false);
		main.setVisible(false);
		
		main.addEventListener(WindowEvent.WindowEventType.MOVEMENT_FINISHED, new IEventListener() {
			@Override
			public void onEvent(WindowEvent event) {
				finishedMovement();
			}
		});
		
		World.getInstance().getUpdater().addItem(this);
		
		if (tabNames != null) {
			
			this.tabNames = tabNames;
			
			/** Construct the header menu **/
			
			headerSize = new Vector2(TAB_POS.get().x*2+tabs*(TAB_SIZE.get().x+TAB_PADDING.get().x), TAB_POS.get().y*2+TAB_SIZE.get().y);
			
			header = WindowManager.getInstance().createRectWindow(	mainName + "/GUI_HEADER", 
																	new Vector2(HEADER_POS.get().x, -1-headerSize.y*3), 
																	headerSize, GOLDEN_WOOD_PAINT);
			
			header.setReceiveTouchEvents(true);
			
			for (int i = 0; i < tabNames.length; ++i) {
				Vector2 offset = new Vector2((TAB_SIZE.get().x+TAB_PADDING.get().x)*i, 0);
				Window tabHolder = WindowManager.getInstance().createImageBox(	tabNames[i], 
																				TAB_POS.get().add(offset), 
																				"MAIN_GUI", "SMALL_CIRCLE_BORDER" , TAB_SIZE);
				
				tabHolder.setReceiveTouchEvents(true);
				tabHolder.setPassTouchEventsThrough(true);
			}
			
			for (int i = 0; i < tabAtlasNames.length; ++i)  {
				final ImageBox tabHolder = (ImageBox) WindowManager.getInstance().getWindow(tabNames[i]);
				final Window image = WindowManager.getInstance().createImageBox(	tabNames[i] + "/Image", 
																			new Vector2(), 
																			tabAtlasNames[i], 
																			tabRegionNames[i] , 
																			TAB_SIZE);
				
				IEventListener small = new IEventListener() {
					@Override
					public void onEvent(WindowEvent event) {
						image.moveTo(new Vector2(), 0.5f);
						image.resize(TAB_SIZE.get(), 0.5f);
					}
				};
				
				IEventListener big = new IEventListener() {
					@Override
					public void onEvent(WindowEvent event) {
						image.moveTo(TAB_SIZE.get().multiply(-0.05f), 0.5f);
						image.resize(TAB_SIZE.get().multiply(1.1f), 0.5f);
					}
				};
				
				image.addEventListener(WindowEventType.TOUCH_LEAVES, small);
				image.addEventListener(WindowEventType.MOUSE_LEAVES, small);
				image.addEventListener(WindowEventType.TOUCH_ENTERS, big);
				image.addEventListener(WindowEventType.MOUSE_ENTERS, big);
				image.setReceiveTouchEvents(true);
				image.setPassTouchEventsThrough(true);
				
				tabHolder.addChild(image);
				header.addChild(tabHolder);
				
				tabHolder.addEventListener(WindowEventType.TOUCH_DOWN, new IEventListener() {
					@Override
					public void onEvent(WindowEvent event) {
						unhighlightTabs();
						tabHolder.setAtlasRegion("SMALL_CIRCLE_BORDER_ACTIVE");
					}
				});
				
				WindowManager.getInstance().addTextTooltip(tabHolder, tabTooltips[i]);
			}
		}
	}
	
	public void hideSubUIs(GUI except) {
		
		boolean switchUIs = true;
		
		for (int i = 0; i < subGUIs.size(); ++i) {
			if (subGUIs.get(i).isVisible() && subGUIs.get(i) != except) {
				subGUIs.get(i).hide();
			} else {
				switchUIs = false;
			}
		}
		
		if (switchUIs) {
			hideOptions();
		}
	}
	
	public void unhighlightTabs() {
		if (tabNames != null) {
			for (int i = 0; i < tabNames.length; ++i) {
				ImageBox tab = (ImageBox) WindowManager.getInstance().getWindow(tabNames[i]);
				if (tab.getAtlasRegion().equals("SMALL_CIRCLE_BORDER_ACTIVE")) { 
					tab.setAtlasRegion("SMALL_CIRCLE_BORDER");
				}
			}
		}
	}

	public void setOptionsVisible(boolean visible) {
		for (int i = 0; i < options.size(); ++i) {
			if (options.get(i) != null) {
				options.get(i).setVisible(visible);
			}
		}
	}
	
	public void show(T displayed) {
		if (header != null) {
			header.moveTo(new Vector2(HEADER_POS.get().x, -1+HEADER_POS.get().y), 0.25f);
		}
		
		main.setReceiveTouchEvents(true);
		main.setPassTouchEventsThrough(false);
		
		if (this.displayed != displayed) {
			main.setVisible(true);
			
			setOptionsVisible(true);
			
			redisplay = false;
			
			if (displayed == null) {
				main.moveTo(new ScaledVector2(WINDOW_POS.x, -WINDOW_SIZE.y*0.99f, 2).get(), DISPLAY_TIME);
				showOptions();
				this.displayed = displayed;
				display();
			} else if (displayed != this.displayed) {
				if (main.getAbsolutePosY() >= SIGIL_HOLDER_SIZE.y) {
					redisplay = true;
					main.moveTo(new Vector2(WINDOW_POS.x, SIGIL_HOLDER_SIZE.y), DISPLAY_TIME);
					hideOptions();
					this.displayed = displayed;
				} else {
					main.moveTo(new Vector2(WINDOW_POS.x, -WINDOW_SIZE.y*0.99f), DISPLAY_TIME);
					showOptions();
					this.displayed = displayed;
					display();
				}
			}
			
			hiding = false;
		}
	}
	
	public void showOptions() {
		for (int i = 0; i < options.size(); ++i) {
			if (options.get(i) != null) {
				options.get(i).moveTo(new Vector2(DateGUI.FAST_FORWARD_BUTTON_POS.x+DateGUI.FAST_FORWARD_BUTTON_OFFSET.get().x, SIGIL_POS.y + PADDING.get().y * i), DISPLAY_TIME);
			}
		}
	}
	
	public abstract void display();
	public abstract void update();
	
	@Override
	public void onUpdate(float time) {
		passedUpdateTime += time;
		if (passedUpdateTime >= UPDATE_DELAY) {
			passedUpdateTime = 0;
			update();
		}
	}
	
	@Override
	public void hide() {
		if (displayed != null) {
			if ( World.getInstance().getMainGUI() != null && World.getInstance().getMainGUI().pickerGUI != null ) {
				World.getInstance().getMainGUI().pickerGUI.unhighlight();
			}
			hiding = true;
			main.moveTo(new Vector2(WINDOW_POS.x, 2*TabGUI.SIGIL_HOLDER_SIZE.y), TabGUI.DISPLAY_TIME);
		
			hideOptions();
			for (int i = 0; i < subGUIs.size(); ++i) {
				subGUIs.get(i).hide();
			}
		}
		
		unhighlightTabs();
		
		if (header != null) {
			header.moveTo(new Vector2(header.getAbsolutePosX(), -1-header.getSize().y*3), 0.25f);
		}
	}
	
	public void hideOptions() {
		for (int i = 0; i < options.size(); ++i) {
			if (options.get(i) != null) {
				options.get(i).moveTo(new Vector2(-TabGUI.PADDING.get().x * (i+1), SIGIL_POS.y + TabGUI.PADDING.get().y * i), TabGUI.DISPLAY_TIME);
			}
		}
	}
	
	public void show() {
		header.moveTo(new Vector2(HEADER_POS.get().x, -1+HEADER_POS.get().y), 0.25f);
		
		ImageBox sigil = (ImageBox) WindowManager.getInstance().getWindow(tabNames[0]+"/Image");
		sigil.setAtlasRegion(World.getInstance().getPlayerController().getHouse().getSigilName());
	}
	
	@Override
	public boolean isVisible() {
		return displayed != null;
	}
	
	protected void finishedMovement() {
		if (redisplay) {
			for (int i = 0; i < subGUIs.size(); ++i) {
				subGUIs.get(i).hide();
			}
			
			redisplay = false;
			display();
			main.moveTo(new ScaledVector2(WINDOW_POS.x, -WINDOW_SIZE.y*0.99f, 2).get(), TabGUI.DISPLAY_TIME);
			showOptions();
		} 
		
		if (hiding) {
			hiding = false;
			displayed = null;
			main.setVisible(false);
			
			if (	!World.getInstance().mainGUI.houseGUI.isVisible()
				&&	!World.getInstance().mainGUI.pickerGUI.holdingGUI.isVisible()
				&&	!World.getInstance().mainGUI.pickerGUI.armyGUI.isVisible()) {
				World.getInstance().mainGUI.houseGUI.show();
			}
		}
	}
}
