package com.tyrfing.games.id17.startmenu;

import com.tyrfing.games.id17.EmpireFrameListener;
import com.tyrlib2.graphics.renderables.FormattedText2.ALIGNMENT;
import com.tyrlib2.graphics.scene.SceneManager;
import com.tyrlib2.gui.Frame;
import com.tyrlib2.gui.Frame.FrameImagePosition;
import com.tyrlib2.gui.IEventListener;
import com.tyrlib2.gui.ImageBox;
import com.tyrlib2.gui.ItemList;
import com.tyrlib2.gui.Label;
import com.tyrlib2.gui.ScaledVector1;
import com.tyrlib2.gui.ScaledVector1.ScaleDirection;
import com.tyrlib2.gui.ScaledVector2;
import com.tyrlib2.gui.Skin;
import com.tyrlib2.gui.WindowEvent;
import com.tyrlib2.gui.WindowEvent.WindowEventType;
import com.tyrlib2.gui.WindowManager;
import com.tyrlib2.main.AndroidMedia;
import com.tyrlib2.main.Media;
import com.tyrlib2.math.Vector2;
import com.tyrlib2.networking.Connection;
import com.tyrlib2.networking.INetworkListener;
import com.tyrlib2.util.Color;

public class MatchMakerUI implements INetworkListener  {
	
	public static Vector2 POS = new Vector2(0.325f, -0.9f);
	public static Vector2 SIZE = new Vector2(0.6f, 0.75f);
	
	public static Vector2 GAME_LIST_POS = new Vector2 (0.025f, 0.025f);
	public static Vector2 GAME_LIST_SIZE = new Vector2(0.55f, 0.55f);
	
	public static Vector2 IP_FIELD_POS = new Vector2(0.025f, 0.6f);
	public static ScaledVector2 IP_FIELD_SIZE = new ScaledVector2(0.25f, 0.1f, 0);
	
	public static ScaledVector2 OK_BUTTON_POS = new ScaledVector2(0.05f, 0.01f, 0);
	public static ScaledVector2 OK_BUTTON_SIZE = new ScaledVector2(0.1f, 0.08f, 0);
	
	public static ScaledVector2 NAME_POS = new ScaledVector2(0.02f, 0.02f, 0);
	public static ScaledVector2 GAME_POS = new ScaledVector2(0, 0.08f, 2);
	
	private Frame frame;
	private ItemList games;
	private Frame gameFrame;
	private ImageBox okButton;
	private Frame ipFrame;
	private IPField ipField;
	
	private Label gameName;
	private Label gamePlayers;
	
	private boolean visible = false;
	
	private GameEntry selected;
	
