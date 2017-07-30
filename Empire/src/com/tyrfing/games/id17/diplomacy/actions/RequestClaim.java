package com.tyrfing.games.id17.diplomacy.actions;

import com.tyrfing.games.id17.diplomacy.Diplomacy;
import com.tyrfing.games.id17.diplomacy.Message;
import com.tyrfing.games.id17.diplomacy.category.HoldingsCategory;
import com.tyrfing.games.id17.diplomacy.category.RelationsCategory;
import com.tyrfing.games.id17.gui.mails.BattleMail;
import com.tyrfing.games.id17.gui.mails.DiploYesNoMail;
import com.tyrfing.games.id17.gui.mails.HeaderedMail;
import com.tyrfing.games.id17.gui.mails.HoldingEntry;
import com.tyrfing.games.id17.gui.mails.Mail;
import com.tyrfing.games.id17.holdings.Barony;
import com.tyrfing.games.id17.holdings.Holding;
import com.tyrfing.games.id17.houses.House;
import com.tyrfing.games.id17.world.World;
import com.tyrlib2.gui.Label;
import com.tyrlib2.gui.WindowManager;
import com.tyrlib2.util.Color;

public class RequestClaim extends DiploAction {

	/**
	 * 
	 */
	private static final long serialVersionUID = -988811138940531175L;
	public final static int ID = 16;
	public final static int BASE_FAVOR = 30;
	public final static int HONOR_COST = 80;
	
	public RequestClaim() {
		super("Push Claim", ID, 1, true);
	}

	@Override
	public boolean isEnabled(House sender, House receiver) {
		return 		sender.isSubjectOf(receiver) 
				&& 	sender.getCountJustfications() > 0;
	}

	@Override
	public void execute(House sender, House receiver, int[] options) {
		Holding holding = World.getInstance().getHolding(options[0]);
		
		if (holding.getOwner() == receiver) return;
		
		int favor = getFavorCost(holding, sender, receiver);
		receiver.changeHouseStat(sender, House.FAVOR_STAT, favor);
		sender.changeHouseStat(receiver, House.FAVOR_STAT, -favor);
		if (holding.getOwner().haveSameOverlordWith(sender)) {
			if (holding.getOwner() != receiver) {
				Diplomacy.getInstance().getAction(Diplomacy.HOLDINGS_ID, HoldingsCategory.REVOKE).execute(receiver, holding.getOwner(), options);
			}
			Diplomacy.getInstance().getAction(Diplomacy.HOLDINGS_ID, HoldingsCategory.GRANT).execute(receiver, holding.getOwner(), options);
		} else {
			int[] warOptions = { options[0], sender.id };
			Diplomacy.getInstance().getAction(Diplomacy.RELATIONS_ID, RelationsCategory.DECLARE_WAR).execute(receiver, holding.getOwner(), warOptions);
		}
		
	}
	
	@Override
	public Mail getOptionMail(final House sender, final House receiver) {
		final RequestClaim request = this;
		HeaderedMail mail = new HeaderedMail("Request: Push Claim", sender, receiver, false) {
			
			private int selection;
			private Label text;
			
			@Override
			public void createHeaderedContent(String leftSigil, String rightSigil, boolean oneColumn, String title) {
				super.createHeaderedContent(leftSigil, rightSigil, oneColumn, title);

				this.addRightColumnList("Claims");
				
				 for (int i = 0; i < sender.getCountJustfications(); ++i) {
					 if (sender.getJustification(i).getHolding() != null) {
						 HoldingEntry entry = new HoldingEntry(sender.getJustification(i).getHolding(), this);
						 rightColumn.addItemListEntry(entry);
						 entry.setReceiveTouchEvents(true);
						 if (i == 0) {
							 entry.highlight();
							 selection = sender.getJustification(i).holding;
						 }
					 }
				 }
				 
				 if (rightColumn.getCountEntries() > 0) {
					 selection = sender.getJustification(0).holding;
	
					 text = (Label) WindowManager.getInstance().createLabel(name + "/MAIN_TEXT", BattleMail.TOTAL_LABEL_POS, "");
					 text.setColor(Color.BLACK.copy());
					 text.setInheritsAlpha(true);
					 left.addChild(text);
	
					 addAcceptButton();
					 
					 updateText(sender.getJustification(0).getHolding());
				 }

			}
			
			@Override
			public void selectRight(int index) {
				 selection = index;
				 updateText(World.getInstance().getHolding(index));
			}
			
			private void updateText(Holding holding) {
				 House owner = holding.getOwner();
				 
				 int favor = getFavorCost(holding, sender, receiver);
				 
				 String MSG = "- Needed favor: <#ff0000>" + favor + "\\#\n";
				 
				 if (owner.haveSameOverlordWith(sender)) {
					 MSG += "- Would be revoked\n  from " +  owner.getLinkedName();
				 } else {
					 MSG += "- Would declare war\n  on the " + owner.getSupremeOverlord().getLinkedName() + "\n";
				 }
				 
				 MSG += "- Declining would be\ndishonorable.";
				 
				 text.setText(MSG);
				 
				 if (favor > sender.getHouseStat(receiver, House.FAVOR_STAT)) {
					 accept.disable();
				 } else {
					 accept.enable();
				 }
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
	
	public static int getFavorCost(Holding holding, House sender, House receiver){
		 int favor = BASE_FAVOR;
		 if (holding.getOwner() == receiver) {
			 favor *= 4;
		 } 
		 
		 if (holding instanceof Barony) {
			 favor *= 2;
		 }
		 
		 return favor;
	}
	
	@Override
	public void respond(Message message) {
		super.respond(message);
		if (message.response > 0) {
			execute(message.sender, message.receiver, message.options);
		} else {
			message.receiver.changeHonor(-HONOR_COST);
		}
	}
	
	@Override
	public Mail getSendMail(Message message) {
		Holding holding = World.getInstance().getHolding(message.options[0]);
		int favor = getFavorCost(holding, message.sender, message.receiver);
		return new DiploYesNoMail("Request: Push Claim", "House " + message.sender.getLinkedName() + " requests us to push\ntheir claim. They have a claim on\n" + holding.getLinkedName() + " of House\n" + holding.getOwner().getLinkedName() +  ". They would owe us <#009030>" + favor + "\\# favor." , message);
	}
	
	@Override
	public Mail getResponseMail(Message message) {
		if (message.response > 0) {
			return new HeaderedMail("Request: Push Claim\nAccepted", "House " + message.receiver.getLinkedName() + " has accepted to push\nour claim.", message.sender, message.receiver);
		} else {
			return new HeaderedMail("Request: Push Claim\nRejected", "House " + message.receiver.getLinkedName() + " has rejected to push\nour claim.", message.sender, message.receiver);
		}
	}

}
