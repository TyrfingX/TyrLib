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

public class HonorProtect extends DiploAction {

	/**
	 * 
	 */
	private static final long serialVersionUID = -225854159482774040L;
	public static final int ID = 23;
	public static final int HONOR = -40;
	public static final int FAVOR = 40;
	
	public HonorProtect() {
		super("Honor Defensive Pact", ID, 1, true);
	}

	@Override
	public boolean isEnabled(House sender, House receiver) {
		return receiver.isProtector(sender);
	}

	@Override
	public void execute(House sender, House receiver, int[] options) {
		War war = sender.getWarByAttackerID(options[0]);
		if (war != null) {
			for (int i = 0; i < war.attackers.size(); ++i) {
				DefensivePact.removeDefensivePact(receiver, war.attackers.get(i));
			}
			
			receiver.changeHouseStat(sender, House.FAVOR_STAT, FAVOR);
			sender.changeHouseStat(receiver, House.FAVOR_STAT, -FAVOR);
			
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
		}
	}
	
	@Override
	public Mail getSendMail(Message message) {
		War war = message.sender.getWarByAttackerID(message.options[0]);
		House other = war.getOther(message.sender);
		Mail mail = new DiploYesNoMail(
				"Obligation: Defend Protectoriat", 
				"Our protectoriat " + message.sender.getLinkedName() + " is" +
				"\nbeing attacked by " + other.getLinkedName() + ".\n" + 
				"\nDecline: " + Util.getFlaggedText(String.valueOf(HONOR), false) + " <img MAIN_GUI HONOR_ICON>" + 
				"\nAgree: " + Util.getFlaggedText(Util.getSignedText(FAVOR), true) + " favor.",
				message); 
		mail.setIconName("War");
		return mail;
	}
	
	@Override
	public Mail getResponseMail(Message message) {
		if (message.response > 0) {
			return new HeaderedMail("Defend Protectoriat", "The " + message.receiver.getLinkedName() + " have followed through\ntheir vow and will\nbe joining our war!" ,message.sender, message.receiver);
		} else {
			return new HeaderedMail("Vow broken!", "The disgraceful " + message.receiver.getLinkedName() + " have broken their\nvow to defend us!" ,message.sender, message.receiver);
		}
	}
}
