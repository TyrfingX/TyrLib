package com.tyrfing.games.id17.gui.mails;

import com.tyrfing.games.id17.Util;
import com.tyrfing.games.id17.diplomacy.Message;
import com.tyrfing.games.id17.diplomacy.actions.SendGift;
import com.tyrlib2.graphics.renderables.FormattedText2.ALIGNMENT;
import com.tyrlib2.gui.Label;
import com.tyrlib2.gui.ScaledVector2;
import com.tyrlib2.gui.WindowManager;
import com.tyrlib2.input.InputManager;
import com.tyrlib2.util.Color;

public class GiftMail extends HeaderedMail {

	public enum STATUS{
		GIFT_RECEIVED,
		GIFT_SEND;
	}
	
	public static final ScaledVector2 BASE_TEXT_POS = new ScaledVector2(BattleMail.PARTIES_POS.x, MAIN_PAPER_POS.y, 1);
	public static final ScaledVector2 PRICE_LABEL_POS = new ScaledVector2(HeaderedMail.MAIN_PAPER_SIZE.x * 0.5f, MarriageMail.MALE_POS.y * 2, 1);
	
	private Message message;
	
	public GiftMail(STATUS status, Message message) {
		super(String.valueOf(Math.random()));
		
		if (status == STATUS.GIFT_SEND) {
		
			this.message = message;
			
			createHeaderedContent(message.sender.getSigilName(), message.receiver.getSigilName(), true, "Send Gift");
			
			int relationChange = SendGift.getRelationChange(message.sender, message.receiver);
			int cost = SendGift.getCosts(message.sender);
			
			Label label = (Label) WindowManager.getInstance().createLabel(this.getName() + "/PRICE_BASE_LAEBEL", BASE_TEXT_POS, "We would gift them gold in height of\n");
			header.addChild(label);
			label.setInheritsAlpha(true);
			label.setColor(Color.BLACK.copy());
			label.setAlignment(ALIGNMENT.CENTER);
			
			Label priceLabel = (Label) WindowManager.getInstance().createLabel(this.getName() + "/PRICE_LABEL", PRICE_LABEL_POS, Util.getFlaggedText(String.valueOf(cost), false) + "<img MAIN_GUI GOLD_ICON>");
			priceLabel.setAlignment(ALIGNMENT.CENTER);
			mainPaper.addChild(priceLabel);
			
			this.addMainLabel("\n\n\nThis gift would increase our relationship by\n<#009030>+" + String.valueOf(relationChange) + "\\# for the duration of 5 years.");
			
			addAcceptButton();
		
		} else {
			
			createHeaderedContent(message.sender.getName(), message.receiver.getName(), true, "Gift received");
			int cost = SendGift.getCosts(message.sender);
			this.addMainLabel("The house of " +  message.sender.getName() + "\nis paying us their respects by gifting us\n\n" + cost + "<img MAIN_GUI GOLD_ICON>");
		}
		
		createButtons();
		InputManager.getInstance().sort();
		
	}
	
	@Override
	protected void onAccept() {
		message.action.send(message.sender, message.receiver, null);
		remove();
	}

}
