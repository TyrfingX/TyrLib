package com.tyrfing.games.id17.diplomacy.actions;

import com.tyrfing.games.id17.Util;
import com.tyrfing.games.id17.diplomacy.Diplomacy;
import com.tyrfing.games.id17.diplomacy.Message;
import com.tyrfing.games.id17.diplomacy.category.RelationsCategory;
import com.tyrfing.games.id17.gui.mails.Mail;
import com.tyrfing.games.id17.gui.mails.WarMail;
import com.tyrfing.games.id17.gui.mails.WarMail.WarMailType;
import com.tyrfing.games.id17.holdings.Holding;
import com.tyrfing.games.id17.houses.House;
import com.tyrfing.games.id17.houses.StatModifier;
import com.tyrfing.games.id17.houses.VaryingStatModifier;
import com.tyrfing.games.id17.war.War;
import com.tyrfing.games.id17.war.WarGoal;
import com.tyrfing.games.id17.war.WarJustification;
import com.tyrfing.games.id17.world.World;

/**
 * options:
 * 0: holdingID
 * 1: houseId
 * 2: justificationID
 * @author Media
 *
 */

public class DeclareWar extends DiploAction {

	public static final int TYRANNY = 10;
	public static final float TYRANNY_DECAY = -1f / World.DAYS_PER_YEAR;
	
	/**
	 * 
	 */
	private static final long serialVersionUID = -6999346090720022648L;
	public static final int RELATION_DECLARE_WAR_NOCLAIM = -60;
	public static final int RELATION_DECLARE_WAR_CLAIM = -40;
	public static final float RELATION_DECLARE_WAR_DURATION = 100 * World.DAYS_PER_YEAR * World.SECONDS_PER_DAY;
	public static final float REPUTATION_WARMONGERER_CLAIM = 10;
	public static final float REPUTATION_WARMONGERER_NO_CLAIM = 30;
	public static final float REPUATION_WARMONGERER_DECAY = -1.f / (World.SECONDS_PER_DAY * World.DAYS_PER_SEASON);
	public static final int ID = 1;
	
	public DeclareWar() {
		super("Declare War", ID, 0, true);
		disabledText = "FAILED TO UPDATE";
	}

	@Override
	public boolean isEnabled(House sender, House receiver) {
		return 	   sender.isIndependend() 
				&& receiver.isIndependend() 
				&& (sender.isEnemy(receiver) == null) 
				&& sender != receiver
				&& sender.getHouseStat(receiver, House.HAS_TRUCE) == 0;
	}
	
	@Override
	public void setDisabledText(House sender, House receiver) {
		if (receiver.getHouseStat(sender, House.HAS_TRUCE) == 0 ) {
			disabledText = Util.getFlaggedText(" (1)", receiver.getHouseStat(sender, House.HAS_TRUCE) == 0 ) + " Not at truce ";
		} else {
			disabledText = " (1) Truce \n";
			StatModifier sm = receiver.getStatModifier("Truce", sender);
			disabledText += " (2) Ends " + World.toDate(sm.timestampStart + sm.duration);
		}
	}	

	@Override
	public void execute(House sender, House receiver, int[] options) {
		House claimant = null;
		House forHouse = sender;
		
		Holding holding = null;
		
		int warMode = WarGoal.CONQUER_HOLDING;
		if (options[0] >= 0) {
			holding = World.getInstance().getHolding(options[0]);
		} else {
			warMode = options[0];
		}
		
		WarJustification justification;
		
		if (options[1] >= 0) {
			claimant = World.getInstance().getHouses().get(options[1]);
			justification = claimant.getJustification(options[2]);
		} else {
			justification = WarJustification.NO_CLAIM_JUSTIFICATION;
		}
		
		if (claimant == null && (warMode == WarGoal.CONQUER_HOLDING || warMode == WarGoal.NO_REASON)) {
			sender.addStatModifier(new VaryingStatModifier("Warmongering", House.TYRANNY, sender, -1, TYRANNY, TYRANNY_DECAY, 0));
		}
		
		WarGoal goal = new WarGoal(holding, forHouse, warMode);
		
		declare(sender, receiver, goal, justification);
		
	}
	
	public void declare(House sender, House receiver, WarGoal goal, WarJustification justification) {

		if (justification.getClaim() != null) {			
			receiver.addStatModifier(new StatModifier("Declared War", House.RELATION_STAT, receiver, sender, RELATION_DECLARE_WAR_DURATION, RELATION_DECLARE_WAR_CLAIM));
			receiver.addStatModifier(new StatModifier("Declared War", House.RELATION_STAT, sender, receiver, RELATION_DECLARE_WAR_DURATION, RELATION_DECLARE_WAR_CLAIM));
			sender.addStatModifier(new VaryingStatModifier("Warmongering", House.WARMONGERER, sender, -1, REPUTATION_WARMONGERER_CLAIM, REPUATION_WARMONGERER_DECAY, 0));
		} else {
			receiver.addStatModifier(new StatModifier("Declared War", House.RELATION_STAT, receiver, sender, RELATION_DECLARE_WAR_DURATION, RELATION_DECLARE_WAR_NOCLAIM));
			sender.addStatModifier(new StatModifier("Declared War", House.RELATION_STAT, sender, receiver, RELATION_DECLARE_WAR_DURATION, RELATION_DECLARE_WAR_NOCLAIM));
			sender.addStatModifier(new VaryingStatModifier("Warmongering", House.WARMONGERER, sender, -1, REPUTATION_WARMONGERER_NO_CLAIM, REPUATION_WARMONGERER_DECAY, 0));
		}
		
		int honorChange = justification.getHonor(sender, receiver);
		
		if (goal.warMode == WarGoal.LIBERATION) {
			honorChange = 100;
		}
		
		sender.changeHonor(honorChange);
		
		War war = new War(sender, receiver, goal, justification);
	}
	
	public static void declareWar(House sender, House receiver, WarGoal goal, WarJustification justification) {
		((DeclareWar)Diplomacy.getInstance().getAction(
				Diplomacy.RELATIONS_ID, 
				RelationsCategory.DECLARE_WAR)
		).declare(sender, receiver, goal, justification);
	}
	
	@Override
	public Mail getOptionMail(House sender, House receiver) {
		return new WarMail(WarMailType.DECLARE_WAR_SEND, new Message(this, sender, receiver, null));
	}
	
	@Override
	public Mail getSendMail(Message message) {
		return new WarMail(WarMailType.DECLARE_WAR_RECEIVE, new Message(this, message.sender, message.receiver, message.options));
	}

}
