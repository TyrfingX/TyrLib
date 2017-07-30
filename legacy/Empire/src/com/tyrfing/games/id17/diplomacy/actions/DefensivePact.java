package com.tyrfing.games.id17.diplomacy.actions;

import java.util.List;

import com.tyrfing.games.id17.Util;
import com.tyrfing.games.id17.diplomacy.Message;
import com.tyrfing.games.id17.gui.mails.DiploYesNoMail;
import com.tyrfing.games.id17.gui.mails.HeaderedMail;
import com.tyrfing.games.id17.gui.mails.Mail;
import com.tyrfing.games.id17.holdings.Barony;
import com.tyrfing.games.id17.houses.House;
import com.tyrfing.games.id17.houses.StatModifier;
import com.tyrfing.games.id17.world.Border.Status;
import com.tyrfing.games.id17.world.World;
import com.tyrlib2.graphics.scene.SceneManager;

public class DefensivePact extends DiploAction {

	/**
	 * 
	 */
	private static final long serialVersionUID = -8025951811921913941L;
	public final static int RELATION_BOOST = 40;
	public final static int REQUIRED_RELATION = 80;
	
	public static final int ID = 2;
	
	public DefensivePact() {
		super("Defensive Pact", ID, 1, false);
		disabledText = "FAILED TO UPDATE";
	}

	@Override
	public boolean isEnabled(House sender, House receiver) {
		return 		receiver.getRelation(sender) >= REQUIRED_RELATION 
				&& 	receiver.getHouseStat(sender, House.HAS_DEFENSIVE_PACT) == 0
				&& 	sender.getHouseStat(receiver, House.OFFERS_PROTECTION) == 0
				&& 	receiver.getHouseStat(sender, House.OFFERS_PROTECTION) == 0;
	}

	@Override
	public void execute(House sender, House receiver, int[] options) {
		establishDefensivePact(sender, receiver);
	}
	
	@Override
	public void setDisabledText(House sender, House receiver) {
		disabledText = "Obligates both parties to defend each other in war.\n\n";
		
		if (receiver.getHouseStat(sender, House.HAS_DEFENSIVE_PACT) != 1 ) {
			disabledText += Util.getFlaggedText(" (1)", receiver.getRelation(sender) >= REQUIRED_RELATION ) + " Relation > " + REQUIRED_RELATION + " (" + (int)receiver.getRelation(sender) + ") \n";
			boolean diplomat = (sender.getHouseStat(receiver, House.HAS_DIPLOMAT) == 1 || receiver.getHouseStat(sender, House.HAS_DIPLOMAT) == 1);
			disabledText += Util.getFlaggedText(" (2)", diplomat ) + " Diplomat at either court \n";
			disabledText += Util.getFlaggedText(" (3)", sender.getHouseStat(receiver, House.OFFERS_PROTECTION) == 0 && receiver.getHouseStat(sender, House.OFFERS_PROTECTION) == 0) + " Not protectoriats of each other ";
		} else {
			disabledText += " (1) In effect until diplomatic relations end \n";
			StatModifier sm = receiver.getStatModifier("Diplomat", sender);
			if (sm == null) {
				sm = sender.getStatModifier("Diplomat", receiver);
			}
			disabledText += " (2) Ends " + World.toDate(sm.timestampStart + sm.duration);
		}
	}
	
	public static void establishDefensivePact(House house1, House house2) {
		house1.addStatModifier(new StatModifier("HAS_DEFENSIVE_PACT", House.HAS_DEFENSIVE_PACT, house1, house2, -1, 1));
		house2.addStatModifier(new StatModifier("HAS_DEFENSIVE_PACT", House.HAS_DEFENSIVE_PACT, house2,  house1, -1, 1));
		
		
		if (!SceneManager.getInstance().getRenderer().isInServerMode()) {
		
			List<Barony> changeBorders = null;
			
			if (house1 == World.getInstance().getPlayerController().getHouse()) {
				changeBorders = house2.getAllBaronies();
			} else if (house2 == World.getInstance().getPlayerController().getHouse()) {
				changeBorders = house1.getAllBaronies();
			}
			
			if (changeBorders != null) {
				for (int i = 0; i < changeBorders.size(); ++i) {
					changeBorders.get(i).getWorldChunk().getBorder().setStatus(Status.ALLY);
				}
			}
		
		}
	}
	
	public static void removeDefensivePact(House house1, House house2) {
		if (house1.getHouseStat(house2, House.HAS_DEFENSIVE_PACT) == 1) {
			house1.removeStatModfifier("HAS_DEFENSIVE_PACT", house2);
			house2.removeStatModfifier("HAS_DEFENSIVE_PACT", house1);
			
			house1.updateBorders();
			house2.updateBorders();
		
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
	public Mail getResponseMail(Message message) {
		String msg = "";
		if (message.response > 0) {
			msg = "They have accepted our offer.\nThe pact will be upheld as long as\nwe maintain diplomatic relations.\n\nMay this pact lead to a glorious future!";
		} else {
			msg = "Fools! They have refused our offer.";
		}
		return new HeaderedMail("Defensive Pact", msg, message.sender, message.receiver);
	}

	@Override
	public Mail getSendMail(Message message) {
		String msg = "We have received an offer for a\ndefensive pact with\nhouse of " + message.sender.getLinkedName() + ".";
		return new DiploYesNoMail("Defensive Pact", msg, message);
	}
	
}
