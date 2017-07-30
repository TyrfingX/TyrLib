package com.tyrfing.games.id17.gui;

import java.util.ArrayList;
import java.util.List;

import com.tyrfing.games.id17.ChatListener;
import com.tyrfing.games.id17.houses.HouseController;
import com.tyrfing.games.id17.networking.JoinGame;
import com.tyrfing.games.id17.networking.NewPlayer;
import com.tyrfing.games.id17.networking.PlayerQuit;
import com.tyrfing.games.id17.world.World;
import com.tyrlib2.graphics.renderables.FormattedText2.ALIGNMENT;
import com.tyrlib2.graphics.renderer.IFrameListener;
import com.tyrlib2.graphics.scene.SceneManager;
import com.tyrlib2.gui.Label;
import com.tyrlib2.gui.ScaledVector2;
import com.tyrlib2.gui.Skin;
import com.tyrlib2.gui.Window;
import com.tyrlib2.gui.WindowManager;
import com.tyrlib2.math.Vector2;
import com.tyrlib2.networking.Connection;
import com.tyrlib2.networking.INetworkListener;
import com.tyrlib2.util.Color;

public class Scoreboard implements INetworkListener, IFrameListener {
	
	public static class Score {
		Label name;
		Label points;
		int playerID;
	}
	
	private List<Score> playerScores = new ArrayList<Score>();

	public static final Vector2 BOARD_POS = new Vector2(0.785f, -0.85f);
	public static final ScaledVector2 SIZE = new ScaledVector2(100, 0, 4);
	public static final ScaledVector2 POINT_OFFSET = new ScaledVector2(0.125f, 0, 2);
	
	private boolean active = false;
	
	public Scoreboard() {

		
	}
	
	private void removePlayer(int playerID) {
		Vector2 offset = null;
		for (int i = 0; i < playerScores.size(); ++i) {
			Score s = playerScores.get(i);
			if (s.playerID == playerID && offset == null) {
				offset = new Vector2(0, -playerScores.get(i).name.getSize().y);
				playerScores.remove(i);
				--i;
				
				WindowManager.getInstance().destroyWindow(s.name);
				WindowManager.getInstance().destroyWindow(s.points);
				
			} else if (offset != null) {
				playerScores.get(i).name.moveBy(offset, 1);
				playerScores.get(i).points.moveBy(offset, 1);
			}
		}
	}
	
	public void addPlayer(int playerID, int houseID) {
		Skin skin = WindowManager.getInstance().getSkin();
		String tmp = skin.LABEL_FONT;
		skin.LABEL_FONT = "FONT_14";
		
		String name =  World.getInstance().getHouses().get(houseID).getSigilName();
		
		Vector2 basePos = new Vector2(BOARD_POS);
		
		if (playerScores.size() > 0) {
			basePos.y += playerScores.get(0).name.getSize().y * (playerScores.size()+1);
		} else {
			Window w = WindowManager.getInstance().getWindow("SCOREBOARD/GOAL");
			basePos.y += w.getSize().y * (playerScores.size()+1);
		}
		
		Label label = (Label) WindowManager.getInstance().createLabel("SCOREBOARD_NAME" + Math.random(), basePos, "<img SIGILS1 " + name + "> " + name);
		label.setFont(SceneManager.getInstance().getFont("FONT_14"));
		label.setColor(ChatListener.chatColors[playerID]);
		label.setBgColor(new Color(0.76f, 0.635f, 0.38f, 0.5f));
		label.getBackground().setSize(new Vector2(SIZE.get().x, label.getFormattedText().getSize().y));
		
		skin.LABEL_FONT = tmp;
		
		Score s = new Score();
		s.name = label;
		
		label = (Label) WindowManager.getInstance().createLabel("SCOREBOARD_POINTS" + Math.random(), basePos.add(POINT_OFFSET.get()), "0");
		label.setFont(SceneManager.getInstance().getFont("FONT_14"));
		label.setColor(new Color(0.3f, 0.19f, 0.12f, 1));
		label.setAlignment(ALIGNMENT.RIGHT);
		
		s.points = label;
		s.playerID = playerID;
		
		playerScores.add(s);
	}
	
