package com.tyrfing.games.id17.ai;

import java.io.Serializable;

import com.tyrfing.games.id17.diplomacy.actions.DiploAction;
import com.tyrfing.games.id17.houses.House;

class Memory implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 6026086358185218391L;
	transient DiploAction action;
	House sender;
	House receiver;
	int[] options;
	float timestamp;
	int response;
	
	public Memory(DiploAction action, House sender, House receiver, int[] options, float timestamp, int response) {
		this.action = action;
		this.sender = sender;
		this.receiver = receiver;
		this.options = options;
		this.timestamp = timestamp;
		this.response = response;
	}
}