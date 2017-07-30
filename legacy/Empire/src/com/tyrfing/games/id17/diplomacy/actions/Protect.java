package com.tyrfing.games.id17.diplomacy.actions;

import com.tyrfing.games.id17.Util;
import com.tyrfing.games.id17.diplomacy.Message;
import com.tyrfing.games.id17.gui.mails.HeaderedMail;
import com.tyrfing.games.id17.gui.mails.Mail;
import com.tyrfing.games.id17.houses.House;
import com.tyrfing.games.id17.houses.StatModifier;
import com.tyrfing.games.id17.world.World;

public class Protect extends DiploAction {

	/**
	 * 
	 */
	private static final long serialVersionUID = -7410557619675197673L;
	
	public static final int ID = 22;
	
	public Protect() {
		super("Protectoriat", ID, 0, false);
		disabledText = "FAILED TO UPDATE";
	}

	@Override
	public boolean isEnabled(House sender, House receiver) {
		return 	   	receiver.getAllHoldings().size() < sender.getAllHoldings().size()
				&& 	sender.getHouseStat(receiver, House.OFFERS_PROTECTION) != 1
				&& 	receiver.getHouseStat(receiver, House.IS_PROTECTED) != 1
				&&	sender.isIndependend()
				&& 	receiver.isIndependend() 
				&& 	sender.isEnemy(receiver) == null
				&&  (sender.getHouseStat(receiver, House.HAS_DIPLOMAT) == 1 || receiver.getHouseStat(sender, House.HAS_DIPLOMAT) == 1)
				&& 	receiver.isInSphereOfInfluence(sender);
	}
	
	@Override
	public void setDisabledText(House sender, House receiver) {
		disabledText = "Vow to protect them in war.\n" + Util.getFlaggedText(Util.getSignedText(House.INFLUENCE_PROTECT), true) + " Influence\n\n";
		
		if (sender.getHouseStat(receiver, House.OFFERS_PROTECTION) == 0 ) {
			boolean diplomat = (sender.getHouseStat(receiver, House.HAS_DIPLOMAT) == 1 || receiver.getHouseStat(sender, House.HAS_DIPLOMAT) == 1);
			disabledText += Util.getFlaggedText(" (1)", diplomat ) + " Diplomat at either court \n";
			disabledText += Util.getFlaggedText(" (2)", receiver.getAllHoldings().size() < sender.getAllHoldings().size() ) + " Our realm (" + sender.getAllHoldings().size() + ") is bigger (" + receiver.getAllHoldings().size() + ")\n";
			disabledText += Util.getFlaggedText(" (3)", receiver.isInSphereOfInfluence(sender) ) + " Is within our sphere of influence";
		} else {
			disabledText += " (1) In effect until diplomatic relations end \n";
			StatModifier sm = receiver.getStatModifier("Diplomat", sender);
			if (sm == null) {
				sm = sender.getStatModifier("Diplomat", receiver);
			}
			disabledText += " (2) Ends " + World.toDate(sm.timestampStart + sm.duration);
		}
	}
	
	@Override
	public void execute(House sender, House receiver, int[] options) {
		sender.addStatModifier(new StatModifier("PROTECTS", House.OFFERS_PROTECTION, sender, receiver, -1, 1));
		receiver.addStatModifier(new StatModifier("PROTECTED", House.IS_PROTECTED, receiver, -1, 1));
	}
	
	@Override
	public Mail getSendMail(Message message) {
		String msg = 	"House of " + message.sender.getLinkedName() + " declared to protect\n"
					+	"us. They vow to defend us, should a\nforeign power declare war on us.";
		return new HeaderedMail("Protection offered!", msg, message.sender, message.receiver);
	}


}
