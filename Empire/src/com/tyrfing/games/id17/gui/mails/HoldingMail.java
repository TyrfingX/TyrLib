package com.tyrfing.games.id17.gui.mails;

import java.util.List;

import com.tyrfing.games.id17.diplomacy.Message;
import com.tyrfing.games.id17.diplomacy.actions.GrantHolding;
import com.tyrfing.games.id17.diplomacy.actions.RevokeHolding;
import com.tyrfing.games.id17.holdings.Barony;
import com.tyrfing.games.id17.holdings.Holding;
import com.tyrfing.games.id17.world.World;
import com.tyrlib2.gui.Label;
import com.tyrlib2.gui.WindowManager;
import com.tyrlib2.input.InputManager;
import com.tyrlib2.util.Color;

public class HoldingMail extends HeaderedMail {

	public enum MailType {
		GRANT_HOLDING_SEND,
		GRANT_HOLDING_RECEIVED,
		GRANT_HOLDING_RESPONSE,
		REVOKE_HOLDING_SEND,
		REVOKE_HOLDING_RECEIVED,
		REVOKE_HOLDING_RESPONSE
	}
	
	private Message message;
	private MailType type;
	private Label effects;
	
	public HoldingMail(MailType type, Message message) {
		super(String.valueOf(Math.random()));
		
		this.message = message;
		this.type = type;
		
		createFrameContent();
		createButtons();
		InputManager.getInstance().sort();
	}
	
	private void createFrameContent() {
		if (type == MailType.GRANT_HOLDING_SEND) {
			createHeaderedContent(message.sender.getSigilName(), message.receiver.getSigilName(), false, "Grant holding");

			addRightColumnList("Holdings");

			List<Holding> holdings = message.sender.getHoldings();
			for (int i = 0; i < holdings.size(); ++i) {
				HoldingEntry entry = new HoldingEntry(holdings.get(i), this);
				rightColumn.addItemListEntry(entry);
				entry.setReceiveTouchEvents(true);
			}

			effects = (Label) WindowManager.getInstance().createLabel(this.getName() + "/EFFECTS", BattleMail.TOTAL_LABEL_POS, "");
			left.addChild(effects);
			effects.setInheritsAlpha(true);
			effects.setColor(Color.BLACK.copy());

			addAcceptButton();
		} else if (type == MailType.REVOKE_HOLDING_SEND){
			createHeaderedContent(message.sender.getName(), message.receiver.getName(), false, "Revoke holding");

			addRightColumnList("Holdings");

			List<Holding> holdings = message.receiver.getHoldings();
			for (int i = 0; i < holdings.size(); ++i) {
				HoldingEntry entry = new HoldingEntry(holdings.get(i), this);
				rightColumn.addItemListEntry(entry);
				entry.setReceiveTouchEvents(true);
			}

			effects = (Label) WindowManager.getInstance().createLabel(this.getName() + "/EFFECTS", BattleMail.TOTAL_LABEL_POS, "");
			left.addChild(effects);
			effects.setInheritsAlpha(true);
			effects.setColor(Color.BLACK.copy());

			addAcceptButton();
		} else if (type == MailType.GRANT_HOLDING_RESPONSE){
			createHeaderedContent(message.sender.getName(), message.receiver.getName(), true, "Grant holding");
			
			String msg = "";
			if (message.response > 0) {
				msg = "\"We humbly accept your\nkindest of offers.\"\n\n\"May your days be blessed.\"";
			} else {
				msg = "\"We are truly thankful for\nyour kind offer, but we must\nregrettfully inform you that we cannot\naccept this gift.\"";
			}
			
			this.addMainLabel(msg);
		} else if (type == MailType.REVOKE_HOLDING_RESPONSE){
			createHeaderedContent(message.sender.getName(), message.receiver.getName(), true, "Revoke holding");
			
			String msg = "";
			msg = "\"May your days be miserable.\nWe have no other choice but to comply.\"";
			
			
			this.addMainLabel(msg);
		} else if (type == MailType.REVOKE_HOLDING_RECEIVED){
			createHeaderedContent(message.sender.getName(), message.receiver.getName(), true, "Revoke holding");
			
			String msg = "";
			msg = "\"We are truly, truly sorry to inform you\nthat the holding " + World.getInstance().getHolding(message.options[0]).getLinkedName() + " shall";
			msg += "\nstand henceforth under our protection.\"\n";
			msg += "\nAs we do not possess armies of our own\nwe have no choice but to comply.";
			
			this.addMainLabel(msg);
			message.respond(0);
		}
		
		
	}
	
	@Override
	protected void onAccept() {
		if (type == MailType.GRANT_HOLDING_SEND || type == MailType.REVOKE_HOLDING_SEND) {
			int[] options = { choiceRight }; 
			
			Mail mail = message.action.getExecutionMail(new Message(message.action, message.sender, message.receiver, options));
			
			if (mail != null)  {
				World.getInstance().getMainGUI().mailboxGUI.addMail(mail, true);
			}
			
			message.action.send(message.sender, message.receiver, options);
		} else { 
			message.respond(0);
		}
		
		remove();
	}

	@Override
	public void selectRight(int index) {
		
		choiceRight = index;
		Holding holding = World.getInstance().getHolding(index);
		
		if (type == MailType.GRANT_HOLDING_SEND) {
			int change = 0;
			
			if (holding instanceof Barony) {
				change = GrantHolding.RELATION_GRANT_BARONY;
			} else {
				change = GrantHolding.RELATION_GRANT_SUB_HOLDING;
			}

			String text = "- Opinion: <#008030>+" + change  + "\\#";
			
			effects.setText(text);
		} else if (type == MailType.REVOKE_HOLDING_SEND) {
			int change = 0;
			
			if (holding instanceof Barony) {
				change = RevokeHolding.RELATION_REVOKE_BARONY;
			} else {
				change = RevokeHolding.RELATION_REVOKE_SUB_HOLDING;
			}
			
			String text = "- Opinion: <#ff0000>" + change  + "\\#\n";
			text += "- Tyranny: <#ff0000>" + -RevokeHolding.TYRANNY + "\\# in\n";
			text += "   all branch families\n";
			
			if (message.receiver.getBaronies().size() > 0) {
				text += "- " + message.receiver.getLinkedName() + " might rebell";
			} 
			
			effects.setText(text);
		}
	}
}
