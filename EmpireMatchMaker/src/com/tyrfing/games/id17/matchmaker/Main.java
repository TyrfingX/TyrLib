package com.tyrfing.games.id17.matchmaker;

public class Main {
	
	public static final int DEFAULT_PORT1 = 3000;
	public static final int DEFAULT_PORT2 = 3001;
	
	public static final void main(String[] args) {
		int port1 = (args.length > 0) ? Integer.valueOf(args[0]) : DEFAULT_PORT1;
		int port2 = (args.length > 1) ? Integer.valueOf(args[1]) : DEFAULT_PORT2;
		MatchMaker mm = new MatchMaker(port1, port2);
		mm.start();
	}
}
