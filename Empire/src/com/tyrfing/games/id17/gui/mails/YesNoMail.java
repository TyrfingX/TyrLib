package com.tyrfing.games.id17.gui.mails;

import com.tyrfing.games.id17.diplomacy.Message;
import com.tyrfing.games.id17.houses.House;
import com.tyrfing.games.id17.world.World;
import com.tyrlib2.graphics.renderables.FormattedText2.ALIGNMENT;
import com.tyrlib2.gui.Label;
import com.tyrlib2.gui.WindowManager;
import com.tyrlib2.input.InputManager;
import com.tyrlib2.util.Color;

public class YesNoMail extends HeaderedMail {
	
	protected boolean responded = false;
	private Label offerTime;
	private float timeStamp;
	
	protected House sender;
	protected House receiver;
	
	public YesNoMail(String title, String text, House sender, House receiver, float timeStamp) {
		super(String.valueOf(Math.random()));
		
		this.timeStamp = timeStamp;
		this.sender  = sender;
		this.receiver = receiver;
		
		createHeaderedContent(sender.getSigilName(), receiver.getSigilName(), true, title);
		this.addMainLabel(text);
		
		offerTime = (Label) WindowManager.getInstance().createLabel(this.getName() + "/OFFER_TIME_LABEL", MarriageMail.OFFER_TIME_LABEL_POS,  "The offer will expire in (10) days.");
		offerTime.setAlignment(ALIGNMENT.CENTER);
		offerTime.setColor(Color.BLACK.copy());
		mainPaper.addChild(offerTime);
		
		
		this.addAcceptAndRejectButtons();
		createButtons();
		InputManager.getInstance().sort();
	}
	
	@Override
	public void onUpdate(float time) {
		if (!responded) {
			float worldTime = World.getInstance().getWorldTime();
			float days = (Message.RESPONSE_TIME - (worldTime - timeStamp)) / World.SECONDS_PER_DAY; 
			
			if (days <= 0) {
				onReject();
			} else {
				offerTime.setText("Expires in (" + String.valueOf((int)days)  + ") days.");
			}
		}
		
		super.onUpdate(time);
	}
}
