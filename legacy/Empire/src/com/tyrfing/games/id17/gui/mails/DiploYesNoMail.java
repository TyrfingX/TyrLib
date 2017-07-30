package com.tyrfing.games.id17.gui.mails;

import com.tyrfing.games.id17.diplomacy.Message;
import com.tyrfing.games.id17.diplomacy.actions.DiploAction;
import com.tyrfing.games.id17.houses.House;
import com.tyrfing.games.id17.world.World;

public class DiploYesNoMail extends YesNoMail {

	private Message message;
	private DiploAction action;
	
	public DiploYesNoMail(String title, String text, Message message) {
		super(title, text, message.sender, message.receiver, message.timeStamp);
		this.message = message;
	}
	
	public DiploYesNoMail(String title, String text, Message message, String leftLabel, String rightLabel) {
		super(title, text, message.sender, message.receiver, message.timeStamp);
		this.message = message;
		accept.getLabel().setText(leftLabel);
		reject.getLabel().setText(rightLabel);
	}
	
	public DiploYesNoMail(String title, String text, DiploAction action, House sender, House receiver) {
		super(title, text, sender, receiver, World.getInstance().getWorldTime());
		this.action = action;
	}
	
	@Override
	protected void onAccept() {
		responded = true;
		if (message != null) {
			message.respond(1);
		} else {
			action.send(sender, receiver, new int[] { 1 });
		}
		this.remove();
	}
	
	@Override
	protected void onReject() {
		responded = true;
		if (message != null) {
			message.respond(0);
		} else {
			action.send(sender, receiver, new int[] { 0 });
		}
		this.remove();
	}

}
