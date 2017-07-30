package com.tyrfing.games.id17.diplomacy.actions;

import com.tyrfing.games.id17.Action;
import com.tyrfing.games.id17.EmpireFrameListener;
import com.tyrfing.games.id17.diplomacy.Message;
import com.tyrfing.games.id17.gui.mails.Mail;
import com.tyrfing.games.id17.houses.House;
import com.tyrfing.games.id17.networking.NetworkAction;
import com.tyrfing.games.id17.world.World;

public abstract class DiploAction extends Action {
	
	
	/**
	 * 
	 */
	private static final long serialVersionUID = -914742860012571831L;
	protected int responses;
	protected boolean hasOptions;

	public final int id;
	public final static int COUNT_DIPLO_ACTIONS = 22;
	
	public DiploAction(String name, int id, int responses, boolean hasOptions) {
		super(name);
		
		this.id = id;
		
		this.responses = responses;
		this.hasOptions = hasOptions;
	}
	
	public abstract boolean isEnabled(House sender, House receiver);
	public abstract void execute(House sender, House receiver, int[] options);
	
	public void send(Message message) {
		if (message.action.isEnabled(message.sender, message.receiver)) {
			message.receiver.getController().informMessage(message);
			
			message.sender.isOnCooldown(true);
			if (responses == 0) {
				execute(message.sender, message.receiver, message.options);
			}
		}
	}
	
	
	public void send(House sender, House receiver, int[] options) {
		Message message = new Message(this, sender, receiver, options);
		if (EmpireFrameListener.MAIN_FRAME.getNetwork().isHost()) {
			send(message);
		} else {
			EmpireFrameListener.MAIN_FRAME.getNetwork().broadcast(new NetworkAction(message));
		}
	}

	public void respond(Message message) { 
		message.sender.getController().informMessage(message);
	}
	
	public int getResponses() {
		return responses;
	}
	
	public boolean hasOptions() {
		return hasOptions;
	}
	
	/**
	 * Mail displayed upon receiving a sent mail
	 * @param message
	 * @return
	 */
	
	public Mail getSendMail(Message message) {
		return null;
	}
	
	/**
	 * Mail displayed upon receiving a response to a mail
	 * @param message
	 * @return
	 */
	
	public Mail getResponseMail(Message message) {
		return null;
	}
	
	/**
	 * Mail displayed upon successful execution of this action
	 * @param message
	 * @return
	 */
	
	public Mail getExecutionMail(Message message) {
		return null;
	}
	
	/**
	 * Mail displayed when selecting this action in the UI and then filling out the options
	 */
	
	public Mail getOptionMail(House sender, House receiver) {
		return null;
	}
	
	@Override
	public void selectedByUser(House sender, House receiver) {
		if (!hasOptions()) {
			Message m = new Message(this, sender, receiver, null);
			Mail mail = getExecutionMail(m);
			
			if (mail != null)  {
				World.getInstance().getMainGUI().mailboxGUI.addMail(mail, true);
			}
			
			send(sender, receiver, null);
			
		} else {
			String identity = sender.toString()+receiver.toString()+this.toString();
			if (!World.getInstance().getMainGUI().mailboxGUI.showIdentity(identity)) {
				Mail mail = getOptionMail(sender, receiver);
				mail.setIdentity(identity);
				World.getInstance().getMainGUI().mailboxGUI.addMail(mail, true);
			}
		}
	}
}
