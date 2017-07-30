package com.tyrfing.games.id17.diplomacy.actions;

import com.tyrfing.games.id17.Util;
import com.tyrfing.games.id17.diplomacy.Message;
import com.tyrfing.games.id17.gui.mails.DiploYesNoMail;
import com.tyrfing.games.id17.gui.mails.HeaderedMail;
import com.tyrfing.games.id17.gui.mails.Mail;
import com.tyrfing.games.id17.houses.House;
import com.tyrfing.games.id17.war.War;

/**
 * OPTIONS:
 * 0: WAR ID
 * @author Sascha
 *
 */

public class HonorDefensivePact extends DiploAction {

	/**
	 * 
	 */
	private static final long serialVersionUID = -225854159482774040L;
	public static final int ID = 5;
	public static final int HONOR = -40;
	public static final int FAVOR = 40;
	public static final int FAVOR_REBELS = 20;
	public static final int FAVOR_MARAUDER = 5;
	
	public HonorDefensivePact() {
		super("Honor Defensive Pact", ID, 1, true);
	}

	@Override
	public boolean isEnabled(House sender, House receiver) {
		return receiver.getHouseStat(sender, House.HAS_DEFENSIVE_PACT) == 1;
	}

	@Override
	public void execute(House sender, House receiver, int[] options) {
		War war = sender.getWarByAttackerID(options[0]);
		if (war != null) {
			for (int i = 0; i < war.attackers.size(); ++i) {
				DefensivePact.removeDefensivePact(receiver, war.attackers.get(i));
			}
			
			House other = war.getOther(sender);
			int favor = other.isRebel() ? (other.isMarauder() ? FAVOR_MARAUDER : FAVOR) : FAVOR_REBELS;
			receiver.changeFavor(sender, favor);
			
			war.addDefenderAlly(receiver);
		}
	}

	@Override
	public void respond(Message message) {
		super.respond(message);
		if (message.response > 0) {
			execute(message.sender, message.receiver, message.options);
		} else {
			message.receiver.changeHonor(HONOR);
			DefensivePact.removeDefensivePact(message.sender, message.receiver);
		}
	}
	
	@Override
	public Mail getSendMail(Message message) {
		War war = message.sender.getWarByAttackerID(message.options[0]);
		if (war != null) {
			House other = war.getOther(message.sender);
			int favor = other.isRebel() ? (other.isMarauder() ? FAVOR_MARAUDER : FAVOR) : FAVOR_REBELS;
			return new DiploYesNoMail(
					"Obligation: Honor defensive Pact", 
					"Our allies " + message.sender.getLinkedName() + " are being attacked\n" + 
					"by the " + other.getLinkedName() + "\n" +
					"\nDecline: " + Util.getFlaggedText(String.valueOf(HONOR), false) + " <img MAIN_GUI HONOR_ICON>" + 
					"\nAgree: " + Util.getFlaggedText(Util.getSignedText(favor), true) + " Favor.",
					message);
		} else {
			return null;
		}
	}
	
	@Override
	public Mail getResponseMail(Message message) {
		if (message.response > 0) {
			return new HeaderedMail("Defensive Pact honored!", "The graceful " + message.receiver.getLinkedName() + " have honored\nour defensive pact and will\nbe joining our war!" ,message.sender, message.receiver);
		} else {
			return new HeaderedMail("Defensive Pact broken!", "The disgusting " + message.receiver.getLinkedName() + " have neglected\nour defensive pact!" ,message.sender, message.receiver);
		}
	}
}
