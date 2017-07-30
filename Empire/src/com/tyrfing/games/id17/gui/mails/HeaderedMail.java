package com.tyrfing.games.id17.gui.mails;

import com.tyrfing.games.id17.gui.DefaultItemListEntry;
import com.tyrfing.games.id17.gui.PaperButton;
import com.tyrfing.games.id17.gui.TabGUI;
import com.tyrfing.games.id17.houses.House;
import com.tyrlib2.graphics.renderables.FormattedText2.ALIGNMENT;
import com.tyrlib2.gui.IEventListener;
import com.tyrlib2.gui.ImageBox;
import com.tyrlib2.gui.ItemList;
import com.tyrlib2.gui.Label;
import com.tyrlib2.gui.ScaledVector2;
import com.tyrlib2.gui.Window;
import com.tyrlib2.gui.WindowEvent;
import com.tyrlib2.gui.WindowEvent.WindowEventType;
import com.tyrlib2.gui.WindowManager;
import com.tyrlib2.input.InputManager;
import com.tyrlib2.util.Color;

public class HeaderedMail extends Mail {

	public static final ScaledVector2 HEADER_POS = new ScaledVector2(0.01f, 0.02f, 1);
	public static final ScaledVector2 HEADER_SIZE = new ScaledVector2(Mail.SIZE.x * 0.95f, Mail.SIZE.y * 0.3f, 1);
	
	public static final ScaledVector2 PARTIES_POS = new ScaledVector2(HEADER_SIZE.x * 0.5f, HEADER_SIZE.y * 0.2f, 1);
	
	public static final ScaledVector2 SIGIL_ATTACKER_POS = new ScaledVector2(0.015f, 0.02f, 1);
	public static final ScaledVector2 SIGIL_DEFENDER_POS = new ScaledVector2(HEADER_SIZE.x - 0.065f, 0.02f, 1);
	public static final ScaledVector2 SIGIL_SIZE = TabGUI.SIGIL_SIZE.multiply(0.8f);
	
	public static final ScaledVector2 ATTACKER_PAPER_POS = new ScaledVector2(HEADER_POS.x, HEADER_POS.y + HEADER_SIZE.y - 0.025f, 1);
	public static final ScaledVector2 ATTACKER_PAPER_SIZE = new ScaledVector2(Mail.SIZE.x * 0.985f / 2, Mail.SIZE.y - HEADER_SIZE.y -  HEADER_POS.y + 0.025f, 1);
	
	public static final ScaledVector2 MAIN_PAPER_POS =  new ScaledVector2(ATTACKER_PAPER_POS.x/2, ATTACKER_PAPER_POS.y, 1);
	public static final ScaledVector2 MAIN_PAPER_SIZE = new ScaledVector2(2 * BattleMail.ATTACKER_PAPER_SIZE.x, BattleMail.ATTACKER_PAPER_SIZE.y, 1);
	
	public static final ScaledVector2 DEFENDER_PAPER_POS = new ScaledVector2(ATTACKER_PAPER_POS.x + ATTACKER_PAPER_SIZE.x - 0.025f * Mail.SIZE.x, ATTACKER_PAPER_POS.y, 1);
	
	public static final ScaledVector2 COLUMN_POS = new ScaledVector2(ATTACKER_PAPER_SIZE.x * 0.1f, ATTACKER_PAPER_SIZE.y * 0.1f, 1);
	public static final ScaledVector2 COLUMN_SIZE = new ScaledVector2(ATTACKER_PAPER_SIZE.x * 0.8f, ATTACKER_PAPER_SIZE.y * 0.8f, 1);
	
	public static final ScaledVector2 LEFT_COLUMN_LABEL = new ScaledVector2(HEADER_SIZE.x * 0.15f, HEADER_SIZE.y * 0.6f, 1);
	public static final ScaledVector2 RIGHT_COLUMN_LABEL = new ScaledVector2(HEADER_SIZE.x * 0.65f, HEADER_SIZE.y * 0.6f, 1);
	
	public static final ScaledVector2 ACCEPT_SIZE = new ScaledVector2(Mail.SIZE.x * 0.35f, Mail.SIZE.y*0.15f, 0);
	public static final ScaledVector2 ACCEPT_POS = new ScaledVector2(Mail.SIZE.x/2, Mail.SIZE.y, 1);
	public static final ScaledVector2 ACCEPT_POS_OFFSET = new ScaledVector2(-ACCEPT_SIZE.x / 2, -2*ACCEPT_SIZE.y / 4, 0);
	public static final ScaledVector2 ACCEPT_BORDER_SIZE = new ScaledVector2(0.005f, 0.005f);
	
	public static final ScaledVector2 ACCEPT2_POS = new ScaledVector2(Mail.SIZE.x/3, Mail.SIZE.y, 1);
	public static final ScaledVector2 REJECT2_POS = new ScaledVector2(2*Mail.SIZE.x/3 , Mail.SIZE.y, 1);
	
