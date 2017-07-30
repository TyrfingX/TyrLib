package com.tyrfing.games.id17.networking;

import java.io.Serializable;

public class PlayerQuit implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 5573079986456649711L;

	public final int playerID;
	
	public PlayerQuit(int playerID) {
		this.playerID = playerID;
	}
	
}
