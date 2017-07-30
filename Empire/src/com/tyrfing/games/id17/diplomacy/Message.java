package com.tyrfing.games.id17.diplomacy;

import java.io.Serializable;

import com.tyrfing.games.id17.EmpireFrameListener;
import com.tyrfing.games.id17.ai.AIThread;
import com.tyrfing.games.id17.diplomacy.actions.DiploAction;
import com.tyrfing.games.id17.houses.House;
import com.tyrfing.games.id17.networking.NetworkAction;
import com.tyrfing.games.id17.world.World;

public class Message implements Serializable {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = -4159846275262150397L;

	public static final float RESPONSE_TIME = 60 * World.SECONDS_PER_DAY;
	
	public final DiploAction action;
	public final House sender;
	public final House receiver;
	public int response;
	public int[] options;
	public final float timeStamp;
	
	public Message(DiploAction action, House sender, House receiver, int[] options) {
		this.action = action;
		this.sender = sender;
		this.receiver = receiver;
		this.response = -1;
		this.options = options;
		timeStamp = World.getInstance().getWorldTime();
	}
	
	public Message(DiploAction action, House sender, House receiver, int response, int[] options) {
		this.action = action;
		this.sender = sender;
		this.receiver = receiver;
		this.response = response;
		this.options = options;
		timeStamp = World.getInstance().getWorldTime();
	}
	
	public void respond(int response) {
		this.response = response;
		if (EmpireFrameListener.MAIN_FRAME.getNetwork().isHost()) {
			AIThread.getInstance().addMessage(this);
		} else {
			EmpireFrameListener.MAIN_FRAME.getNetwork().broadcast(new NetworkAction(this));
		}
	}
}
