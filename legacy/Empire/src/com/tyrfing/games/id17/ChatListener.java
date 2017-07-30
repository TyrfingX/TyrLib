package com.tyrfing.games.id17;

import java.util.ArrayList;
import java.util.List;

import com.tyrfing.games.id17.gui.PlayerController;
import com.tyrfing.games.id17.houses.House;
import com.tyrfing.games.id17.houses.HouseController;
import com.tyrfing.games.id17.networking.JoinGame;
import com.tyrfing.games.id17.networking.PlayerQuit;
import com.tyrfing.games.id17.startmenu.IPField;
import com.tyrfing.games.id17.world.World;
import com.tyrlib2.graphics.renderer.IFrameListener;
import com.tyrlib2.graphics.scene.SceneManager;
import com.tyrlib2.graphics.text.GLText;
import com.tyrlib2.gui.IEventListener;
import com.tyrlib2.gui.Label;
import com.tyrlib2.gui.Skin;
import com.tyrlib2.gui.WindowEvent;
import com.tyrlib2.gui.WindowEvent.WindowEventType;
import com.tyrlib2.gui.WindowManager;
import com.tyrlib2.input.IKeyboardEvent;
import com.tyrlib2.input.IKeyboardListener;
import com.tyrlib2.input.InputManager;
import com.tyrlib2.math.Vector2;
import com.tyrlib2.networking.Connection;
import com.tyrlib2.networking.INetworkListener;
import com.tyrlib2.util.Color;

public class ChatListener implements IFrameListener, INetworkListener, IKeyboardListener {

	private float passedTime;
	private Label writeField;
	private boolean flip;
	private String text;
	
	public static class ChatEntry {
		Label label;
		float passedTime;
		
		public ChatEntry(Label label) {
			this.label = label;
		}
	}
	
	private List<ChatEntry> chatEntries = new ArrayList<ChatEntry>();
	
	public static Vector2 CHAT_POS = new Vector2(0.1f, -0.4f);
	
	public static final int MAX_PLAYERS = 256;
	public static final Color[] chatColors = new Color[MAX_PLAYERS];
	
	static {
		chatColors[0] = Color.RED;
		chatColors[1] = Color.BLUE;
		chatColors[2] = new Color(0.2f, 0.7f, 0.2f, 1.0f);
		chatColors[3] = new Color(1f, 1f, 0.3f, 1.0f);
		chatColors[4] = Color.WHITE;
		chatColors[5] = Color.BLACK;
		for (int i = 6; i < MAX_PLAYERS; ++i) {
			chatColors[i] = Color.getRandomColor();
		}
	}
	
	public ChatListener() {
		if (!SceneManager.getInstance().getRenderer().isInServerMode()) {
			writeField = (Label) WindowManager.getInstance().createLabel("CHAT_WRITE_FIELD", CHAT_POS, "");
			writeField.setVisible(false);
			writeField.setFont(SceneManager.getInstance().getFont("FONT_14"));
			writeField.setColor(new Color(0.3f, 0.19f, 0.12f, 1));
			writeField.setBgColor(new Color(0.76f, 0.635f, 0.38f, 0.7f));
		}
	}
	
	@Override
	public void onSurfaceCreated() {
	}

	@Override
	public void onSurfaceChanged() {
	}

	@Override
	public void onFrameRendered(float time) {
		if (!SceneManager.getInstance().getRenderer().isInServerMode()) {
			passedTime += time;
			if (passedTime >= IPField.FLIP_TIME && writeField.isVisible()) {
				if (flip) {
					writeField.setText("Chat: " + text + "|");
				} else {
					writeField.setText("Chat: " + text + " ");
				}
				
				flip = !flip;
				
				passedTime = 0;
			}
			
			for (int i = 0; i < chatEntries.size(); ++i) {
				ChatEntry e = chatEntries.get(i);
				e.passedTime += time;
				if (e.passedTime >= 30) {
					e.label.fadeOut(0, 0.5f);
					e.label.moveBy(new Vector2(0, -chatEntries.get(i).label.getSize().y), 0.5f);
					chatEntries.remove(e);
				}
			}
		}
	}

	@Override
	public void onNewConnection(Connection c) {
	}

	@Override
	public void onConnectionLost(final Connection c) {
		SceneManager.getInstance().getRenderer().queueEvent(new Runnable() {
			final HouseController hc = 	World.getInstance().getPlayer(c);
			@Override
			public void run() {
				if (hc != null) {
					if (hc.hasJoined) {
						String s = "<< " + hc.getHouse().getName() + " has left >>";
						if (!SceneManager.getInstance().getRenderer().isInServerMode()) {
							addChatEntry(s);
						}
						
						if (EmpireFrameListener.MAIN_FRAME.getNetwork().isHost()) {
							EmpireFrameListener.MAIN_FRAME.getNetwork().broadcast(s);
						}
					}
				}
			}
		});
	}

