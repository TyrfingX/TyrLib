package com.tyrfing.games.id17.diplomacy.actions;

import com.tyrfing.games.id17.Util;
import com.tyrfing.games.id17.diplomacy.Message;
import com.tyrfing.games.id17.gui.mails.Mail;
import com.tyrfing.games.id17.gui.mails.WarMail;
import com.tyrfing.games.id17.gui.mails.WarMail.WarMailType;
import com.tyrfing.games.id17.houses.House;
import com.tyrfing.games.id17.war.War;
import com.tyrfing.games.id17.world.World;

public class DictateDemands extends DiploAction {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1281358635050092612L;
	public static final int ID = 3;
	
	public DictateDemands() {
		super("Dictate Demands", ID, 0, false);
		disabledText = "FAILED TO UPDATE";
	}

	@Override
	public boolean isEnabled(House sender, House receiver) {
		War war = sender.isEnemy(receiver);
		if (war != null) {
			if (sender == war.attackers.get(0)) {
				return war.getProgress() >= 1;
			} else {
				return war.getProgress() <= -1;
			}
		}
		
		return false;
	}
	
	@Override
	public void setDisabledText(House sender, House receiver) {
		War war = sender.isEnemy(receiver);
		boolean winning = false;
		if (sender == war.attackers.get(0)) {
			winning =  war.getProgress() >= 1;
		} else {
			winning =  war.getProgress() <= -1;
		}
		disabledText = Util.getFlaggedText(" (1)", winning  ) + " War Progress = +100% ";
	}	

	@Override
	public void execute(House sender, House receiver, int[] options) {
		War war = sender.isEnemy(receiver);
		
		House player = World.getInstance().getPlayerController().getHouse();
		
		if (war.attacker != player && war.defender != player && (war.attackers.contains(player) || war.defenders.contains(player))) {
			Mail mail = new WarMail(WarMailType.DICTATE_DEMANDS, new Message(this, sender, receiver, options));
			World.getInstance().getMainGUI().mailboxGUI.addMail(mail, false);
		}
		
		war.win(sender);
	}
	
	@Override
	public Mail getExecutionMail(Message message) {
		return new WarMail(WarMailType.DICTATE_DEMANDS, message);
	}
	
	@Override
	public Mail getSendMail(Message message) {
		return new WarMail(WarMailType.DICTATE_DEMANDS, message);
	}

}
