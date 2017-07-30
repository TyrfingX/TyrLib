package com.tyrfing.games.id17.diplomacy;

import java.io.Serializable;
import java.util.Vector;

import com.tyrlib2.game.IUpdateable;

public class MessageExecutor implements IUpdateable, Serializable  {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 4053739401883452551L;
	
	private Vector<Message> messages = new Vector<Message>();
	
	public MessageExecutor() {
		
	}

	public void addMessage(Message message) {
		messages.add(message);
	}
	
	@Override
	public void onUpdate(float time) {
		while(!messages.isEmpty()) {
			Message message = messages.get(0);
			if (message.response != -1) {
				message.action.respond(message);
			} else {
				message.action.send(message);
			}
			messages.remove(0);
		}
	}

	@Override
	public boolean isFinished() {
		return false;
	}
}
