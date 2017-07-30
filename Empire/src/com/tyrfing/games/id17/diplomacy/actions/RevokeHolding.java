package com.tyrfing.games.id17.diplomacy.actions;

import com.tyrfing.games.id17.diplomacy.Message;
import com.tyrfing.games.id17.gui.mails.HoldingMail;
import com.tyrfing.games.id17.gui.mails.Mail;
import com.tyrfing.games.id17.holdings.Barony;
import com.tyrfing.games.id17.holdings.Holding;
import com.tyrfing.games.id17.houses.House;
import com.tyrfing.games.id17.houses.StatModifier;
import com.tyrfing.games.id17.houses.VaryingStatModifier;
import com.tyrfing.games.id17.world.World;

public class RevokeHolding extends DiploAction{

	/**
	 * 
	 */
	private static final long serialVersionUID = -6421009403273540185L;
	public static final int RELATION_REVOKE_SUB_HOLDING = -50;
	public static final int RELATION_REVOKE_BARONY = -70;
	public static final float RELATION_REVOKE_DURATION = 40 * World.DAYS_PER_YEAR * World.SECONDS_PER_DAY;
	
	public static final int TYRANNY = 30;
	public static final float TYRANNY_DECAY = -1f / World.DAYS_PER_SEASON;
	public static final float TYRANNY_DURATION = 10 * World.DAYS_PER_YEAR * World.SECONDS_PER_DAY;
	
	public static final int ID = 10;
	
	public RevokeHolding() {
		super("Revoke", ID, 1, true);
	}

	@Override
	public boolean isEnabled(House sender, House receiver) {
		return true;
	}

	@Override
	public void respond(Message message) {
		super.respond(message);
		execute(message.sender, message.receiver, message.options);
	}
	
	@Override
	public void execute(House sender, House receiver, int[] options) {
		
		Holding holding = World.getInstance().getHolding(options[0]);
		
		House.transferHolding(sender, holding, true);
		
		if (holding instanceof Barony) {
			Barony barony = (Barony) holding;
			World.getInstance().getMap().changeOwner(barony);
			receiver.addStatModifier(new StatModifier("Revoked Holding", House.RELATION_STAT, receiver, sender, RELATION_REVOKE_DURATION, RELATION_REVOKE_BARONY));
		} else {
			receiver.addStatModifier(new StatModifier("Revoked Barony", House.RELATION_STAT, receiver, sender, RELATION_REVOKE_DURATION, RELATION_REVOKE_SUB_HOLDING));
		}
		
		sender.addStatModifier(new VaryingStatModifier("Revocation", House.TYRANNY, sender, -1, TYRANNY, TYRANNY_DECAY, 0));
		
		for (int i = 0; i < sender.getSubHouses().size(); ++i) {
			sender.getSubHouses().get(i).addStatModifier(new StatModifier("Tyranny", House.RELATION_STAT, sender.getSubHouses().get(i), sender, TYRANNY_DURATION, -TYRANNY));
		}
	}
	
	@Override
	public Mail getOptionMail(House sender, House receiver) {
		return new HoldingMail(HoldingMail.MailType.REVOKE_HOLDING_SEND, new Message(this, sender, receiver, null));
	}
	
	@Override
	public Mail getResponseMail(Message message) {
		return new HoldingMail(HoldingMail.MailType.REVOKE_HOLDING_RESPONSE, message);
	}
	
	@Override
	public Mail getSendMail(Message message) {
		return new HoldingMail(HoldingMail.MailType.REVOKE_HOLDING_RECEIVED, message);
	}

}
