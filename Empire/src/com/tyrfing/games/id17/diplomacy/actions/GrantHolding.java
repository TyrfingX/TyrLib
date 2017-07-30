package com.tyrfing.games.id17.diplomacy.actions;

import com.tyrfing.games.id17.diplomacy.Message;
import com.tyrfing.games.id17.gui.mails.HoldingMail;
import com.tyrfing.games.id17.gui.mails.Mail;
import com.tyrfing.games.id17.holdings.Barony;
import com.tyrfing.games.id17.holdings.Holding;
import com.tyrfing.games.id17.houses.House;
import com.tyrfing.games.id17.houses.StatModifier;
import com.tyrfing.games.id17.world.World;

public class GrantHolding extends DiploAction{

	/**
	 * 
	 */
	private static final long serialVersionUID = -2347138858339897430L;
	public static final int RELATION_GRANT_SUB_HOLDING = 25;
	public static final int RELATION_GRANT_BARONY = 50;
	public static final float RELATION_GRANT_DURATION = 80 * World.DAYS_PER_YEAR * World.SECONDS_PER_DAY;
	
	public static final int ID = 4;
	
	public GrantHolding() {
		super("Grant", ID, 0, true);
	}

	@Override
	public boolean isEnabled(House sender, House receiver) {
		return 	    sender != receiver
				&&  sender.getBaronies().size() > 0
				&&	receiver.isSubjectOf(sender);
	}

	@Override
	public void execute(House sender, House receiver, int[] options) {
		
		Holding holding = World.getInstance().getHolding(options[0]);
		
		if (receiver == holding.getOwner()) return;
		
		House.transferHolding(receiver, holding, true);
		
		if (holding instanceof Barony) {
		
			Barony barony = (Barony) holding;
			World.getInstance().getMap().changeOwner(barony);
		
			receiver.addStatModifier(new StatModifier("Granted Holding", House.RELATION_STAT, receiver, sender, RELATION_GRANT_DURATION, RELATION_GRANT_BARONY));
		} else {
			receiver.addStatModifier(new StatModifier("Granted Barony", House.RELATION_STAT, receiver, sender, RELATION_GRANT_DURATION, RELATION_GRANT_SUB_HOLDING));
		}
	}
	
	@Override
	public void respond(Message message) {
		super.respond(message);
		if (message.response > 0) {
			execute(message.sender, message.receiver, message.options);
		}
	}
	
	@Override
	public Mail getOptionMail(House sender, House receiver) {
		return new HoldingMail(HoldingMail.MailType.GRANT_HOLDING_SEND, new Message(this, sender, receiver, null));
	}
	
	@Override
	public Mail getResponseMail(Message message) {
		return new HoldingMail(HoldingMail.MailType.GRANT_HOLDING_RESPONSE, message);
	}

}
