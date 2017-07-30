package com.tyrfing.games.id17.diplomacy.actions;

import com.tyrfing.games.id17.diplomacy.Message;
import com.tyrfing.games.id17.gui.mails.DiploYesNoMail;
import com.tyrfing.games.id17.gui.mails.HeaderedMail;
import com.tyrfing.games.id17.gui.mails.Mail;
import com.tyrfing.games.id17.houses.House;
import com.tyrfing.games.id17.world.World;

public class InviteToIntrigue extends DiploAction {

	/**
	 * 
	 */
	private static final long serialVersionUID = -24573796462321804L;

	public static final int ID = 6;
	
	public static final int HONOR_COST = 40;
	public static final int FAVOR = 20;
	
	public InviteToIntrigue() {
		super("Invite", ID, 1, true);
	}

	@Override
	public boolean isEnabled(House sender, House receiver) {
		return InviteToIntrigue.sIsEnabled(sender, receiver);
	}
	
	public static boolean sIsEnabled(House sender, House receiver) {
		return 		sender.intrigueProject != null
				&&	sender.intrigueProject.receiver != receiver
				&&  !sender.intrigueProject.supporters.contains(receiver)
				&&  	(sender.haveSameOverlordWith(receiver)  || sender.isRealmNeighbour(receiver)
					|| 	 sender.intrigueProject.receiver.haveSameOverlordWith(receiver) ||  sender.intrigueProject.receiver.isRealmNeighbour(receiver));
	}
	
	public static boolean refusingCostsHonor(House sender, House receiver) {
		return sender.getHouseStat(receiver, House.FAVOR_STAT) >= FAVOR;
	}

	@Override
	public void execute(House sender, House receiver, int[] options) {
		receiver.changeHouseStat(sender, House.FAVOR_STAT, FAVOR);
		sender.changeHouseStat(receiver, House.FAVOR_STAT, -FAVOR);
		sender.intrigueProject.addSupporter(receiver);
	}
	
	@Override
	public void respond(Message message) {
		super.respond(message);
		if (message.sender.intrigueProject != null) {
			if (message.response > 0) {
				execute(message.sender, message.receiver, message.options);
			} else {
				if (refusingCostsHonor(message.sender, message.receiver)) {
					message.receiver.changeHouseStat(message.sender, House.FAVOR_STAT, FAVOR);
					message.sender.changeHouseStat(message.receiver, House.FAVOR_STAT, -FAVOR);
					message.receiver.changeHonor(-HONOR_COST);
				} 
				message.sender.intrigueProject.rejectInvite(message.receiver);
			}
		}
	}
	
	@Override
	public Mail getSendMail(Message message) {
		if (message.sender.intrigueProject == null) return null;
		
		String msg = "House " + message.sender.getLinkedName() + " invites us to support\ntheir plot:\n" + message.sender.intrigueProject.toString() + "\n";
		if (message.sender.getHouseStat(message.receiver, House.FAVOR_STAT) >= FAVOR) {
			msg += "Declining would be dishonorable,\nas we owe them <#ff0000>"  + FAVOR + "\\# favor.";
		} else {
			msg += "They would owe us <#009030>"  + FAVOR + "\\# favor.";
		}
		Mail mail = new DiploYesNoMail("Request: Support Intrigue", msg,  message);
		mail.setIconName("Intrigue");
		
		return mail;
	}
	
	@Override
	public Mail getResponseMail(Message message) {
		Mail mail;
		if (message.response > 0) {
			mail = new HeaderedMail("Request: Support Intrigue\nAccepted", "House " + message.receiver.getLinkedName() + " has accepted to join\nour plotting efforts.", message.sender, message.receiver);
		} else {
			mail = new HeaderedMail("Request: Support Intrigue\nRejected", "House " + message.receiver.getLinkedName() + " has rejected to join\nour plotting efforts.", message.sender, message.receiver);
		}
		mail.setIconName("Intrigue");
		return mail;
	}
	
	@Override
	public Mail getOptionMail(final House sender, final House receiver) {
		final InviteToIntrigue request = this;
		String msg = "";
		if (sender.getHouseStat(receiver, House.FAVOR_STAT) >= FAVOR) {
			msg = 	"We would invite house " + receiver.getLinkedName() + 
					"\nto join our plot. They are obligated to\naccept since they owe us <#009030>" + 
					FAVOR + 
					"\\# favor.\n\nThey have <#009030>" + 
					receiver.getCourtPower(sender.intrigueProject.receiver) + 
					"\\# court power to contribute\ntowards our plotting efforts.";
		} else {
			msg = 	"We would invite house " + receiver.getName() + 
					"\nto join our plot.\nWe would owe them <#ff0000>" + 
					FAVOR + 
					"\\# favor.\n\nThey have <#009030>" + 
					receiver.getCourtPower(sender.intrigueProject.receiver) + 
					"\\# court power to contribute\ntowards our plotting efforts.";
		}
		
		HeaderedMail mail = new HeaderedMail("Request: Support Intrigue", msg, sender, receiver) {
			@Override 
			public void onAccept() { 
				Message message = new Message(request, sender, receiver, null);
				message.action.send(message.sender, message.receiver, null);
				remove();
			}
		};
		mail.setIconName("Intrigue");
		mail.addAcceptButton();
		return mail;
	}

}