	protected Window header;
	protected Window mainPaper;
	protected Window left;
	protected Window right;
	
	protected Label titleLabelRight;
	protected Label titleLabelLeft;
	
	protected ItemList rightColumn;
	protected ItemList leftColumn;
	
	protected int choiceLeft;
	protected int choiceRight;
	
	protected PaperButton accept;
	protected PaperButton reject;
	private Label mainLabel;
	
	public HeaderedMail(String name) {
		super(name);
	}
	
	public HeaderedMail(String title, String iconAtlas, String iconName) {
		super(String.valueOf(Math.random()));
		createHeaderedContent(null, null, true, title);
		createButtons();
		addAcceptButton();
		
		ImageBox icon = (ImageBox) WindowManager.getInstance().createImageBox(this.getName() + "/ICON", SIGIL_ATTACKER_POS, iconAtlas, iconName, SIGIL_SIZE);
		header.addChild(icon);
		icon.setInheritsAlpha(true);
		
		InputManager.getInstance().sort();
	}
	
	public HeaderedMail(String title, String text, House sender, House receiver) {
		super(String.valueOf(Math.random()));
		
		createHeaderedContent(sender.getSigilName(), receiver.getSigilName(), true, title);
		this.addMainLabel(text);
		createButtons();
		InputManager.getInstance().sort();
	}
	
	public HeaderedMail(String title, House sender, House receiver, boolean oneColumn) {
		super(String.valueOf(Math.random()));
		
		createHeaderedContent(sender.getSigilName(), receiver.getSigilName(), oneColumn, title);
		createButtons();
		InputManager.getInstance().sort();
	}
	
	protected void createHeaderedContent(String leftSigil, String rightSigil, boolean oneColumn, String title) {
		
		String name = this.getName();
		
		header =  WindowManager.getInstance().createImageBox(name + "/HEADER", HEADER_POS, "MAIN_GUI", "PAPER2", HEADER_SIZE);
		frame.addChild(header);
		header.setInheritsAlpha(true);
		
		Label label = (Label) WindowManager.getInstance().createLabel(name + "/BATTLEFIELD", PARTIES_POS, title);
		header.addChild(label);
		label.setInheritsAlpha(true);
		label.setColor(Color.BLACK.copy());
		label.setAlignment(ALIGNMENT.CENTER);
		
		if (leftSigil != null) {
			ImageBox sigil = (ImageBox) WindowManager.getInstance().createImageBox(name + "/SIGIL_ATTACKER", SIGIL_ATTACKER_POS, "SIGILS1",leftSigil, SIGIL_SIZE);
			header.addChild(sigil);
			sigil.setInheritsAlpha(true);
		}
		
		if (rightSigil != null) {
			ImageBox sigil = (ImageBox) WindowManager.getInstance().createImageBox(name + "/SIGIL_DEFENDER", SIGIL_DEFENDER_POS, "SIGILS1", rightSigil, SIGIL_SIZE);
			header.addChild(sigil);
			sigil.setInheritsAlpha(true);
		}
		
		if (oneColumn) {
			mainPaper = WindowManager.getInstance().createImageBox(name + "/DECLARE_WAR_RECEIVE", MAIN_PAPER_POS, "MAIN_GUI", "PAPER", MAIN_PAPER_SIZE);
			frame.addChild(mainPaper);
			mainPaper.setInheritsAlpha(true);
		} else {
			left = WindowManager.getInstance().createImageBox(name + "/LEFT_COLUMN", BattleMail.ATTACKER_PAPER_POS, "MAIN_GUI", "PAPER", BattleMail.ATTACKER_PAPER_SIZE);
			frame.addChild(left);
			left.setInheritsAlpha(true);
			
			right = WindowManager.getInstance().createImageBox(name + "/RIGHT_COLUMN", BattleMail.DEFENDER_PAPER_POS, "MAIN_GUI", "PAPER", BattleMail.ATTACKER_PAPER_SIZE);
			frame.addChild(right);
			right.setInheritsAlpha(true);
		}
	}
	
	public void addRightColumnList(String title) {
		 titleLabelRight = (Label) WindowManager.getInstance().createLabel(this.getName() + "/RIGHT_COLUMN/TITLE/LABEL", RIGHT_COLUMN_LABEL, title);
		 titleLabelRight.setColor(Color.BLACK.copy());
		 titleLabelRight.setInheritsAlpha(true);
		 header.addChild(titleLabelRight);
		 
		 int items = (int) (3 / Math.sqrt(WindowManager.getInstance().getScale(0).y));
		 rightColumn = (ItemList) WindowManager.getInstance().createItemList(this.getName() + "/RIGHT_COLUMN/ITEMLIST", COLUMN_POS, COLUMN_SIZE, 0, items);
		 rightColumn.setReceiveTouchEvents(true);
		 rightColumn.setPassTouchEventsThrough(true);
		 right.addChild(rightColumn);
	}
	
