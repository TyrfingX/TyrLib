package com.tyrfing.games.id17.diplomacy.actions;

import com.tyrfing.games.id17.diplomacy.Diplomacy;
import com.tyrfing.games.id17.diplomacy.Message;
import com.tyrfing.games.id17.diplomacy.category.RelationsCategory;
import com.tyrfing.games.id17.gui.mails.DiploYesNoMail;
import com.tyrfing.games.id17.gui.mails.HeaderedMail;
import com.tyrfing.games.id17.gui.mails.Mail;
import com.tyrfing.games.id17.houses.House;
import com.tyrfing.games.id17.war.War;

public class DeclareIndependence extends DiploAction {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1012549659893550929L;
	public static final int ID = 18;
	
	public DeclareIndependence() {
		super("Declare", ID, 1, true);
	}

	@Override
	public boolean isEnabled(House sender, House receiver) {
		return    sender.getHouseStat(receiver, House.HAS_MARRIAGE) != 1 
			   && sender.getRelation(receiver) <= 0
			   && sender.getHouseStat(receiver, House.HAS_TRUCE) == 0
			   && sender.getSupremeOverlord() == receiver;
	}

	@Override
	public void execute(House sender, House receiver, int[] options) {
		if (!this.isEnabled(sender, receiver)) return;
		
		for (int i = 0; i < sender.getCountWars(); ++i) {
			War war = sender.getWar(i);
			if (war.goal.goalHolding != null) {
				if (war.goal.goalHolding.getOwner().isSubjectOf(sender)) {
					war.end();
					--i;
				}
			}
		}
		
		receiver.removeSubHouse(sender);
		
		receiver.updateBorders();
		sender.updateBorders();
	}
	
	@Override
	public Mail getOptionMail(final House sender, final House receiver) {
		String msg = "If our Lord Family refuses to accept\n" + 
					 "our independence, we will be at war\n" + 
					 "with them.\n";
		
		final DeclareIndependence request = this;
		HeaderedMail mail = new HeaderedMail("Declare Independence", 
											 msg,
											 sender, receiver) {
			@Override 
			public void onAccept() { 
				Message message = new Message(request, sender, receiver, null);
				message.action.send(message.sender, message.receiver, null);
				remove();
			}
		};
		mail.addAcceptButton();
		return mail;
	}
	
	@Override
	public void respond(Message message) {
		super.respond(message);
		execute(message.sender, message.receiver, message.options);
		if (message.response > 0) {
			
		} else {
			int[] warOptions = { -1, message.sender.id };
			Diplomacy.getInstance().getAction(Diplomacy.RELATIONS_ID, RelationsCategory.DECLARE_WAR).execute(message.sender, message.receiver, warOptions);
		}
	}
	
	@Override
	public Mail getSendMail(Message message) {
		return new DiploYesNoMail("Declare Independece", 
								  "The despicable House " + message.sender.getLinkedName() + " have\n" +
								  "declared their independence from us.\n\n" + 
								  "Not accepting their declaration\n" + 
								  "will mean war.", message);
	}

	@Override
	public Mail getResponseMail(Message message) {
		if (message.response > 0) {
			String msg = "Our independence declaration has\nbeen acknowledged.";
			return new HeaderedMail("Declare Independence:\nAccepted", msg, message.sender, message.receiver);
		} else {
			return new HeaderedMail("Declare Independence:\nRejected", "Our independence declaration has\nnot been acknowledged.\n\nThis means WAR!", message.sender, message.receiver);
		}
	}
	
}
