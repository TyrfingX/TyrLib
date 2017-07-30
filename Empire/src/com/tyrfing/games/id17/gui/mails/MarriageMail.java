package com.tyrfing.games.id17.gui.mails;

import com.tyrfing.games.id17.diplomacy.Message;
import com.tyrfing.games.id17.diplomacy.actions.Marriage;
import com.tyrfing.games.id17.gui.PaperButton;
import com.tyrfing.games.id17.world.World;
import com.tyrlib2.graphics.renderables.FormattedText2.ALIGNMENT;
import com.tyrlib2.graphics.scene.SceneManager;
import com.tyrlib2.gui.IEventListener;
import com.tyrlib2.gui.Label;
import com.tyrlib2.gui.ScaledVector2;
import com.tyrlib2.gui.Window;
import com.tyrlib2.gui.WindowEvent;
import com.tyrlib2.gui.WindowEvent.WindowEventType;
import com.tyrlib2.gui.WindowManager;
import com.tyrlib2.input.InputManager;
import com.tyrlib2.util.Color;

public class MarriageMail extends HeaderedMail {
	
	public static final ScaledVector2 MALE_POS = new ScaledVector2(HeaderedMail.MAIN_PAPER_SIZE.x * 0.1f, HeaderedMail.MAIN_PAPER_SIZE.y * 0.1f, 1);
	public static final ScaledVector2 FEMALE_POS = new ScaledVector2(HeaderedMail.MAIN_PAPER_SIZE.x - MALE_POS.x - HeaderedMail.ACCEPT_SIZE.x, MALE_POS.y, 1);
	public static final ScaledVector2 OFFER_POS = new ScaledVector2(HeaderedMail.MAIN_PAPER_SIZE.x * 0.5f, MALE_POS.y * 3, 1);
	public static final ScaledVector2 PRICE_LABEL_POS = new ScaledVector2(OFFER_POS.x, OFFER_POS.y + MALE_POS.y * 3, 1);
	public static final ScaledVector2 PRICE_LABEL_POS_R = new ScaledVector2(OFFER_POS.x, OFFER_POS.y + MALE_POS.y, 1);
	public static final ScaledVector2 OFFER_TIME_LABEL_POS = new ScaledVector2(OFFER_POS.x, OFFER_POS.y + MALE_POS.y * 3, 1);
	
	public enum MailType {
		MARRIAGE_OFFER_SEND,
		MARRIAGE_OFFER_RECEIVED,
		MARRIAGE_OFFER_RESPONSE
	}

	private MailType type;
	private Message message;
	
	private PaperButton male;
	private PaperButton female;
	private Label offer;
	private Label priceLabel;
	private Window price;
	
	private Label offerTime;
	
	private int choice = 1;
	private int cost;
	
	private boolean responded = false;
	
	public static final String TEXT_1 = "marry a suitable candidate of your house.\"\nWe are expected to pay a dowry of\n";
	public static final String TEXT_2 = "marry a suitable candidate of your house.\"\nWe are expected to receive a dowry of\n";
	
	private int daysLastFrame;
	
	public MarriageMail(MailType type, Message message) {
		super(String.valueOf(Math.random()));
		
		this.type = type;
		this.message = message;
		this.iconName = "Marriage";
		
		createFrameContent();
		createButtons();
		InputManager.getInstance().sort();
	}
	
