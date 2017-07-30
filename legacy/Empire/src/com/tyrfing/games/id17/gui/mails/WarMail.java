package com.tyrfing.games.id17.gui.mails;

import java.util.List;

import com.tyrfing.games.id17.diplomacy.Message;
import com.tyrfing.games.id17.gui.DefaultItemListEntry;
import com.tyrfing.games.id17.holdings.Barony;
import com.tyrfing.games.id17.holdings.Holding;
import com.tyrfing.games.id17.houses.House;
import com.tyrfing.games.id17.war.War;
import com.tyrfing.games.id17.war.WarGoal;
import com.tyrfing.games.id17.war.WarJustification;
import com.tyrfing.games.id17.world.World;
import com.tyrlib2.gui.Label;
import com.tyrlib2.gui.WindowManager;
import com.tyrlib2.input.InputManager;
import com.tyrlib2.util.Color;

public class WarMail extends HeaderedMail {
	
	private War war;
	
	private WarMailType warMailType;
	
	private Message message;
	
	public enum WarMailType {
		WHITE_PEACE_SEND,
		WHITE_PEACE_RECEIVE,
		ADMIT_DEFEAT,
		DICTATE_DEMANDS,
		DECLARE_WAR_SEND,
		DECLARE_WAR_RECEIVE;
		
		public String toString() {
			switch(this) {
			case WHITE_PEACE_RECEIVE:
			case WHITE_PEACE_SEND:
				return "White Peace Offer";
			case ADMIT_DEFEAT:
				return "Defeat";
			case DICTATE_DEMANDS:
				return "Victory";
			case DECLARE_WAR_SEND:
			case DECLARE_WAR_RECEIVE:
				return "Declaration of war";
			default:
				return "";
			}
		}
	}
	
	public WarMail(WarMailType type, Message message) {
		super(String.valueOf(Math.random()));
		
		this.warMailType = type;
		this.message = message;
		this.iconName = "War";
		
		createFrameContent();
		createButtons();
		InputManager.getInstance().sort();
	}
	
	public Message getMessage() {
		return message;
	}
	
	
	private void createFrameContent() {
		war = message.sender.isEnemy(message.receiver);
		
		House attacker, defender;
		if (war != null) {
			attacker = war.attackers.get(0);
			defender = war.defenders.get(0);
		} else {
			attacker = message.sender;
			defender = message.receiver;
		}

		
		choiceRight = 0;
		choiceLeft = -1;
		
		String name = getName();
		String msg = warMailType.toString();
		
		if (warMailType != WarMailType.WHITE_PEACE_RECEIVE && warMailType != WarMailType.WHITE_PEACE_SEND && warMailType != WarMailType.DECLARE_WAR_RECEIVE) {
			
			createHeaderedContent(attacker.getSigilName(), defender.getSigilName(), false, msg);
			
			if (warMailType == WarMailType.DICTATE_DEMANDS || warMailType == WarMailType.ADMIT_DEFEAT) {
				msg = "";
				
				if (warMailType == WarMailType.DICTATE_DEMANDS && message.sender == attacker || warMailType == WarMailType.ADMIT_DEFEAT && message.sender == defender) {
					msg = war.goal != null ? war.goal.resultsAttackerOnVictory(war) : "";
				} else {
					msg = war.goal != null ? war.goal.resultsAttackerOnDefeat() : "";
					float reparations = War.getReparations(defender, attacker);
					if (defender == World.getInstance().getPlayerController().getHouse()) {
						reparations *= war.getWarContribution(World.getInstance().getPlayerController().getHouse());
					}
					msg += "- Reparations <#ff0000>" + String.valueOf((int)reparations) + "<img MAIN_GUI GOLD_ICON>";
				}
				
				Label resultsAttacker = (Label) WindowManager.getInstance().createLabel(name + "/ATTACKER_RESULTS_LABEL", BattleMail.TOTAL_LABEL_POS,msg);
				resultsAttacker.setColor(Color.BLACK.copy());
				resultsAttacker.setInheritsAlpha(true);
				left.addChild(resultsAttacker);
				
				msg = "";
				if (warMailType == WarMailType.DICTATE_DEMANDS && message.sender == defender || warMailType == WarMailType.ADMIT_DEFEAT && message.sender == attacker) {
					msg = war.goal.resultsDefenderOnVictory();
				} else {
					msg = war.goal.resultsDefenderOnDefeat(war);
				}
				
				Label resultsDefender = (Label) WindowManager.getInstance().createLabel(name + "/DEFENDER_RESULTS_LABEL", BattleMail.TOTAL_LABEL_POS,msg);
				resultsDefender.setColor(Color.BLACK.copy());
				resultsDefender.setInheritsAlpha(true);
				right.addChild(resultsDefender);
			} else if (warMailType == WarMailType.DECLARE_WAR_SEND) {
				 
				 this.addRightColumnList("War Goal");
				 this.addLeftColumnList("Justifications");
				 
				 addWarGoalBaronies(defender);
				 
				 addAcceptButton();
			}
			
		} else if (warMailType == WarMailType.DECLARE_WAR_RECEIVE) {
			
			createHeaderedContent(attacker.getName(), defender.getName(), true, msg);
			
			String text = "\"To the miserable scum,\n";
			
			if (message.options[0] > 0 && message.options[1] != WarGoal.LIBERATION) {
				Holding holding = World.getInstance().getHolding(message.options[0]);
				text += holding.getLinkedName();
				if (holding.holdingData.barony != holding) {
					text += " of " + holding.holdingData.barony.getName();
				}
			} 
			
			if (message.options[1] >= 0) {
				text += "\nis ours by the heavenly\nrights of the gods!\"";
			} else if (message.options[1] == WarGoal.LIBERATION) {
				text += "your despicable tyranny shall soon\nfind an end.\"";
			} else {
				text += "\nshall be ours for the taking!\"";
			}
			
			text += "\n\n\"Surrender or be torn apart by\nour superior military might.\"";
			
			addMainLabel(text);
		} else {
			
			createHeaderedContent(attacker.getName(), defender.getName(), true, msg);
			
			String text = "";
			if (message.response > 0) {
				text = "The enemy has accepted our\ngratious offer.\n\nThe war has ended in white peace!";
			} else {
				text = "Fools they are, they have rejected\nour kindest of peace offers.\n\nThe war shall rage on!";
			}
			
			addMainLabel(text);
		}
	}
	
