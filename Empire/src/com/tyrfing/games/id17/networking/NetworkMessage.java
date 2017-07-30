package com.tyrfing.games.id17.networking;

import java.io.Serializable;

import com.tyrlib2.networking.Connection;

public abstract class NetworkMessage implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 3871360356354324649L;

	public void process(Connection c) { }
	
}
