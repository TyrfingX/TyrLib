package com.tyrfing.games.id17.matchmaker;


import com.tyrlib2.networking.Connection;

public class HostedGame {
	public int playerCount;
	public int maxPlayerCount = 16;
	public Connection connection;
	public int id;
	public int port;
	public Process process;
}
