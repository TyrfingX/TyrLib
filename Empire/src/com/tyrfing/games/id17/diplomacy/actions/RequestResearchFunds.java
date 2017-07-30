package com.tyrfing.games.id17.diplomacy.actions;

import com.tyrfing.games.id17.diplomacy.Message;
import com.tyrfing.games.id17.gui.mails.BattleMail;
import com.tyrfing.games.id17.gui.mails.DiploYesNoMail;
import com.tyrfing.games.id17.gui.mails.HeaderedMail;
import com.tyrfing.games.id17.gui.mails.Mail;
import com.tyrfing.games.id17.gui.mails.TechEntry;
import com.tyrfing.games.id17.houses.House;
import com.tyrfing.games.id17.houses.StatModifier;
import com.tyrfing.games.id17.houses.VaryingStatModifier;
import com.tyrfing.games.id17.technology.Technology;
import com.tyrfing.games.id17.technology.TechnologyProject;
import com.tyrfing.games.id17.world.World;
import com.tyrlib2.gui.Label;
import com.tyrlib2.gui.WindowManager;
import com.tyrlib2.util.Color;

public class RequestResearchFunds extends DiploAction {

	/**
	 * 
	 */
	private static final long serialVersionUID = 2171914055395578048L;
	/**
	 * OPTIONS:
	 * 0: TECH ID
	 */
	
	
	public final static int BASE_FAVOR = 40;
	public final static int HONOR_COST = 40;
	public final static float RELATION_YES = 40;
	public final static float RELATION_NO = -10;
	public final static float DURATION = 5 * World.SECONDS_PER_DAY * World.DAYS_PER_YEAR;
	
	public static final int ID = 21;
	
	public RequestResearchFunds() {
		super("Research Funds", ID, 1, true);
	}

	@Override
	public boolean isEnabled(House sender, House receiver) {
		return receiver == sender.getOverlord();
	}

	@Override
	public void execute(House sender, House receiver, int[] options) {
		Technology t = World.getInstance().techTreeSet.trees[0].techs[options[0]];
		receiver.changeGold(-t.funds);
		sender.changeGold(t.funds);
		sender.startTechnologyProject(new TechnologyProject(sender, t));
		
		receiver.changeHouseStat(sender, House.FAVOR_STAT, BASE_FAVOR);
		sender.changeHouseStat(receiver, House.FAVOR_STAT, -BASE_FAVOR);
		
		receiver.addStatModifier(new StatModifier("Funded Research", House.RELATION_STAT, receiver, sender, DURATION, RELATION_YES));
		receiver.addStatModifier(new VaryingStatModifier("Funding", House.WEALTH, receiver, -1, Technology.INTELLECTUAL_RESEARCH, Technology.INTELLECTUAL_RESEARCH_DECAY, 0));
	}
	
	@Override
	public void respond(Message message) {
		super.respond(message);
		if (message.response > 0) {
			execute(message.sender, message.receiver, message.options);
		} else {
			if (message.sender.getHouseStat(message.receiver, House.FAVOR_STAT) >= BASE_FAVOR) {
				message.receiver.changeHouseStat(message.sender, House.FAVOR_STAT, BASE_FAVOR);
				message.sender.changeHouseStat(message.receiver, House.FAVOR_STAT, -BASE_FAVOR);
				message.sender.addStatModifier(new StatModifier("Refused Funding", House.RELATION_STAT, message.sender, message.receiver, DURATION, RELATION_NO));
				message.receiver.changeHonor(-HONOR_COST);
			}
		}
	}
	
	@Override
	public Mail getSendMail(Message message) {
		Technology t = World.getInstance().techTreeSet.trees[0].techs[message.options[0]];
	
		String msg = "House " + message.sender.getLinkedName() + " requests us to fund\ntheir research.\nThey want to research\n" + t.name + "<img TECH " + t.name + "> requiring " + t.funds + "<img MAIN_GUI GOLD_ICON>.\n";
		if (message.sender.getHouseStat(message.receiver, House.FAVOR_STAT) >= BASE_FAVOR) {
			msg += "Declining would be dishonorable,\nas we owe them <#ff0000>"  + BASE_FAVOR + "\\# favor.";
		} else {
			msg += "They would owe us <#009030>" + BASE_FAVOR + "\\# favor.";
		}
		return new DiploYesNoMail("Request: Fund Research\n" +  t.name, msg,  message);
	}
	
	@Override
	public Mail getResponseMail(Message message) {
		if (message.response > 0) {
			return new HeaderedMail("Request: Fund Research\nAccepted", "House " + message.receiver.getLinkedName() + " has accepted to fund\nour research. We will begin\nthe research immediately.", message.sender, message.receiver);
		} else {
			return new HeaderedMail("Request: Fund Research\nRejected", "House " + message.receiver.getLinkedName() + " has rejected to fund\nour research.", message.sender, message.receiver);
		}
	}
	
	@Override
	public Mail getOptionMail(final House sender, final House receiver) {
		final RequestResearchFunds request = this;
		HeaderedMail mail = new HeaderedMail("Request: Fund Research", sender, receiver, false) {
			
			private int selection;
			private Label text;
			
			@Override
			public void createHeaderedContent(String leftSigil, String rightSigil, boolean oneColumn, String title) {
				super.createHeaderedContent(leftSigil, rightSigil, oneColumn, title);

				this.addRightColumnList("Technology");
				
				Technology[] techs = World.getInstance().techTreeSet.trees[0].techs;
				
				 for (int i = 0; i < techs.length; ++i) {
					 if (sender.canResearch(techs[i])) {
						 TechEntry entry = new TechEntry(i, this);
						 rightColumn.addItemListEntry(entry);
						 entry.setReceiveTouchEvents(true);
						 if (i == 0) {
							 entry.highlight();
						 }
					 }
				 }
				 
				 selection = 0;

				 text = (Label) WindowManager.getInstance().createLabel(name + "/MAIN_TEXT", BattleMail.TOTAL_LABEL_POS, "");
				 text.setColor(Color.BLACK.copy());
				 text.setInheritsAlpha(true);
				 left.addChild(text);

				 addAcceptButton();
				 updateText();

			}
			
			@Override
			public void selectRight(int index) {
				 selection = index;
				 updateText();
			}
			
			private void updateText() {
				 
				 String MSG = "";
				 
				 if (sender.getHouseStat(receiver, House.FAVOR_STAT) >= BASE_FAVOR) {
					 MSG = "- Used favor: <#009030>" + BASE_FAVOR + "\\#\n";
					 MSG += "- Declining would be\ndishonorable.";
				 } else {
					 MSG = "- Favor: <#ff0000>" + BASE_FAVOR + "\\#\n";
				 }
				 
				 text.setText(MSG);
			}
			
			@Override
			protected void onAccept() {
				int[] options = { selection };
				Message message = new Message(request, sender, receiver, options);
				message.action.send(message.sender, message.receiver, options);
				remove();
			}
		};
		
		return mail;
	}
	

}