	public void addLeftColumnList(String title) {
		 titleLabelLeft = (Label) WindowManager.getInstance().createLabel(this.getName() + "/LEFT_COLUMN/TITLE/LABEL", LEFT_COLUMN_LABEL, title);
		 titleLabelLeft.setColor(Color.BLACK.copy());
		 titleLabelLeft.setInheritsAlpha(true);
		 header.addChild(titleLabelLeft);
		 
		 int items = (int) (3 / Math.sqrt(WindowManager.getInstance().getScale(0).y));
		 leftColumn = (ItemList) WindowManager.getInstance().createItemList(this.getName() + "/LEFT_COLUMN/ITEMLIST", COLUMN_POS, COLUMN_SIZE, 0, items);
		 leftColumn.setReceiveTouchEvents(true);
		 leftColumn.setPassTouchEventsThrough(true);
		 left.addChild(leftColumn);
	}
	
	public void unhighlightRightColumn() {
		for (int i = 0; i < rightColumn.getCountEntries(); ++i) {
			((DefaultItemListEntry)rightColumn.getEntry(i)).unhighlight();
		}
	}
	
	public void unhighlightLeftColumn() {
		for (int i = 0; i < leftColumn.getCountEntries(); ++i) {
			((DefaultItemListEntry)leftColumn.getEntry(i)).unhighlight();
		}
	}
	
	public void addMainLabel(String text) {
		mainLabel = (Label) WindowManager.getInstance().createLabel(this.getName() + "/MAIN_LABEL", new ScaledVector2(BattleMail.PARTIES_POS.x, MAIN_PAPER_POS.y*1.1f, 1), text);
		header.addChild(mainLabel);
		mainLabel.setInheritsAlpha(true);
		mainLabel.setColor(Color.BLACK.copy());
		mainLabel.setAlignment(ALIGNMENT.CENTER);
	}
	
	public void addAcceptButton() {
		 accept = new PaperButton(this.getName() + "/ACCEPT/BUTTON", ACCEPT_POS.get().add(ACCEPT_POS_OFFSET.get()), ACCEPT_SIZE.get(), ACCEPT_BORDER_SIZE.get().x, "Accept");
		 accept.setReceiveTouchEvents(true);
		 accept.addEventListener(WindowEventType.CONFIRMED, new IEventListener() {
			@Override
			public void onEvent(WindowEvent event) {
				onAccept();
			}
		 });
		 frame.addChild(accept);
	}
	
	public void addAcceptAndRejectButtons() {
		 accept = new PaperButton(this.getName() + "/ACCEPT/BUTTON", ACCEPT2_POS.get().add(ACCEPT_POS_OFFSET.get()), ACCEPT_SIZE.get(), ACCEPT_BORDER_SIZE.get().x, "Accept");
		 accept.setReceiveTouchEvents(true);
		 accept.addEventListener(WindowEventType.CONFIRMED, new IEventListener() {
			@Override
			public void onEvent(WindowEvent event) {
				onAccept();
			}
		 });
		 frame.addChild(accept);
		 
		 reject = new PaperButton(this.getName() + "/REJECT/BUTTON", REJECT2_POS.get().add(ACCEPT_POS_OFFSET.get()), ACCEPT_SIZE.get(), ACCEPT_BORDER_SIZE.get().x, "Refuse");
		 reject.setReceiveTouchEvents(true);
		 reject.addEventListener(WindowEventType.CONFIRMED, new IEventListener() {
			@Override
			public void onEvent(WindowEvent event) {
				onReject();
			}
		 });
		 frame.addChild(reject);
	}
	
	protected void onAccept() {}
	protected void onReject() {}
	public void selectRight(int index) {}
	public void selectLeft(int index) {}

	@Override
	public String getTooltipText() {
		if (mainLabel != null) {
			String text = mainLabel.getText();
			String sub = "";
			boolean close = false;
			int count = 0;
			for (int i = 0; (count < MailboxGUI.MAX_TOOLTIP_LENGTH || close) && i < text.length(); ++i) {
				if (text.charAt(i) == '<') {
					close = true;
				} else if (text.charAt(i) == '>')  {
					close = false;
				} else if (!close) {
					sub += text.charAt(i);
					count++;
				}
							
			}
			
			if (sub.length() < text.length()) {
				sub += "...";
			}
			
			return sub;
		} else {
			return super.getTooltipText();
		}
	}

	public void setAcceptText(String text) {
		accept.getLabel().setText(text);
	}

	public PaperButton getAcceptButton() {
		return accept;
	}
}