	public MatchMakerUI() {
		
		try {
			MatchMakerUI.class.getClassLoader().loadClass("com.tyrfing.games.id17.startmenu.PlayerUpdate");
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		}
		
		Skin skin = WindowManager.getInstance().getSkin();
		Vector2 tmpRepeat = skin.FRAME_MIDDLE_REPEAT;
		skin.FRAME_MIDDLE_REPEAT = new Vector2(40, 20);
		
		frame =  WindowManager.getInstance().createFrame("MatchMaker", POS, SIZE);
		
		gameFrame = WindowManager.getInstance().createFrame("MatchMaker/Games", GAME_LIST_POS, GAME_LIST_SIZE);
		gameFrame.setInheritsAlpha(true);
		frame.addChild(gameFrame);
		
		ipFrame = WindowManager.getInstance().createFrame("MatchMaker/IPFrame", IP_FIELD_POS, IP_FIELD_SIZE);
		ipFrame.setInheritsAlpha(true);
		ipFrame.getImageBox(FrameImagePosition.MIDDLE).setReceiveTouchEvents(true);
		ipFrame.getImageBox(FrameImagePosition.MIDDLE).setPassTouchEventsThrough(true);
		
		ipFrame.getImageBox(FrameImagePosition.MIDDLE).addEventListener(WindowEventType.TOUCH_DOWN, new IEventListener() {
			@Override
			public void onEvent(WindowEvent event) {
				ipField.activate();
				
				if (EmpireFrameListener.BUILD_TARGET == EmpireFrameListener.ANDROID_TARGET) {
					((AndroidMedia)Media.CONTEXT).showKeyboard();
				}
			}
			
		});
		
		frame.addChild(ipFrame);
		
		
		okButton = (ImageBox) WindowManager.getInstance().createImageBox("MatchMaker/OK", OK_BUTTON_POS.get().add(new Vector2(IP_FIELD_SIZE.get().x, 0)), "MAIN_GUI", "SMALL_RECT_WOOD", OK_BUTTON_SIZE);
		ipFrame.addChild(okButton);
		okButton.setInheritsAlpha(true);
		final Label caption = (Label) WindowManager.getInstance().createLabel("MatchMaker/OK/Label", new Vector2(OK_BUTTON_SIZE.get().multiply(0.5f).x, OK_BUTTON_SIZE.get().multiply(0.25f).y), "Ok");
		caption.setAlignment(ALIGNMENT.CENTER);
		caption.setInheritsAlpha(true);
		okButton.addChild(caption);
		
		okButton.addEventListener(WindowEventType.TOUCH, new IEventListener() {
			@Override
			public void onEvent(WindowEvent event) {
				if (selected != null) {
					join(selected.getAddress());
				} else if (!ipField.getAddress().equals("")) {
					join(ipField.getAddress());
				}
			}
		});
		
		okButton.setReceiveTouchEvents(true);
		
		okButton.addEventListener(WindowEventType.TOUCH_ENTERS, new IEventListener() {
			@Override
			public void onEvent(WindowEvent event) {
				caption.setColor(Color.RED);
			}
		});
		
		okButton.addEventListener(WindowEventType.TOUCH_LEAVES, new IEventListener() {
			@Override
			public void onEvent(WindowEvent event) {
				caption.setColor(Color.WHITE);
			}
		});
		
		
		gameName = (Label) WindowManager.getInstance().createLabel("MatchMaker/GameName", NAME_POS, "Name");
		gameName.setColor(Color.WHITE);
		gameFrame.addChild(gameName);
		gameName.setInheritsAlpha(true);
		
		gamePlayers = (Label) WindowManager.getInstance().createLabel("MatchMaker/Players", new Vector2(GAME_LIST_SIZE.x, 0).add(new Vector2(-NAME_POS.get().x, NAME_POS.get().y)), "Players");
		gamePlayers.setColor(Color.WHITE);
		gamePlayers.setAlignment(ALIGNMENT.RIGHT);
		gameFrame.addChild(gamePlayers);
		gamePlayers.setInheritsAlpha(true);
		
		games = (ItemList) WindowManager.getInstance().createItemList("MatchMaker/GameList", GAME_LIST_POS.add(new ScaledVector2(0, 0.08f, 0).get()).add(new Vector2(0.01f,0)), GAME_LIST_SIZE, new ScaledVector1(0.03f, ScaleDirection.Y, 0).get(), 10);
		games.setInheritsAlpha(true);
		games.setPassTouchEventsThrough(true);
		games.setReceiveTouchEvents(true);
		frame.addChild(games);
		
		frame.setAlpha(0);
		
		skin.FRAME_MIDDLE_REPEAT = tmpRepeat;
	}
	
	public void show() {
		frame.fadeIn(1, 1);
		ipField = new IPField(ipFrame);
		visible = true;
	}
	

	
	public void hide() {
		frame.fadeOut(0, 1);
		ipField.destroy();
		visible = false;
		
		for (int i = 0; i < games.getCountEntries(); ++i) {
			WindowManager.getInstance().destroyWindow(games.getEntry(i));
		}
		
		games.clear();
	}
	
	public boolean isVisible() {
		return visible;
	}

	@Override
	public void onNewConnection(Connection c) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onConnectionLost(Connection c) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onReceivedData(Connection c, Object o) {
		if (o instanceof PlayerUpdate) {
			final PlayerUpdate u = (PlayerUpdate) o;
			final MatchMakerUI ui = this;
			
			SceneManager.getInstance().getRenderer().queueEvent(new Runnable() {
				
				@Override
				public void run() {
					nextGame: for (int i = 0; i < games.getCountEntries(); ++i) {
						GameEntry entry = (GameEntry) games.getEntry(i);
						for (int j = 0; j < u.id.length; ++j) {
							if (entry.getID() == u.id[j]) {
								entry.setCountPlayers(u.current[j], u.max[i]);
								continue nextGame;
							}
						}
						
						WindowManager.getInstance().destroyWindow(games.getEntry(i));
					}
					
					nextGame: for (int i = 0; i < u.id.length; ++i) {
						for (int j = 0; j < games.getCountEntries(); ++j) {
							GameEntry entry = (GameEntry) games.getEntry(j);
							if (entry.getID() == u.id[i]) {
								continue nextGame;
							}
						}
						
						String address = u.address[i];
						
						GameEntry entry = new GameEntry(String.valueOf(u.id[i]), u.id[i], ui, address);
						entry.setCountPlayers(u.current[i], u.max[i]);
						games.addItemListEntry(entry);
						if (visible) {
							entry.setAlpha(1);
						} else {
							entry.setAlpha(0);
						}
					}	
				}
			});

		}
	}
	
	public void setSelected(GameEntry entry) {
		selected = entry;
	}
	
	public void deselectAll() {
		for (int i = 0; i < games.getCountEntries(); ++i) {
			GameEntry entry = (GameEntry) games.getEntry(i);
			entry.unhighlight();
		}
		selected = null;
	}

	public void join(String address) {
		hide();
		EmpireFrameListener.MAIN_FRAME.connectToGame(address);
	}
	
}
