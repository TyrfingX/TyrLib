package com.tyrfing.games.id17.diplomacy.actions;

import com.tyrfing.games.id17.Util;
import com.tyrfing.games.id17.diplomacy.Message;
import com.tyrfing.games.id17.gui.mails.GiftMail;
import com.tyrfing.games.id17.gui.mails.GiftMail.STATUS;
import com.tyrfing.games.id17.gui.mails.Mail;
import com.tyrfing.games.id17.houses.House;
import com.tyrfing.games.id17.houses.StatModifier;
import com.tyrfing.games.id17.world.World;

public class SendGift extends DiploAction {

	/**
	 * 
	 */
	private static final long serialVersionUID = 5065162539027066802L;
	public static final float SENDER_INCOME_FACTOR = 10;
	public static final float RECEIVER_INCOME_FACTOR = 10;
	public static final int RECEIVER_RELATION_MAX = 40;
	public static final int RECEIVER_RELATION_MIN = 20;
	public static final float RELATION_DURATION = 5 * World.DAYS_PER_YEAR * World.SECONDS_PER_DAY;
	
	public static final int ID = 12;
	
	public SendGift() {
		super("Send Gift", ID, 0, true);
		disabledText = "FAILED TO UPDATE";
	}

	@Override
	public boolean isEnabled(House sender, House receiver) {
		return sender.isEnemy(receiver) == null && sender.getGold() >= getCosts(sender);
	}
	
	@Override
	public void setDisabledText(House sender, House receiver) {
		disabledText = Util.getFlaggedText(" (1)", sender.getGold() >= getCosts(sender)) + " Gold >= " + getCosts(sender) + " ";
	}	

	@Override
	public void execute(House sender, House receiver, int[] options) {
		int costs = getCosts(sender);
		
		int relationChange = getRelationChange(sender, receiver);
		
		receiver.changeGold(costs);
		sender.changeGold(-costs);
		receiver.addStatModifier(new StatModifier("Gift", House.RELATION_STAT, receiver, sender, RELATION_DURATION, relationChange));
		sender.addStatModifier(new StatModifier("Gift", House.RELATION_STAT, sender, receiver, RELATION_DURATION, relationChange));

	}
	
	public static int getCosts(House house) {
		return (int) ( house.getIncome() * SENDER_INCOME_FACTOR + 1 );
	}
	
	public static int getRelationChange(House sender, House receiver) {
		int gifted = (int) receiver.getModifierValue("Gift", sender);
		int giftValue = (int) ((RECEIVER_RELATION_MAX - RECEIVER_RELATION_MIN) * Math.min(1, (getCosts(sender) / (receiver.getIncome() * RECEIVER_INCOME_FACTOR)))) + RECEIVER_RELATION_MIN;
		return Math.min(giftValue, RECEIVER_RELATION_MAX - gifted);
	}
	
	@Override
	public Mail getOptionMail(House sender, House receiver) {
		return new GiftMail(STATUS.GIFT_SEND, new Message(this, sender, receiver, null));
	}
	
	@Override
	public Mail getSendMail(Message message) {
		return new GiftMail(STATUS.GIFT_RECEIVED, message);
	}

}