	@Override
	public void onNewConnection(Connection c) {
	}

	@Override
	public void onConnectionLost(Connection c) {
	}

	@Override
	public void onReceivedData(Connection c, Object o) {
		if (o instanceof JoinGame) {
			final JoinGame jg = (JoinGame) o;
			
			SceneManager.getInstance().getRenderer().queueEvent(new Runnable() {
				@Override
				public void run() {
					if (active) addPlayer(jg.playerID, jg.houseID);
				}
			});
		} else if (o instanceof NewPlayer) {
			final NewPlayer np = (NewPlayer) o;
			
			SceneManager.getInstance().getRenderer().queueEvent(new Runnable() {
				@Override
				public void run() {
					if (np.hasJoined && active) {
						addPlayer(np.playerID, np.houseID);
					}
				}
			});
		} else if (o instanceof PlayerQuit) {
			final PlayerQuit pq = (PlayerQuit) o;
			
			SceneManager.getInstance().getRenderer().queueEvent(new Runnable() {
				@Override
				public void run() {
					if (active) removePlayer(pq.playerID);
				}


			});
		}
		
	} 

	@Override
	public void onSurfaceCreated() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onSurfaceChanged() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onFrameRendered(float time) {
		for (int i = 0; i < playerScores.size(); ++i) {
			HouseController hc = World.getInstance().getPlayer(playerScores.get(i).playerID);
			playerScores.get(i).points.setText(String.valueOf((int)hc.getHouse().points));
			
			if (i > 0 && !playerScores.get(i).name.isMoving() && !playerScores.get(i-1).name.isMoving()) {
				if ((int)hc.getHouse().points > (int)World.getInstance().getPlayer(playerScores.get(i-1).playerID).getHouse().points) {
					Vector2 offset = new Vector2(0, -playerScores.get(i).name.getSize().y);
					Score s = playerScores.get(i);
					s.name.moveBy(offset, 1);
					s.points.moveBy(offset, 1);
					offset = new Vector2(0, playerScores.get(i).name.getSize().y);
					playerScores.get(i-1).name.moveBy(offset, 1);
					playerScores.get(i-1).points.moveBy(offset, 1);
					
					playerScores.set(i, playerScores.get(i-1));
					playerScores.set(i-1, s);
				}
			} 
		}
	}

	public void show() {
		active = true;
		Skin skin = WindowManager.getInstance().getSkin();
		String tmp = skin.LABEL_FONT;
		skin.LABEL_FONT = "FONT_14";
		
		Vector2 basePos = new Vector2(BOARD_POS);
		
		if (playerScores.size() > 0) {
			basePos.y += playerScores.get(0).name.getSize().y * (playerScores.size()+1);
		}
		
		Label label = (Label) WindowManager.getInstance().createLabel("SCOREBOARD/GOAL", basePos, "   Goal");
		label.setFont(SceneManager.getInstance().getFont("FONT_14"));
		label.setColor(Color.BLACK);
		label.setBgColor(new Color(0.76f, 0.635f, 0.38f, 0.7f));
		label.getBackground().setSize(new Vector2(SIZE.get().x, label.getFormattedText().getSize().y));
		
		
		skin.LABEL_FONT = tmp;
		
		label = (Label) WindowManager.getInstance().createLabel("SCOREBOARD_POINTS" + Math.random(), basePos.add(POINT_OFFSET.get()), String.valueOf((int)World.getInstance().goalPoints));
		label.setFont(SceneManager.getInstance().getFont("FONT_14"));
		label.setColor(new Color(0.3f, 0.19f, 0.12f, 1));
		label.setAlignment(ALIGNMENT.RIGHT);
	}
	
}
