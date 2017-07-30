package com.tyrfing.games.id17.diplomacy.actions;

import com.tyrfing.games.id17.diplomacy.Message;
import com.tyrfing.games.id17.gui.mails.Mail;
import com.tyrfing.games.id17.gui.mails.WarMail;
import com.tyrfing.games.id17.gui.mails.WarMail.WarMailType;
import com.tyrfing.games.id17.houses.House;
import com.tyrfing.games.id17.war.War;
import com.tyrfing.games.id17.world.World;

public class AdmitDefeat extends DiploAction {

	/**
	 * 
	 */
	private static final long serialVersionUID = -7933695652402401901L;
	public static final int ID = 0;
	
	public AdmitDefeat() {
		super("Admit Defeat", ID, 0, false);
	}

	@Override
	public boolean isEnabled(House sender, House receiver) {
		War war = sender.isEnemy(receiver);
		if (war != null) {
			if (sender == war.attackers.get(0) || sender == war.defenders.get(0)) {
				return true;
			}
		}
		
		return false;
	}

	@Override
	public void execute(House sender, House receiver, int[] options) {
		War war = sender.isEnemy(receiver);
		
		House player = World.getInstance().getPlayerController().getHouse();
		
		if (war.attacker != player && war.defender != player) {
			if (war.attackers.contains(player) && war.attackers.contains(sender)) {
				Mail mail = new WarMail(WarMailType.ADMIT_DEFEAT, new Message(this, sender, receiver, options));
				World.getInstance().getMainGUI().mailboxGUI.addMail(mail, false);
			} else if(war.defenders.contains(player) && war.defenders.contains(sender)) {
				Mail mail = new WarMail(WarMailType.ADMIT_DEFEAT, new Message(this, sender, receiver, options));
				World.getInstance().getMainGUI().mailboxGUI.addMail(mail, false);
			}
		}
		
		war.win(receiver);
	}
	
	@Override
	public Mail getExecutionMail(Message message) {
		return new WarMail(WarMailType.ADMIT_DEFEAT, message);
	}
	
	@Override
	public Mail getSendMail(Message message) {
		return new WarMail(WarMailType.ADMIT_DEFEAT, message);
	}

}