	@Override
	public void onReceivedData(Connection c, Object o) {
		if (o instanceof String) {
			final String s = (String) o;
			SceneManager.getInstance().getRenderer().queueEvent(new Runnable() {
				@Override
				public void run() {
					if (!SceneManager.getInstance().getRenderer().isInServerMode()) {
						addChatEntry(s);
					}
					
					if (EmpireFrameListener.MAIN_FRAME.getNetwork().isHost()) {
						EmpireFrameListener.MAIN_FRAME.getNetwork().broadcast(s);
					}
				}
			});
		} else if (o instanceof JoinGame) {
			House house = World.getInstance().getHouses().get(((JoinGame) o).houseID);
			final String message = "<< " + house.getName() + " has joined the game >>";
			SceneManager.getInstance().getRenderer().queueEvent(new Runnable() {
				@Override
				public void run() {
					if (!SceneManager.getInstance().getRenderer().isInServerMode()) {
						addChatEntry(message);
					}
				}
			});
		} else if (o instanceof PlayerQuit) {
			House house = World.getInstance().getPlayer(((PlayerQuit) o).playerID).getHouse();
			final String message = "<< " + house.getName() + " has left the game >>";
			SceneManager.getInstance().getRenderer().queueEvent(new Runnable() {
				@Override
				public void run() {
					if (!SceneManager.getInstance().getRenderer().isInServerMode()) {
						addChatEntry(message);
					}
				}
			});
		}
		
	}
	
	public void addChatEntry(String entry) {
		
		Skin skin = WindowManager.getInstance().getSkin();
		String tmp = skin.LABEL_FONT;
		skin.LABEL_FONT = "FONT_14";
		
		Label label = (Label) WindowManager.getInstance().createLabel("CHAT_ENTRY" + Math.random(), CHAT_POS, entry);
		label.setFont(SceneManager.getInstance().getFont("FONT_14"));
		label.setColor(new Color(0.3f, 0.19f, 0.12f, 1));
		label.setBgColor(new Color(0.76f, 0.635f, 0.38f, 0.7f));
		
		skin.LABEL_FONT = tmp;
		
		final ChatEntry e = new ChatEntry(label);
		
		chatEntries.add(0, e);
		
		label.addEventListener(WindowEventType.FADE_OUT_FINISHED, new IEventListener() {
			@Override
			public void onEvent(WindowEvent event) {
				WindowManager.getInstance().destroyWindow(event.getSource());
			}
		});
		
		for (int i = 0; i < chatEntries.size(); ++i) {
			chatEntries.get(i).label.moveTo(new Vector2(CHAT_POS.x, CHAT_POS.y-chatEntries.get(i).label.getSize().y*(i+1)), 0.5f);
		}
		
	}
	
	@Override
	public boolean onPress(IKeyboardEvent e) {
		short code = e.getKeyCode();
		char c = e.getKeyChar();
		boolean isPrintable = e.isPrintable() && c >= GLText.CHAR_START && c <= GLText.CHAR_END;
		
		if (code == InputManager.VK_ENTER) {
			if (writeField.isVisible() && !text.equals("")) {
				PlayerController p = World.getInstance().getPlayerController();
				String hex = chatColors[p.playerID].toHex();
				String prefix = "<" + hex + ">" + p.getHouse().getName() + "\\#: ";
				if (EmpireFrameListener.MAIN_FRAME.getNetwork().isHost()) {
					addChatEntry(prefix + text);
				}
				EmpireFrameListener.MAIN_FRAME.getNetwork().broadcast(prefix + text);
				EmpireFrameListener.MAIN_FRAME.getNetwork().flush();
			} else {
				writeField.setText("Chat: |");
			}
			
			text = "";
			
			writeField.setVisible(!writeField.isVisible());
			
			return true;
		} else if (writeField.isVisible()) {
			if (code == InputManager.VK_BACK_SPACE) {
				if (text.length() > 0) {
					text = text.substring(0, text.length()-1);
				}
			} else if (isPrintable){
				text += c;
			}
			
			writeField.setText("Chat: " + text);
			
			return true;
		}
		
		return false;
	}

	@Override
	public boolean onRelease(IKeyboardEvent e) {
		if (writeField.isVisible()) {
			return true;
		} 
		
		return false;
	}

	@Override
	public long getPriority() {
		return 0;
	}

}