	private void createFrameContent() {

		if (type == MailType.MARRIAGE_OFFER_SEND) {
			responded = true;
			createHeaderedContent(message.sender.getSigilName(), message.receiver.getSigilName(), true, "Marriage Offer\n\"We propose that one of our...");
			addAcceptButton();
			
			male = new PaperButton(this.getName() + "/MALE/BUTTON", MALE_POS.get(), ACCEPT_SIZE.get(), ACCEPT_BORDER_SIZE.get().x, "<#ff0000>-1\\#   male");
			male.setReceiveTouchEvents(true);
			male.addEventListener(WindowEventType.CONFIRMED, new IEventListener() {
				@Override
				public void onEvent(WindowEvent event) {
					selectMale();
				}
			});

			Window symbol = WindowManager.getInstance().createImageBox(this.getName() + "/MALE", JustificationEntry.SIGIL_POS.get(0), "MAIN_GUI", "MALE_ICON", JustificationEntry.SIGIL_SIZE.get());
			male.addChild(symbol);

			mainPaper.addChild(male);


			female = new PaperButton(this.getName() + "/FEMALE/BUTTON", FEMALE_POS.get(), ACCEPT_SIZE.get(), ACCEPT_BORDER_SIZE.get().x, "<#ff0000>-1\\# female");
			female.setReceiveTouchEvents(true);
			female.addEventListener(WindowEventType.CONFIRMED, new IEventListener() {
				@Override
				public void onEvent(WindowEvent event) {
					selectFemale();
				}
			});
			
			
			symbol = WindowManager.getInstance().createImageBox(this.getName() + "/FEMALE", JustificationEntry.SIGIL_POS.get(0), "MAIN_GUI", "FEMALE_ICON", JustificationEntry.SIGIL_SIZE.get());
			female.addChild(symbol);
			
			mainPaper.addChild(female);

			female.highlight();

			offer = (Label) WindowManager.getInstance().createLabel(this.getName() + "/", OFFER_POS, TEXT_1);
			offer.setAlignment(ALIGNMENT.CENTER);
			offer.setColor(Color.BLACK.copy());
			mainPaper.addChild(offer);
			
			cost = Marriage.getPrice(message.sender);
			
			priceLabel = (Label) WindowManager.getInstance().createLabel(this.getName() + "/PRICE_LABEL", PRICE_LABEL_POS,  String.valueOf(cost));
			priceLabel.setAlignment(ALIGNMENT.CENTER);
			priceLabel.setColor(Color.RED.copy());
			mainPaper.addChild(priceLabel);
			
			ScaledVector2 pos = new ScaledVector2(PRICE_LABEL_POS.x*1.025f + priceLabel.getFont().glText.getLength(priceLabel.getText()) / (2 * SceneManager.getInstance().getViewportWidth()), PRICE_LABEL_POS.y, 1);
			price = WindowManager.getInstance().createImageBox(this.getName() + "/PRICE", pos, "MAIN_GUI", "GOLD_ICON", JustificationEntry.SIGIL_SIZE);
			mainPaper.addChild(price);
		} else if (type == MailType.MARRIAGE_OFFER_RESPONSE) {
			responded = true;
			createHeaderedContent(message.sender.getName(), message.receiver.getName(), true, "Marriage Offer");
			
			String msg = "";
			
			if (message.response > 0) {
				msg = "They have accepted our offer for marriage!";
			} else {
				msg = "The arrogant fools have refused our\ngods-blessed marriage offer.";
			}
			
			this.addMainLabel(msg);
		} else if (type == MailType.MARRIAGE_OFFER_RECEIVED) {
			createHeaderedContent(message.sender.getName(), message.receiver.getName(), true, "Marriage Offer from\nHouse " +  message.sender.getLinkedName());
			String msg = "\"We propose that one of our ";
			
			if (message.options[0] > 0) {
				msg += "female\n";
			} else {
				msg += "male\n";
			}
			
			msg += TEXT_2;
			
			this.addMainLabel(msg);
			
			priceLabel = (Label) WindowManager.getInstance().createLabel(this.getName() + "/PRICE_LABEL", PRICE_LABEL_POS_R,  String.valueOf(message.options[1]));
			priceLabel.setAlignment(ALIGNMENT.CENTER);
			priceLabel.setColor(new Color(0.0f, 0.5f, 0.2f, 1));
			
			mainPaper.addChild(priceLabel);

			ScaledVector2 pos = new ScaledVector2(PRICE_LABEL_POS.x*1.025f + priceLabel.getFont().glText.getLength(priceLabel.getText()) / (2 * SceneManager.getInstance().getViewportWidth()), PRICE_LABEL_POS_R.y, 1);
			price = WindowManager.getInstance().createImageBox(this.getName() + "/PRICE", pos, "MAIN_GUI", "GOLD_ICON", JustificationEntry.SIGIL_SIZE);
			mainPaper.addChild(price);
			
			offerTime = (Label) WindowManager.getInstance().createLabel(this.getName() + "/OFFER_TIME_LABEL", OFFER_TIME_LABEL_POS,  "The offer will expire in (10) days.");
			offerTime.setAlignment(ALIGNMENT.CENTER);
			offerTime.setColor(Color.BLACK.copy());
			mainPaper.addChild(offerTime);
			
			daysLastFrame = (int) ((Message.RESPONSE_TIME - ( World.getInstance().getWorldTime() - message.timeStamp)) / World.SECONDS_PER_DAY); 
			
			addAcceptAndRejectButtons();
		}

		

	}
	
	private void selectMale() {
		female.unhighlight();
		male.highlight();
		
		choice = 0;
		
		cost = Marriage.getPrice(message.sender);
		
		priceLabel.setText(String.valueOf(cost));
		offer.setText(TEXT_1);
		priceLabel.setColor(Color.RED.copy());
	}
	
	private void selectFemale() {
		male.unhighlight();
		female.highlight();
		
		choice = 1;
		
		cost = Marriage.getPrice(message.sender);
		
		priceLabel.setText(String.valueOf(cost));
		offer.setText(TEXT_1);
		priceLabel.setColor(Color.RED.copy());
	}
	
	@Override
	protected void onAccept() {
		responded = true;
		if (type == MailType.MARRIAGE_OFFER_SEND) {
			int[] options = { choice, cost }; 
			
			Mail mail = message.action.getExecutionMail(new Message(message.action, message.sender, message.receiver, options));
			
			if (mail != null)  {
				World.getInstance().getMainGUI().mailboxGUI.addMail(mail, true);
			}
			
			message.action.send(message.sender, message.receiver, options);
		} else {
			message.respond(1);
		}
		remove();
	}
	
	@Override
	protected void onReject() {
		responded = true;
		message.respond(0);
		remove();
	}
	
	@Override
	public void onUpdate(float time) {
		if (!responded) {
			float worldTime = World.getInstance().getWorldTime();
			float days = (Message.RESPONSE_TIME - (worldTime - message.timeStamp)) / World.SECONDS_PER_DAY; 
			
			if (days <= 0) {
				message.respond(0);
				this.remove();
				responded = true;
			} else {
				if ((int) days != daysLastFrame) {
					offerTime.setText("The offer will expire in (" + String.valueOf((int)days)  + ") days.");
					daysLastFrame = (int) days;
				}
			}
		}
		
		super.onUpdate(time);
	}
	
	@Override
	public String getTooltipText() {
		if (type == MailType.MARRIAGE_OFFER_RECEIVED) {
			return "Marriage request by House " + message.sender.getName();
		} else if (type == MailType.MARRIAGE_OFFER_RESPONSE) {
			if (message.response > 0) {
				return "Marriage request accepted by House " + message.receiver.getName();
			} else {
				return "Marriage request refused by House " + message.receiver.getName();
			}
		} else {
			return super.getTooltipText();
		}
	}
}