	private void addWarGoalBaronies(House house) {
		 List<Barony> baronies = house.getBaronies();
		 for (int i = 0; i < baronies.size(); ++i) {
			 BaronyEntry entry = new BaronyEntry(baronies.get(i), this);
			 rightColumn.addItemListEntry(entry);
			 entry.setReceiveTouchEvents(true);
		 }
		 
		 for (int i = 0; i < house.getSubHouses().size(); ++i) {
			 addWarGoalBaronies(house.getSubHouses().get(i));
		 }
	}
	
	@Override
	protected void onAccept() {
		WarJustification j = 		choiceLeft != -1 
								? 	message.sender.getJustification(choiceLeft)
								:	WarJustification.NO_CLAIM_JUSTIFICATION;
		int[] options = { choiceRight, j.getClaim() != null ? j.claim : WarGoal.NO_REASON, choiceLeft }; 
		
		Mail mail = message.action.getExecutionMail(new Message(message.action, message.sender, message.receiver, options));
		
		if (mail != null)  {
			World.getInstance().getMainGUI().mailboxGUI.addMail(mail, true);
		}
		
		message.action.send(message.sender, message.receiver, options);
		
		remove();
	}
	
	public void selectRight(int index) {
		choiceRight = index;
		Holding holding = World.getInstance().getHolding(index);
		
		for (int i = 0; i < leftColumn.getCountEntries(); ++i) {
			leftColumn.getEntry(i).fadeOut(0, 0.2f);
		}
		
		leftColumn.clear();
		
		DefaultItemListEntry entry = new JustificationEntry(WarJustification.NO_CLAIM_JUSTIFICATION, this);
		entry.setReceiveTouchEvents(true);
		entry.highlight();
		leftColumn.addItemListEntry(entry);
		
		for (int i = 0; i < message.sender.getCountJustfications(); ++i) {
			if (message.sender.getJustification(i).isApplyable(holding)) {
				entry = new JustificationEntry(message.sender.getJustification(i), this);
				entry.setReceiveTouchEvents(true);
				leftColumn.addItemListEntry(entry);
			}
		}
		
		if (message.receiver.hasReputation("Tyrant")) {
			entry = new JustificationEntry(new WarJustification(WarGoal.LIBERATION), this);
			entry.setReceiveTouchEvents(true);
			leftColumn.addItemListEntry(entry);
		}
	}
	
	public void selectLeft(int index) {
		choiceLeft = index;
	}
	
	@Override
	public String getTooltipText() {
		switch(warMailType) {
		case WHITE_PEACE_RECEIVE:
		case WHITE_PEACE_SEND:
			return "White Peace offer by House of " + message.sender.getName() + "...";
		case ADMIT_DEFEAT:
			return "We have defeated House of " + message.sender.getName() + " in war...";
		case DICTATE_DEMANDS:
			return "We have been defeated by House of " + message.sender.getName() + "...";
		case DECLARE_WAR_SEND:
		case DECLARE_WAR_RECEIVE:
			return "House of " + message.sender.getName() + " has declared war...";
		default:
			return super.getTooltipText();
		}
		
	}
	
}
