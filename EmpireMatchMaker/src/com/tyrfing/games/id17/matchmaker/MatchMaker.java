package com.tyrfing.games.id17.matchmaker;

import java.io.IOException;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.List;

import com.tyrfing.games.id17.EmpireFrameListener;
import com.tyrfing.games.id17.startmenu.PlayerUpdate;
import com.tyrlib2.networking.Connection;
import com.tyrlib2.networking.INetworkListener;
import com.tyrlib2.networking.Network;

public class MatchMaker extends Thread {
	
	private Network playerNetwork;
	private Network hostNetwork;
	private List<HostedGame> hostedGames = new ArrayList<HostedGame>();
	private boolean running;
	
	public static final long PLAYER_COUNT_UPDATES = 500;
	public static final int MAX_COUNT_HOSTED_GAMES = 5;
	
	private int players;
	private int maxPlayers;
	
	private PlayerListener playerListener;
	private HostListener hostListener;
	
	private int nextID;
	
	private boolean backconnectOk = true;
	private Process waiting;
	
	public MatchMaker(int port1, int port2) {
		System.out.println("Starting matchmaker on ports: " +  port1 + ", " + port2 + "...");
		
		playerListener = new PlayerListener(this);
		hostListener = new HostListener(this);
		
		playerNetwork = new Network();
		playerNetwork.addListener(playerListener);
		playerNetwork.setLog(false);
		playerNetwork.setMeasureBandwithUse(false);
		playerNetwork.host(port1);
		
		hostNetwork = new Network();
		hostNetwork.addListener(hostListener);
		hostNetwork.setLog(false);
		hostNetwork.setMeasureBandwithUse(false);
		hostNetwork.host(port2);
		
		System.out.println("... start successfull.");
	}
	
	public void onGameOpened(HostedGame game) {
		System.out.println("... new hosted game successfully opened!");
		game.id = nextID++;
		maxPlayers += game.maxPlayerCount;
		game.port = 6000 + hostedGames.size();
		game.process = waiting;
		hostedGames.add(game);
		
		backconnectOk = true;
	}
	
	public void onGameClosed(HostedGame game) {
		System.out.println("Game " + game.id + " closed.");
		maxPlayers -= game.maxPlayerCount;
		hostedGames.remove(game);
	}
	
	public HostedGame getGame(Connection c) {
		for (int i = 0; i < hostedGames.size(); ++i)  {
			if (hostedGames.get(i).connection == c) {
				return hostedGames.get(i);
			}
		}
		
		return null;
	}
	
	public void hostGame() {
		
		try {
			backconnectOk = false;
			int serverPort = hostNetwork.getServer().getPort();
			int port = 6000 + hostedGames.size();
			String address = "swordscroll.com";
			
			ProcessBuilder pb = new ProcessBuilder(	"java", "-jar", "SwordScroll.jar", 
													"-host", String.valueOf(port), String.valueOf(serverPort), address);
			
			waiting = pb.start();
			waiting.getOutputStream().close();
			waiting.getErrorStream().close();
			waiting.getInputStream().close();
			
			
			//waiting = Runtime.getRuntime().exec(" -jar SwordScroll.jar -host " + port + " " + serverPort + " " + address);
			System.out.print("Successfully started a new game on port " +  port);
			System.out.print( "... awaiting connection...");
			System.out.println(" backaddress is " + address + ":" + serverPort + "...");
		} catch (IOException e) {
			e.printStackTrace();
		}

	}
	
	public boolean needNewGame() {
		return 		players >= maxPlayers * 0.5f * hostedGames.size() 
				&& 	hostedGames.size() <= MAX_COUNT_HOSTED_GAMES;
	}
	
	public void onPlayerConnected(Connection c) {
		players++;
		
		final Network network = new Network();
		network.addListener(new INetworkListener() {
			@Override
			public void onNewConnection(Connection c) {
				playerNetwork.send(EmpireFrameListener.CAN_HOST, c);
				network.close();
			}

			@Override
			public void onConnectionLost(Connection c) {
				
			}

			@Override
			public void onReceivedData(Connection c, Object o) {
				
			}
		});
		network.setLog(false);
		network.setMeasureBandwithUse(false);
		
		try {
			try {
				network.connectTo(c.getServerName(), c.getServerPort());
			} catch (UnknownHostException e) {
				playerNetwork.send(EmpireFrameListener.CANNOT_HOST, c);
			} catch (IOException e) {
				playerNetwork.send(EmpireFrameListener.CANNOT_HOST, c);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public void onPlayerDisconnected(Connection c) {
		players--;
	}

	
	@Override
	public void run() {
		running = true;
		
		try {
			while (running) {
				try {
					Thread.sleep(PLAYER_COUNT_UPDATES);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
				
				update();
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public void update() throws IOException {
		if (needNewGame() && backconnectOk) {
			hostGame();
		}
		
		playerNetwork.broadcast(create(hostedGames));
	}

	public void updatePlayers(HostedGame game, int players, int maxPlayers, int port) {
		game.playerCount = players;
		this.maxPlayers -= game.maxPlayerCount;
		game.maxPlayerCount = maxPlayers;
		this.maxPlayers += game.maxPlayerCount;
		game.port = port;
	}
	
	public static PlayerUpdate create(List<HostedGame> hostedGames) {
		PlayerUpdate u = new PlayerUpdate();
		u.current = new int[hostedGames.size()];
		u.max = new int[hostedGames.size()];
		u.id = new int[hostedGames.size()];
		u.address = new String[hostedGames.size()];
		
		for (int i = 0; i < hostedGames.size(); ++i) {
			u.current[i] = hostedGames.get(i).playerCount;
			u.max[i] = hostedGames.get(i).maxPlayerCount;
			u.id[i] = hostedGames.get(i).id;
			String address = 	  hostedGames.get(i).connection.getServerName() 
								+ ":" 
								+ hostedGames.get(i).port;
			u.address[i] = address;
		}
		
		return u;
	}
}
